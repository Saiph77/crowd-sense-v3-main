package com.fzu.crowdsense.canalHandler;


import com.fzu.crowdsense.algorithms.resource.Participant;
import com.fzu.crowdsense.canal.AbstractCanalHandler;
import com.fzu.crowdsense.canal.CanalTable;
import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.service.MessageService;
import com.fzu.crowdsense.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 处理 Task表中变化的数据
 *
 * @author Zaki
 * @version 2.0
 * @since 2023-05-18
 **/
@CanalTable("task")
@Component
@Slf4j
public class TaskCanalHandler extends AbstractCanalHandler<Task> {


    @Resource
    private MessageService messageService;

    @Resource
    private TaskService taskService;

    /**
     * 处理新增的task
     *
     * @param data task
     */
    @Override
    public void insert(Task data) {
        // 如果更新的是大任务，则直接忽略
        if (data.getRootId() == -1) {
            return;
        }

        // 为小任务分配人员完成
        // 标题
        String title = "任务推荐通知";
        // 内容
        String content = "亲爱的用户：\n\t您好！我们很高兴向您推荐一项新的任务，这个任务与您完美契合，请您了解并尽快完成该任务";


        List<Participant> list = taskService.allcoationMiniTask(data.getId()).getData();
        log.info("任务({})分配请如下：===》{}", data.getId(), list);

        list.forEach(o -> messageService.addMessage(o.getId(), data.getId(), 1, title, content)
        );
    }

    /**
     * 处理task的更新数据，主要监测审核状态
     *
     * @param oldData 旧数据
     * @param newData 新数据
     */
    @Override
    public void update(Task oldData, Task newData) {

        // 如果更新的不是大任务，则直接忽略
        if (newData.getRootId() != -1) {
            return;
        }

        Long taskId = newData.getId();

        // 检查上线状态的变化
        if (!Objects.equals(oldData.getCheckStatus(), newData.getCheckStatus()) && newData.getCheckStatus() == 1 && newData.getOnlineStatus() == 1) {
            // 上线通知
            String content = "亲爱的发布者：\n\t您好！您所发布的任务 (" + newData.getTitle() + ") 已顺利通过审核并成功上线，祝您能够顺利收集到合适的数据！";
            messageService.addMessage(newData.getPublisherId(), taskId, 0, "上线通知", content);
            return;
        }

        // 检查审核状态的变化
        if (!Objects.equals(oldData.getCheckStatus(), newData.getCheckStatus()) && newData.getCheckStatus() == 2) {
            // 审核失败通知
            String content = "亲爱的发布者：\n\t您好！您所发布的任务 (" + newData.getTitle() + ") 未能通过审核，理由如下：\n\t" + newData.getInvalidationReason();
            messageService.addMessage(newData.getPublisherId(), taskId, 0, "审核结果通知", content);
            return;
        }


        // 检查是否下线
        // 如果没有下线，则直接忽略
        if (Objects.equals(oldData.getOnlineStatus(), newData.getOnlineStatus()) && newData.getOnlineStatus() == 0) {
            return;
        }

        String content = null;
        String title = null;
        // 检查任务状态
        if (!Objects.equals(oldData.getSubmitStatus(), newData.getSubmitStatus())) {
            int i = newData.getSubmitStatus();
            switch (i) {
                case 0: {
                    title = "任务下线通知";
                    content = "亲爱的发布者：\n\t您好！您所发布的任务 (" + newData.getTitle() + ")已下线";
                    break;
                }
                case 1: {
                    title = "任务完成通知";
                    content = "亲爱的发布者：\n\t您好！您所发布的任务 (" + newData.getTitle() + ")已完成";
                    break;
                }
                case 2: {
                    title = "任务失效通知";
                    content = "亲爱的发布者：\n\t您好！您所发布的任务 (" + newData.getTitle() + ")已失效";
                    break;
                }
            }
        }

        if (content != null) {
            messageService.addMessage(newData.getPublisherId(), taskId, 0, title, content);
        }


    }

    @Override
    public void delete(Task data) {

    }
}
