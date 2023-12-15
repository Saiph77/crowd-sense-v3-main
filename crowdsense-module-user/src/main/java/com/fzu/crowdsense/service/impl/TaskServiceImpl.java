package com.fzu.crowdsense.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.algorithms.algo.T_RandomFactory;
import com.fzu.crowdsense.algorithms.resource.Participant;
import com.fzu.crowdsense.algorithms.resource.SimpleTask;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.GalobResultUtils;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.mapper.TaskMapper;
import com.fzu.crowdsense.mapper.UserMapper;
import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.model.entity.TaskSubmit;
import com.fzu.crowdsense.model.entity.Type;
import com.fzu.crowdsense.model.entity.User;
import com.fzu.crowdsense.model.enums.ReviewStatusEnum;
import com.fzu.crowdsense.model.vo.HistoryTasksVO;
import com.fzu.crowdsense.model.vo.TaskVO;
import com.fzu.crowdsense.service.TaskService;
import com.fzu.crowdsense.service.TaskSubmitService;
import com.fzu.crowdsense.service.TypeService;
import com.fzu.crowdsense.service.UserService;
import com.fzu.crowdsense.utils.BeanCopyUtils;
import com.fzu.crowdsense.utils.FileUtils;
import com.fzu.crowdsense.utils.RecommUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.fzu.crowdsense.common.ErrorCode.SYSTEM_ERROR;
import static com.fzu.crowdsense.constant.SystemConstants.FILE_IMAGES_SUB_PATH;
import static com.fzu.crowdsense.constant.TaskConstant.*;

