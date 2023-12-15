package com.fzu.crowdsense.service.impl;

import com.fzu.crowdsense.entity.NewTask;
import com.fzu.crowdsense.entity.NewTaskSubmit;
import com.fzu.crowdsense.entity.v2.TaskPublish;
import com.fzu.crowdsense.entity.v2.TaskSubmit;
import com.fzu.crowdsense.service.DataMigrationService;
import com.fzu.crowdsense.service.NewTaskService;
import com.fzu.crowdsense.service.NewTaskSubmitService;
import com.fzu.crowdsense.service.v2.MatchNewService;
import com.fzu.crowdsense.service.v2.OldTaskPublishService;
import com.fzu.crowdsense.service.v2.OldTaskSubmitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


/**
 * @author Zaki
 */
@Service
public class DataMigrationServiceImpl implements DataMigrationService {

    @Resource
    private NewTaskService newTaskService;

    @Resource
    private OldTaskPublishService oldTaskPublishService;

    @Resource
    private MatchNewService matchNewService;

    @Resource
    private OldTaskSubmitService oldTaskSubmitService;

    @Resource
    private NewTaskSubmitService newTaskSubmitService;

    /**
     * 实现将crowdsense_v2--task_publish表中的数据迁移到crowdsense--task表中
     *
     * @return 迁移的数据数
     */
    @Override
    public Integer fromTaskPublishToTask() {
        // 获取crowdsense_v2--task_publish表中的所有数据
        List<TaskPublish> oldList = oldTaskPublishService.getAll();

        // 循环进行数据转换
        List<NewTask> newList = new ArrayList<>(oldList.size());

        Map<Integer, Integer> idMap = new HashMap<>();

        int id = 4000;

        for (TaskPublish taskPublish : oldList) {
            NewTask newTask = new NewTask();

            // newTask设置为默认值
            newTask.setPublisherId(1644963335902466048L);
            newTask.setOnlineStatus(0);
            newTask.setCheckStatus(1);
            newTask.setCompletedSmallTask(0L);
            newTask.setNumberOfSmallTask(0L);


            // 设置直接可以迁移的数据
            newTask.setTitle(taskPublish.getTitle());
            newTask.setDetails(taskPublish.getDetails());
            newTask.setSubmitLimit(taskPublish.getSubmitLimit());
            newTask.setMaxPassed(Long.valueOf(taskPublish.getMaxPassed()));
            newTask.setCurrentPassed(Long.valueOf(taskPublish.getCurrentPassed()));
            if (taskPublish.getIntegration() != null) {
                newTask.setIntegration(Double.valueOf(taskPublish.getIntegration()));
            }
            newTask.setStartTime(localDateTimeToDate(taskPublish.getStartTime()));
            newTask.setEndTime(localDateTimeToDate(taskPublish.getEndTime()));
            newTask.setCreateTime(localDateTimeToDate(taskPublish.getCreateTime()));
            newTask.setUpdateTime(localDateTimeToDate(taskPublish.getUpdateTime()));
            newTask.setSize(taskPublish.getSize());

            // 设置需要转换的数据
            // 转换type
            switch (taskPublish.getType()) {
                case 1:
                    newTask.setType("温度");
                    break;
                case 2:
                    newTask.setType("噪音");
                    break;
                case 3:
                    newTask.setType("空气质量");
                    break;
            }

            // 存储旧id和新id的映射
            id += 1;
            newTask.setId((long) id);

            idMap.put(id, taskPublish.getId());

            newList.add(newTask);
        }

        Map<Integer, Integer> parentMap = matchNewService.getAllParentMap();

        // 设置rootId
        List<NewTask> finalList = new ArrayList<>(oldList.size());
        for (NewTask newTask : newList) {
            // 获取旧id
            Integer oldId = idMap.get(newTask.getId().intValue());

            // 获取旧id的旧的父类id映射
            Integer oldParentId = parentMap.get(oldId);

            // 获取旧的父类id的新id
            Integer newParentId = -1;

            for (Integer key : idMap.keySet()) {
                Integer value = idMap.get(key);
                if (Objects.equals(value, oldParentId)) {
                    newParentId = key;
                    break;
                }
            }

            newTask.setRootId(Long.valueOf(newParentId));
            finalList.add(newTask);
        }

        finalList.forEach(newTask -> newTaskService.save(newTask));


        return finalList.size();
    }

