package com.fzu.crowdsense.canalHandler;

import com.fzu.crowdsense.canal.AbstractCanalHandler;
import com.fzu.crowdsense.canal.CanalTable;
import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.model.entity.TaskSubmit;
import com.fzu.crowdsense.service.MessageService;
import com.fzu.crowdsense.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 监听 TaskSubmit 表
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-05-02
 **/
@CanalTable("task_submit")
@Component
@Slf4j
public class TaskSubmitHandler extends AbstractCanalHandler<TaskSubmit> {

    @Resource
    private MessageService messageService;


    @Resource
    private TaskService taskService;


    /**
     * 处理新增数据
     *
     * @param data data
     */
    @Override
    public void insert(TaskSubmit data) {
        // 通过taskId找到发布者id
        Task task = taskService.getById(data.getTaskId());

        // 发送数据审核通知给发布者
        String content = "亲爱的发布者：\n\t您好！您所发布的任务 (" + task.getTitle() + ") 有新的提交数据，请尽快审核";
        messageService.addMessage(task.getPublisherId(), data.getId(), 2, "数据审核通知", content);

    }

    /**
     * 处理更新数据
     *
     * @param oldData oldData
     * @param newData newData
     */
    @Override
    public void update(TaskSubmit oldData, TaskSubmit newData) {
        if (Objects.equals(oldData.getStatus(), newData.getStatus())) {
            return;
        }
        Task task = taskService.getById(newData.getTaskId());
        if (task == null) {
            return;
        }

        String content = null;
        if (newData.getStatus() == 1) {
            // 数据审核通过通知
            content = "亲爱的提交者：\n\t您好！感谢您为任务 (" + task.getTitle() + ") 所做的贡献，非常高兴的通知您，您所提交的数据已经通过审核！";

        } else if (newData.getStatus() == 2) {
            // 数据审核失败通知
            content = "亲爱的提交者：\n\t您好！感谢您为任务 (" + task.getTitle() + ") 所做的贡献，但是非常遗憾，您所提交的数据未能通过审核，原因如下\n" + newData.getReason();

        }
        if (content != null) {
            messageService.addMessage(task.getPublisherId(), newData.getId(), 3, "任务提交通知", content);
        }

    }

    @Override
    public void delete(TaskSubmit data) {

    }


}
