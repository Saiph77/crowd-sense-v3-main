package com.fzu.crowdsense.model.request.tasksubmit;

import lombok.Data;

@Data
public class TaskSubmitAddRequest {

    private Long taskId;

    private String description;

    private Double numericalValue;

    private Double longitude;

    private Double latitude;

}
