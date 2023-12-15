package com.fzu.crowdsense.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.service.TaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.fzu.crowdsense.constant.TaskConstant.*;

@Component
public class ScheduledTasks {

    @Resource
    private TaskService taskService;

    /**
     * 下线超过时间的任务
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void checkTasks() {
        Date now = new Date();
        LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Task::getOnlineStatus,ONLINE);
        lambdaQueryWrapper.eq(Task::getSubmitStatus,UN_COMPLETED);

        List<Task> tasks = taskService.list(lambdaQueryWrapper);

        for (Task task : tasks) {
            if (task.getEndTime() == null){
                throw new BusinessException(ErrorCode.NULL_ERROR,"EndTime is null");
            } else{
                if (now.compareTo(task.getEndTime()) > 0) {
                    task.setOnlineStatus(OFFLINE);
                    task.setSubmitStatus(TIMEOUT);
                    taskService.updateById(task);
                    System.out.println("执行一段操作");
                }
            }
        }
    }
}
