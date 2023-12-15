package com.fzu.crowdsense.model.request.tasksubmit;

import lombok.Data;

@Data
public class TaskSubmitUpdateRequest {

    private Long id;

    private Long taskId;

    private Long rootTaskId;

    private Long submitterId;

    private String description;

    private Double numericalValue;

    private Double longitude;

    private Double latitude;

    private String filesPath;

    private Integer status;
}
