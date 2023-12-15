package com.fzu.crowdsense.algorithms.algo;

import com.fzu.crowdsense.algorithms.adapter.AlgoFactoryAdapter;
import com.fzu.crowdsense.algorithms.constraint.Coordinate;
import com.fzu.crowdsense.algorithms.resource.ComplexTask;
import com.fzu.crowdsense.algorithms.resource.Participant;
import com.fzu.crowdsense.algorithms.resource.ParticipantPool;
import com.fzu.crowdsense.algorithms.resource.SimpleTask;
import com.fzu.crowdsense.algorithms.service.TaskAssignmentAlgo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;


@Slf4j
@Component
public class T_RandomFactory extends AlgoFactoryAdapter {


    //筛选条件，100km
    private final long WORK_TASK_DISTANCE_RANGE = 100000;

    //分配约束
    private final int MAX_ALLOCATION_NUM_TO_PACTICIPANT = 2;

    //注入参与者池，参与者池为全局管理类
    @Resource
    private ParticipantPool participantPool;


    /**
     * 单任务分配
     *
     * @return {@code TaskAssignmentAlgo}
     */
    @Override
    public TaskAssignmentAlgo getTaskAssignmentAlgo() {
        return new TaskAssignmentAlgo() {

            @Override
            public Optional<Map<Long, List<Participant>>> getAssignmentScheme(ComplexTask complexTask) {


                //获取小任务列表
                List<SimpleTask> complexTaskChildren = complexTask.getChildren();


                //获取所有候选者信息
                List<Participant> canditates = getCandidates(complexTask.getCoordinate());


                //任务数量和工人数量
                int taskNum = complexTaskChildren.size();
                int workerNum = canditates.size();

                //默认每个任务只需要一个参与者
                int[] p =new int[taskNum];
                for (int i =0; i < taskNum; i++){
                    p[i] = 1;
                }


                //任务-工作者距离矩阵
                double[][] distanceMatrix = new double[workerNum][taskNum];
                //任务距离矩阵
                double[][] taskDistanceMatrix = new double[taskNum][taskNum];

                //根据candidateLocations和taskLocations计算距离矩阵
                for (int i = 0; i < workerNum; i++){
                    //获取参与者位置
                    Coordinate workerLocation = canditates.get(i).getLocation();
                    for (int j = 0; j < taskNum; j++){
                        // 获取小任务位置
                        Coordinate taskLocation = complexTaskChildren.get(j).getCoordinate();

                        //计算参与者到任务的距离矩阵
                        distanceMatrix[i][j] = workerLocation.getDistance(taskLocation);

                    }
                }

                //根据taskLocations计算任务距离矩阵
                for (int i = 0; i < taskNum; i++){
                    Coordinate taskLocation = complexTaskChildren.get(i).getCoordinate();
                    for (int j = 0; j < taskNum; j++){

                        Coordinate anotherTaskLocation = complexTaskChildren.get(j).getCoordinate();
                        // 计算任务与任务之间的距离矩阵
                        taskDistanceMatrix[i][j] = taskLocation.getDistance(anotherTaskLocation);
                    }
                }

                // 获取每个任务需要的参与者数量（需要提交的数量）
                for (int i =0; i < taskNum; i++){
                    p[i] = complexTaskChildren.get(i).getMaxAllocationNum();
                }

                //创建算法实体
                T_Random t_random = new T_Random(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, p, MAX_ALLOCATION_NUM_TO_PACTICIPANT);
                t_random.taskAssign();

                //保存任务分配结果
                Map<Long, List<Participant>> assignmentScheme = new HashMap<>();

                /*
                将结果数据加入assignmentScheme,算法结果为map<SimpleParticipant,List<task>>,
                需要将结果转为map<task, List<SimpleParticipant>>
                **/
                for (Map.Entry<Long, List<Integer>> entry : t_random.getAssignMap().entrySet()){
                    Participant participant = canditates.get(Math.toIntExact(entry.getKey()));

                    for (Integer taskIndex : entry.getValue()){
                        //先获取任务id值
                        Long taskId = complexTaskChildren.get(taskIndex).getTaskId();
//                        assignmentScheme.get(taskId).add(participant);
                        // 如果反转后的map中已经有这个task作为键，就获取它对应的worker列表
                        if (assignmentScheme.containsKey (taskId)) {
                            List<Participant> workers = assignmentScheme.get (taskId);
                            // 把当前的worker加入到worker列表中
                            workers.add (participant);
                        } else {
                            // 如果反转后的map中还没有这个task作为键，就创建一个新的worker列表，并把当前的worker加入其中
                            List<Participant> workers = new ArrayList<> ();
                            workers.add (participant);
                            // 把这个新的键值对放入反转后的map中
                            assignmentScheme.put (taskId, workers);
                        }
                    }


                }
                return Optional.ofNullable(assignmentScheme);
            }

            @Override
            public Optional<List<Participant>> getAssignmentScheme(SimpleTask task) {

                List<Participant> assignmentScheme = new ArrayList<>();

                int taskNum = 1;

                try {

                    List<Participant> candidates = getCandidates(task.getCoordinate());
                    int workerNum = candidates.size();

                    double[][] distanceMatrix = getDistanceMatrix(task, candidates, taskNum);
                    for (double[] matrix : distanceMatrix) {
                        System.out.println(Arrays.toString(matrix));
                    }
                    double[][] taskDistanceMatrix = new double[][]{{1}};

                    //实例化算法实例
                    T_Random tRandom = new T_Random(workerNum, taskNum, distanceMatrix, taskDistanceMatrix, new int[]{task.getMaxAllocationNum()}, 1);
                    //调用任务分配方法，实现任务分配功能
                    tRandom.taskAssign();

                    //获取任务分配结果,遍历所有
                    Map<Long, List<Integer>> assignMapResult = tRandom.getAssignMap();
                    for (Map.Entry<Long, List<Integer>> entry : assignMapResult.entrySet()) {
                        if(entry.getValue().size() > 0) {
                            //将参与者id放入结果集中
                            assignmentScheme.add(candidates.get(Math.toIntExact(entry.getKey())));
                        }
                     }
//                    assignMapResult.keySet().forEach(index -> assignmentScheme.add(candidates.get(index)));
                } catch (Exception e) {
                    log.error("Error occurred while getting assignment scheme: {}", e.getMessage());
                    return Optional.empty();
                }



                return Optional.of(assignmentScheme);

            }

            private List<Participant> getCandidates(Coordinate taskLocation) {

                return new ArrayList<>(participantPool.getParticipantsWithinRange(taskLocation, WORK_TASK_DISTANCE_RANGE));
            }

            private double[][] getDistanceMatrix(SimpleTask task, List<Participant> candidates, int taskNum) {
                int workerNum = candidates.size();
                double[][] distanceMatrix = new double[workerNum][taskNum];
                Coordinate taskLocation = task.getCoordinate();
                for (int i = 0; i < workerNum; i++) {
                    Participant worker = candidates.get(i);
                    Coordinate workerLocation = worker.getLocation();
                    distanceMatrix[i][0] = workerLocation.getDistance(taskLocation);
                }
                return distanceMatrix;
            }

        };

    }

}
