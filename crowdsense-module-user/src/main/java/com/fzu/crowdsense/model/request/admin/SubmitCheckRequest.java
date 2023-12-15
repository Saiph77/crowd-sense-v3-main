package com.fzu.crowdsense.model.request.admin;

import lombok.Data;

/**
 * 任务审核请求
 * 审核内容：1 审核状态 2 审核描述 invalidationReason
 */
@Data
public class SubmitCheckRequest {

    private Long id;

    private Integer checkStatus;

    private String reason;
}
