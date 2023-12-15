package com.fzu.crowdsense.model.request.task;

import com.fzu.crowdsense.common.PageRequest;
import lombok.Data;

@Data
public class MyTaskQueryRequest extends PageRequest {

    /**
     * 0表示“下线”，1表示“上线”
     */
    private Integer onlineStatus;

    /**
     * 0表示“未完成”，1表示“已完成”，2表示“失效”
     */
    private Integer submitStatus;

    /**
     * 0表示“待审核”，1表示“审核通过”，2表示“未通过”
     */
    private Integer checkStatus;
}
