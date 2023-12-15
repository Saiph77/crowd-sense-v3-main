package com.fzu.crowdsense.algorithms.Controller;

import com.fzu.crowdsense.algorithms.algo.T_RandomFactory;
import com.fzu.crowdsense.algorithms.resource.ComplexTask;
import com.fzu.crowdsense.algorithms.resource.Participant;
import com.fzu.crowdsense.algorithms.resource.SimpleTask;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.GalobResultUtils;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.model.vo.TaskVO;
import com.fzu.crowdsense.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Lenovo
 * @version 1.0
 * @description: 任务分配Controller
 * @date 2023/5/25 16:14
 */
@RestController
@Api("任务分配接口")
@RequestMapping("/senseTask")
@Slf4j
public class AllocationController {

    @Resource
    private TaskService taskService;

    @Resource
    private T_RandomFactory t_randomFactory;

    @GetMapping("/allocation/minitask/{taskId}")
    @ApiOperation(value = "任务分配,用于分配小任务")
    public BaseResponse<List<Participant>> allcoationMiniTask(@ApiParam(value = "小任务id") @PathVariable Long taskId) {

        log.info("---------进入当前方法，taskId:" + taskId +"--------------");
        // TODO 从redis中获取数据

        // 获取任务
        Task task = taskService.getById(taskId);

        SimpleTask simpleTask = new SimpleTask(task);

        //创建算法工厂示例
        List<Participant> assignmentScheme = t_randomFactory.getTaskAssignmentAlgo().getAssignmentScheme(simpleTask)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_ALLOCATION_ERROR));

        assignmentScheme.forEach(item -> {
            log.info(item.toString());
        });

        return GalobResultUtils.success(assignmentScheme);

    }


    @GetMapping("/allocation/bigtask/{taskId}")
    @ApiOperation(value = "任务分配, 用于大任务的分配")
    public BaseResponse<Map<Long, List<Participant>>> allcoationBigTask(@ApiParam(value = "大任务id") @PathVariable Long taskId) {

        log.info("---------进入当前方法，taskId:" + taskId +"--------------");
        //TODO 判断是否是大任务

        // TODO 从redis中获取数据

        // 缓存中不存在任务数据，从数据库中获取大任务信息
        Task bigTask = taskService.getById(taskId);


        /*
        * 获取小任务集合
        * 这里获取的是大任务的所有子任务
        * TODO: 过滤掉处于已完成或者已失效的任务状态
         */
        List<TaskVO> childTaskList = taskService.getChildren(taskId);

        //创建大任务封装对象
        TaskVO taskVO = new TaskVO();
        BeanUtils.copyProperties(bigTask, taskVO);
        taskVO.setChildTask(childTaskList);

        //创建大任务实例
        ComplexTask complexTask = new ComplexTask(taskVO);


        //创建算法工厂示例
        Map<Long, List<Participant>> assignmentScheme = t_randomFactory.getTaskAssignmentAlgo().getAssignmentScheme(complexTask)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_ALLOCATION_ERROR));



        return GalobResultUtils.success(assignmentScheme);

    }


}
