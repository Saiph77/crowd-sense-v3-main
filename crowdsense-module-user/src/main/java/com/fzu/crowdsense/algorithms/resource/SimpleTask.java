package com.fzu.crowdsense.algorithms.resource;


import com.fzu.crowdsense.model.entity.Task;
import lombok.ToString;
/*
 * @description: 小任务
 */
@ToString
public class SimpleTask extends AbstractTask{


    public SimpleTask(Task task) {
        super(task);
    }
}