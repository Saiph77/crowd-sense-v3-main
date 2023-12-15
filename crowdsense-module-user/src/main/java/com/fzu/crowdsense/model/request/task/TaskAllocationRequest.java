package com.fzu.crowdsense.model.request.task;

import com.fzu.crowdsense.common.PageRequest;
import lombok.Data;

@Data
public class TaskAllocationRequest extends PageRequest {
    //用户id
    private Long userId;

}