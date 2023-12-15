package com.fzu.crowdsense.model.request.task;

import lombok.Data;

@Data
/**
 * 更改任务上下线状态
 */
public class TaskOnlineRequest {

    private Long id;

    private Integer online;
}
