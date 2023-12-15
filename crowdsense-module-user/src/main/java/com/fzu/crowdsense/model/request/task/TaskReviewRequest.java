package com.fzu.crowdsense.model.request.task;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class TaskReviewRequest {

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 0表示“待审核”，1表示“审核通过”，2表示“未通过”
     */
    private Integer checkStatus;

    private String invalidationReason;
}
