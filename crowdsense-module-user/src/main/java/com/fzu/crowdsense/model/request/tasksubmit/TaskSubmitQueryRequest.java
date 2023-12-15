package com.fzu.crowdsense.model.request.tasksubmit;

import com.fzu.crowdsense.common.PageRequest;
import lombok.Data;

@Data
public class TaskSubmitQueryRequest extends PageRequest {

    private Long id;

    private Long taskId;

    private Long rootTaskId;

    private Long submitterId;

    /**
     * 0表示"待审核"，1表示"已通过"，2表示"不合格"
     */
    private Integer status;

}
