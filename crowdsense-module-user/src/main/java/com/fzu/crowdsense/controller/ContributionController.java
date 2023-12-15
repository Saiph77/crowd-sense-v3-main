package com.fzu.crowdsense.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.model.entity.TaskSubmit;
import com.fzu.crowdsense.model.vo.ContributionVO;
import com.fzu.crowdsense.service.TaskService;
import com.fzu.crowdsense.service.TaskSubmitService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@Api
@RestController
@RequestMapping("/contribution")
public class ContributionController {

    @Resource
    private TaskService taskService;

    @Resource
    private TaskSubmitService taskSubmitService;

    //增
    @GetMapping("/get")
    public BaseResponse<ContributionVO> getContribution() {
        Long userId = Long.valueOf((String) StpUtil.getLoginId());
        ContributionVO contributionVO = new ContributionVO();

        //累计发布任务数量
        LambdaQueryWrapper<Task> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Task::getPublisherId, userId);
        lambdaQueryWrapper1.eq(Task::getRootId, -1);
        List<Task> taskList1 = taskService.list(lambdaQueryWrapper1);
        contributionVO.setNumberOfPublishTask(taskList1.size());

        //获得提交记录的数量
        LambdaQueryWrapper<Task> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.eq(Task::getPublisherId, userId);
        lambdaQueryWrapper2.ne(Task::getRootId, -1);
        List<Task> taskList2 = taskService.list(lambdaQueryWrapper2);
        Long sum = taskList2.stream()
                .mapToLong(Task::getCurrentPassed)
                .sum();
        contributionVO.setNumberOfReceive(sum.intValue());

        //参与任务数量
        LambdaQueryWrapper<TaskSubmit> lambdaQueryWrapper3 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper3.eq(TaskSubmit::getSubmitterId, userId);
        List<TaskSubmit> taskSubmitList3 = taskSubmitService.list(lambdaQueryWrapper3);
        long count = taskSubmitList3.stream()
                .map(TaskSubmit::getTaskId)
                .distinct()
                .count();
        contributionVO.setNumberOfParticipateInTask((int)count);

        //记录被接受数量
        lambdaQueryWrapper3.eq(TaskSubmit::getStatus,1);
        contributionVO.setNumberOfBeReceivedTask(taskSubmitService.list(lambdaQueryWrapper3).size());

        //最常完成任务类型
        Map<String, Integer> typeCountMap = new HashMap<>();

        for (TaskSubmit taskSubmit : taskSubmitList3) {
            Long taskId = taskSubmit.getTaskId();
            Task task = taskService.getById(taskId);
            if (task != null) {
                String type = task.getType();
                typeCountMap.put(type, typeCountMap.getOrDefault(type, 0) + 1);
            }
        }

        String mostFrequentType = new String();
        if (typeCountMap.size() > 0) {
            mostFrequentType = Collections.max(typeCountMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        }else {
            mostFrequentType = "你还没有完成过任务哦";
        }

        contributionVO.setTaskType(mostFrequentType);

        //第一次发布任务的时间
        Task MostEarlyTime = taskList1.stream()
                .min(Comparator.comparing(Task::getCreateTime))
                .orElse(null);

        if (MostEarlyTime == null){
            contributionVO.setMostSubmitTime("你还没有发布过任务哦");
        }else{
            contributionVO.setMostSubmitTime(MostEarlyTime.getCreateTime().toString());
        }


        //第一次完成任务的时间
        TaskSubmit MostCompleteTime = taskSubmitList3.stream()
                .min(Comparator.comparing(TaskSubmit::getCreateTime))
                .orElse(null);

        if (MostCompleteTime == null){
            contributionVO.setMostSubmitTime("你还没有完成过任务哦");
        }else{
            contributionVO.setMostCompleteTime(MostCompleteTime.getCreateTime().toString());
        }

        //最常提交任务的地点为


        return ResultUtils.success(contributionVO);
    }
}