/**
* @author bopeng
* @description 针对表【task】的数据库操作Service实现
* @createDate 2023-04-07 00:03:23
*/
@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{

    @Resource
    private UserService userService;

    @Resource
    private TaskSubmitService taskSubmitService;

    @Resource
    private TypeService typeService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private T_RandomFactory t_randomFactory;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //过滤半径
    private double RADIUS;

    // 任务推荐
    // 实现思路 通过stream流 先按照距离排序 再按照类型分组 组内排序 返回 Map<Integer, List<Task>>
    @Override
    public Map<Integer, List<Task>> recommendTask(User user, Double l1, Double l2) {
        // 1 获取当前用户可用的任务列表 目前是全表
        // TODO 根据用户现有地址 GPS定位 根据距离只搜索到1000m内的任务
        QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
        // TODO 统一定义枚举和常量
        taskQueryWrapper.eq("check_status", ReviewStatusEnum.PASS.getValue());
        taskQueryWrapper.eq("online_status", ONLINE);
        // TODO 过滤用户拒绝后的任务
        List<Task> tasks = list(taskQueryWrapper);
        // 2 根据用户提交记录的类型 获取用户的类型喜好 以集合形式返回
        QueryWrapper<TaskSubmit> submitQueryWrapper = new QueryWrapper<>();
        List<TaskSubmit> submitTasks = taskSubmitService.list(submitQueryWrapper);

        // type优先级列表 根据用户submit生成 task_type根据发布任务获得
        List<String> types = submitTasks.stream()
                .collect(Collectors.groupingBy(TaskSubmit::getType, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        System.out.println(types);
        System.out.println("111111111111");
        // 使用丢失精度的方法 每隔200m排一次序
        Map<Integer, List<Task>> groupedTasks = tasks.stream()
                .collect(Collectors.groupingBy(
                        task -> RecommUtil.getDistance(task.getLatitude(),task.getLongitude(),l1,l2) / 200));
        groupedTasks.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    System.out.println("Distance Group: " + entry.getKey());
                    entry.getValue().stream()
                            .sorted(Comparator.comparing(Task::getType))
                            .forEach(System.out::println);
                });
        System.out.println("succuseefully runing");
        return groupedTasks;
    }

    @Override
    public List<Task> getTaskAllocation(User user) {
        LambdaQueryWrapper<Task> taskQueryWrapper = new LambdaQueryWrapper();
        // TODO 根据用户现有地址 GPS定位 根据距离只搜索到1000m内的任务

        //任务状态限制
        taskQueryWrapper.eq(Task::getCheckStatus, ReviewStatusEnum.PASS.getValue());
        taskQueryWrapper.eq(Task::getOnlineStatus, ONLINE);
        // TODO 过滤用户拒绝后的任务

        List<Task> tasks = list(taskQueryWrapper);
        Collections.shuffle(tasks);

        return tasks;
    }

    @Override
    public List<TaskVO> getSmallTaskList(Long bigTaskId, Long pageNum, Long pageSize) {

        return null;
    }

    /**
     * 根据根任务的id查询所对应的子任务的集合
     * 不考虑任务状态
     * @param id 根任务的id
     * @return
     */
    @Override
    public List<TaskVO> getChildren(Long id) {

        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getRootId,id);
        queryWrapper.orderByAsc(Task::getCreateTime);
        List<Task> tasks = list(queryWrapper);

        List<TaskVO> taskVOs = toTaskVOList(tasks);
        return taskVOs;
    }

    private List<TaskVO> toTaskVOList(List<Task> list){

        List<TaskVO> taskVOs = BeanCopyUtils.copyBeanList(list, TaskVO.class);

        //遍历vo集合
        for (TaskVO taskVO : taskVOs) {
            // 对查出对子任务进行统一处理（相同逻辑可以用在从大任务中创建小任务？）
        }
        return taskVOs;
    }

    @Override
    public Long countByTaskId(Long taskId){
        LambdaQueryWrapper<Task> taskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        taskLambdaQueryWrapper.eq(Task::getId, taskId);
        Long count = (long)count(taskLambdaQueryWrapper);
        return count;
    }

    @Override
    public BaseResponse<List<Long>> getRandomUserIdByTaskId(Long taskId) {
        Task task = getById(taskId);
        //设置最大数量，若没设置最大提交数量，则设置为1000
        Long maxPassed = task.getMaxPassed();
        if (maxPassed == null) {
            maxPassed = 1000L;
        }

        //获得所有用户id
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        List<Long> userIds = new ArrayList<>();
        for (User user : userList) {
            userIds.add(user.getId());
        }

        // maxPassed 数量的 20%
        int randomCount = (int) Math.round(maxPassed * 0.2);

        //存储已选择的随机索引,利用set的性质快速选择出不重复的id
        Set<Integer> selectedIndexes = new HashSet<>();
        Random random = new Random();
        while (selectedIndexes.size() < randomCount && selectedIndexes.size() < userIds.size()) {
            int randomIndex = random.nextInt(userIds.size());
            if (!selectedIndexes.contains(randomIndex)) {
                selectedIndexes.add(randomIndex);
            }
        }
        //set转list
        List<Long> randomUserIds = new ArrayList<>();
        for (int index : selectedIndexes) {
            randomUserIds.add(userIds.get(index));
        }

        return ResultUtils.success(randomUserIds);
    }

    /**
     * 根据taskId获取taskVO
     */
    @Override
    public BaseResponse<TaskVO> getTaskVoByTaskId(Long taskId){
        Task task = getById(taskId);
        TaskVO taskVO = BeanCopyUtils.copyBean(task, TaskVO.class);
        User publisher = userService.getUserInfoById(task.getPublisherId());
        taskVO.setPublisherInfo(publisher);

        if (task.getRootId() == -1){
            taskVO.setChildTask(getChildren(taskId));
        }
        return ResultUtils.success(taskVO);
    }

    @Override
    public List<String> updateTaskImages(Long taskId, MultipartFile[] images) {

        Task task = getById(taskId);


        List<String> paths = new ArrayList<>();

        for (MultipartFile image : images) {
            if (StrUtil.isNotEmpty(task.getImagesPath())) {
                //获取旧文件名
                String[] oldFilePath = task.getImagesPath().split("/");
                String oldFileName = oldFilePath[oldFilePath.length - 1];
                //删除旧文件
                FileUtils.delete(FILE_IMAGES_SUB_PATH, oldFileName);
            }

            try {
                String path = FileUtils.upload(FILE_IMAGES_SUB_PATH, image);
//                //更新redis缓存
//                stringRedisTemplate.opsForValue().set(TASK_INFO + taskId, JSONUtil.toJsonStr(task));
                paths.add(path);
            } catch (IOException e) {
//                log.error("更新任务图片失败=====》{}", e.getLocalizedMessage());
                throw new BusinessException(SYSTEM_ERROR, e.getLocalizedMessage());
            }
        }

        String path = String.join(",",paths);
        task.setImagesPath(path);
        boolean i = updateById(task);
        if (!i) {
            log.error("更新任务图片失败");
            throw new BusinessException(SYSTEM_ERROR, "更新任务图片失败");
        }

        return paths;
    }



    /**
     * 推荐任务
     * 2023/5/31 新增对rootId的限制，现在只能推荐大任务
     */
    //region 推荐
    @Override
    public List<Task> getTaskRecommend(User user, long current, long pageSize) {
        // 1 获取当前用户可用的任务列表 目前是全表
        // TODO 根据用户现有地址 GPS定位 根据距离只搜索到1000m内的任务

        LambdaQueryWrapper<Task> taskQueryWrapper = new LambdaQueryWrapper();

        //任务状态限制
        taskQueryWrapper.eq(Task::getCheckStatus, ReviewStatusEnum.PASS.getValue());
        taskQueryWrapper.eq(Task::getOnlineStatus, ONLINE);
        taskQueryWrapper.eq(Task::getSubmitStatus, UN_COMPLETED);
        taskQueryWrapper.eq(Task::getRootId,-1);
        List<Task> tasks = list(taskQueryWrapper);

        // TODO 过滤， 选择范围：RADIUS
        List<Task> candidateSet = filterTasks(tasks, user, RADIUS);

        if (candidateSet.size() <= RecommendNUM) {
            //过滤后数据<= 5，直接返回
            return candidateSet;
        }

        // 获取用户历史数据


        // 获取用户的提交数据信息
        LambdaQueryWrapper<TaskSubmit> taskSubmitQueryWrapper = new LambdaQueryWrapper<>();

        taskSubmitQueryWrapper.eq(TaskSubmit::getSubmitterId, user.getId())
                .eq(TaskSubmit::getStatus, ReviewStatusEnum.PASS.getValue());//限制仅通过数据

        List<TaskSubmit> taskSubmits = taskSubmitService.list(taskSubmitQueryWrapper);


        List<HistoryTasksVO> historyTasksSet = new ArrayList<>();

        for (TaskSubmit item : taskSubmits) {
            //获取该提交对应的任务
            Task history_task = getById(item.getTaskId());

            //封装类
            HistoryTasksVO historyTasksVO = new HistoryTasksVO();
            historyTasksVO.setUserId(user.getId());
            historyTasksVO.setTaskId(history_task.getId());
            historyTasksVO.setSubmitTime(item.getCompleteTime());

            //获取任务类型对应的id值
            LambdaQueryWrapper<Type> typeQueryWrapper = new LambdaQueryWrapper();
            typeQueryWrapper.eq(Type::getType, history_task.getType());
            Type one = typeService.getOne(typeQueryWrapper);
            historyTasksVO.setType(one.getId());

            //拷贝
            BeanUtils.copyProperties(history_task, historyTasksVO);

            historyTasksVO.setDuration(history_task.getEndTime(), history_task.getStartTime());

            //加入集合
            historyTasksSet.add(historyTasksVO);
        }

        //获取用户偏好
        double[] userPreferenceVector = getUserPreferenceVector(historyTasksSet);

        // 获取推荐分数，采用余弦相似度计算兴趣偏好向量与所有任务特征向量的相似度
        Map<Long, Double> recommendationScores = getRecommendationScores(candidateSet, userPreferenceVector);

//        recommendationScores.forEach((k, v) -> System.out.println("TaskId:" + k + "  score:" + v));

        List<Long> topNRecommendations = recommendationScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

//        topNRecommendations.forEach(aLong -> System.out.println("score:"+aLong));

        //获取推荐任务id对应的任务
        List<Task> recommendedTasks = topNRecommendations.stream()
                .map(taskId -> candidateSet.stream()
                        .filter(task -> task.getId().equals(taskId))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return listToPage((int)current,(int)pageSize,recommendedTasks).getRecords();
    }


    public <T> Page<T> listToPage(int currentPage, int pageSize, List<T> list) {
        int listSize = list.size();
        int startIndex = Math.min((currentPage - 1) * pageSize, listSize);
        int endIndex = Math.min(startIndex + pageSize, listSize);
        List<T> records = list.subList(startIndex, endIndex);

        Page<T> page = new Page<>(currentPage, pageSize);
        page.setRecords(records);
        page.setTotal(listSize);

        return page;
    }

    /*
     * @description:根据历史任务获取用户的偏好
    No such property: code for class: Script1
            * @return: double
            * @author: Lenovo
            * @time: 2023/5/15 21:03
     */
//    public double[] getUserPreferenceVector(List<HistoryTasksVO> historyTasks) {
//
//        double[] preferenceVector = new double[4];
//        double integrationSum = 0;
//        for (HistoryTasksVO historyTaskVO : historyTasks) {
//            Task historyTask = historyTaskVO.getHistoryTask();
//            double integration = historyTask.getIntegration();
//            preferenceVector[0] += integration * Integer.parseInt(historyTask.getType());
//            preferenceVector[1] += integration * historyTask.getLongitude();
//            preferenceVector[2] += integration * historyTask.getLatitude();
//            preferenceVector[3] += integration * historyTaskVO.getDuration();
//            integrationSum += integration;
//        }
//        for (int i = 0; i < preferenceVector.length; i++) {
//            preferenceVector[i] /= integrationSum;
//        }
//        return preferenceVector;
//
//    }

    private double[] getUserPreferenceVector(List<HistoryTasksVO> userHistory) {
        double[] userPreferenceVector = new double[4];

        for (String column : Arrays.asList("type", "longitude", "latitude", "duration")) {
            double columnWeightedSum = userHistory.stream()
                    .mapToDouble(h -> h.getIntegration() * getColumnValue(h, column))
                    .sum();
            double integrationSum = userHistory.stream()
                    .mapToDouble(HistoryTasksVO::getIntegration)
                    .sum();
            double columnWeightedAverage = columnWeightedSum / integrationSum;
            userPreferenceVector[Arrays.asList("type", "longitude", "latitude", "duration").indexOf(column)] = columnWeightedAverage;
        }
        return userPreferenceVector;
    }

    private double getColumnValue(HistoryTasksVO historyTask, String column) {
        switch (column) {
            case "type":
                return historyTask.getType();
            case "longitude":
                return historyTask.getLongitude();
            case "latitude":
                return historyTask.getLatitude();
            case "duration":
                return historyTask.getDuration();
            default:
                throw new IllegalArgumentException("Invalid column: " + column);
        }
    }

    /*
     * @description:计算每个任务类型、地理位置和奖励积分的加权平均值，其中权重是用户的兴趣偏好向量
    No such property: code for class: Script1
            * @return: java.util.Map<java.lang.Long,java.lang.Double>
            * @author: Lenovo
            * @time: 2023/5/15 21:42
     */
    private Map<Long, Double> getRecommendationScores(List<Task> tasks, double[] userPreferenceVector) {
        Map<Long, Double> recommendationScores = new HashMap<>();
        for (Task task : tasks) {
            double typeWeighted = getWeightedAverage(tasks, "type", task.getType());

            double longitudeWeighted = task.getLongitude() * task.getIntegration();

            double latitudeWeighted = task.getLatitude() * task.getIntegration();

//            double locationWeighted = getWeightedAverage(tasks, Arrays.asList("longitude", "latitude"), Arrays.asList(task.getLongitude(), task.getLatitude()));
            double durationWeighted = (RecommUtil.getDuration(task.getEndTime(), task.getStartTime())) * task.getIntegration();


            // 计算每个任务与用户兴趣偏好向量之间的余弦相似度，并将这些相似度作为推荐分数
            double cosineSimilarity = getCosineSimilarity(new double[]{typeWeighted, longitudeWeighted, latitudeWeighted, durationWeighted}, userPreferenceVector);

            recommendationScores.put(task.getId(), cosineSimilarity);
        }
        return recommendationScores;
    }

    private double getWeightedAverage(List<Task> tasks, String column, Object value) {
        double columnWeightedSum = tasks.stream()
                .filter(t -> t.getType().equals(value))
                .mapToDouble(t -> t.getIntegration())
                .sum();
        double integrationSum = tasks.stream()
                .filter(t -> t.getType().equals(value))
                .mapToDouble(Task::getIntegration)
                .sum();
        return columnWeightedSum / integrationSum;
    }

    private double getWeightedAverage(List<Task> tasks, List<String> columns, List<Double> values) {
        double columnWeightedSum = tasks.stream()
                .filter(t -> t.getLongitude().equals(values.get(0)) && t.getLatitude().equals(values.get(1)))
                .mapToDouble(t -> t.getIntegration())
                .sum();
        double integrationSum = tasks.stream()
                .filter(t -> t.getLongitude().equals(values.get(0)) && t.getLatitude().equals(values.get(1)))
                .mapToDouble(Task::getIntegration)
                .sum();
        return columnWeightedSum / integrationSum;
    }

    // 使用余弦相似度度量向量之间的相似度
    private double getCosineSimilarity(double[] vector1, double[] vector2) {
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i], 2);
            norm2 += Math.pow(vector2[i], 2);
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public List<Task> filterTasks(List<Task> tasks, User user, double radius) {
        //过滤逻辑

        return tasks;
    }




    //endregion

    //region 分配任务
    @Override
    public BaseResponse<List<Participant>> allcoationMiniTask(Long taskId) {

        log.info("---------进入当前方法，taskId:" + taskId +"--------------");
        // TODO 从redis中获取数据

        // 获取任务
        Task task = getById(taskId);

        SimpleTask simpleTask = new SimpleTask(task);

        //创建算法工厂示例
        List<Participant> assignmentScheme = t_randomFactory.getTaskAssignmentAlgo().getAssignmentScheme(simpleTask)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_ALLOCATION_ERROR));

        assignmentScheme.forEach(item -> {
            log.info(item.toString());
        });

        return GalobResultUtils.success(assignmentScheme);

    }
    //endregion


}






