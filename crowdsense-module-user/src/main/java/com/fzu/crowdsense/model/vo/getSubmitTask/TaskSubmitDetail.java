package com.fzu.crowdsense.model.vo.getSubmitTask;

import lombok.Data;

@Data
public class TaskSubmitDetail {

    private Long submitId;

    private Long submitterId;

    private String submitterName;

    private Long taskId;

    private Double taskLongitude;

    private Double taskLatitude;

    private Double submitLongitude;

    private Double submitLatitude;

    private String taskName;

    private String submitTime;

//    private Task historyTask;

    private String description;

    private Double number;

    private String filePath;

    private Integer status;

    private Long maxPassed;

    private Long currentPassed;


}
