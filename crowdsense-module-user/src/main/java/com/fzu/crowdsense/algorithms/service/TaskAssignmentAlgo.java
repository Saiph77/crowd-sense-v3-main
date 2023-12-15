package com.fzu.crowdsense.algorithms.service;


import com.fzu.crowdsense.algorithms.resource.ComplexTask;
import com.fzu.crowdsense.algorithms.resource.Participant;
import com.fzu.crowdsense.algorithms.resource.SimpleTask;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.exception.BusinessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
* 任务分配类。由实现类完成任务分配过程
 */
@FunctionalInterface
public interface TaskAssignmentAlgo {
    /**
     * Given a task, return a list of workers that are assigned to the task.
     *
     * @param task The task for which you want to get the assignment scheme.
     * @return A list of participants .
     */
    Optional<List<Participant>> getAssignmentScheme(SimpleTask task);
    default Optional<Map<Long, List<Participant>>> getAssignmentScheme(ComplexTask tasks){
        Map<Long, List<Participant>> assignmentScheme = new HashMap<>();
        for(SimpleTask task : tasks.getChildren()){
//            getAssignmentScheme(task).ifPresent(item -> candidate.add(item));
            List<Participant> candidateSet = getAssignmentScheme(task).orElseThrow( () -> new BusinessException(ErrorCode.TASK_ALLOCATION_ERROR));
            assignmentScheme.getOrDefault(task.getTaskId(), candidateSet);
        }
        return Optional.ofNullable(assignmentScheme);
    };
}