    /**
     * 将crowdsense_v2--task_submit表中的数据迁移到crowdsense--task_submit表中
     *
     * @return 迁移的数据数
     */
    @Override
    public Integer taskSubmitFromOldToNew() {
        // 获取crowdsense_v2--task_submit表中所有旧的数据集合
        List<TaskSubmit> oldList = oldTaskSubmitService.getAllTaskSubmits();

        // 循环进行数据转换
        List<NewTaskSubmit> newList = new ArrayList<>(oldList.size());

        int id = 200;

        for (TaskSubmit old : oldList) {
            NewTaskSubmit newTaskSubmit = new NewTaskSubmit();

            // 设置默认值
            newTaskSubmit.setSubmitterId(1644963335902466048L);
            newTaskSubmit.setId((long) id);
            id += 1;

            newTaskSubmit.setType("空气质量");

            newTaskSubmit.setDescription(old.getTextDescription());
            if (old.getNumericalValue() != null) {
                newTaskSubmit.setNumericalValue(Double.valueOf(old.getNumericalValue()));
            }
            newTaskSubmit.setLatitude(old.getLatitude());
            newTaskSubmit.setLongitude(old.getLongitude());
            newTaskSubmit.setStatus(old.getStatus());
            if (old.getCheckTime() != null) {
                newTaskSubmit.setCheckTime(localDateTimeToDate(old.getCheckTime()));
            }
            if (old.getUpdateTime() != null) {
                newTaskSubmit.setUpdateTime(localDateTimeToDate(old.getUpdateTime()));
            }
            if (old.getCompleteTime() != null) {
                newTaskSubmit.setCompleteTime(localDateTimeToDate(old.getCompleteTime()));
            }
            newTaskSubmit.setTaskId(Long.valueOf(old.getTaskPublishId()));

            newList.add(newTaskSubmit);
        }

        // 获取crowdsense_v2--task_publish表中的所有数据
        List<TaskPublish> oldPublishList = oldTaskPublishService.getAll();

        int taskId = 4000;
        Map<Integer, Integer> idMap = new HashMap<>();
        for (TaskPublish taskPublish : oldPublishList) {
            taskId += 1;
            idMap.put(taskId, taskPublish.getId());
        }

        Map<Integer, Integer> parentMap = matchNewService.getAllParentMap();

        List<NewTaskSubmit> finalList = new ArrayList<>(oldList.size());
        for (NewTaskSubmit newTaskSubmit : newList) {
            Integer oldId = newTaskSubmit.getTaskId().intValue();
            // 获取旧id的旧的父类id映射
            Integer oldParentId = parentMap.get(oldId);

            int newParentId = -1;
            int newId = -1;
            // 获取新id
            for (Integer key : idMap.keySet()) {
                Integer value = idMap.get(key);
                if (Objects.equals(value, oldParentId)) {
                    newParentId = key;
                    break;
                } else if (Objects.equals(value, oldId)) {
                    newId = key;
                }
            }
            newTaskSubmit.setTaskId((long) newId);
            newTaskSubmit.setRootTaskId((long) newParentId);
            finalList.add(newTaskSubmit);
        }

        finalList.forEach(newTaskSubmit -> newTaskSubmitService.save(newTaskSubmit));

        return finalList.size();
    }

    public Date localDateTimeToDate(LocalDateTime localDateTime) {
        //获取系统默认时区
        ZoneId zoneId = ZoneId.systemDefault();
        //时区的日期和时间
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        //获取时刻
        return Date.from(zonedDateTime.toInstant());
    }
}
