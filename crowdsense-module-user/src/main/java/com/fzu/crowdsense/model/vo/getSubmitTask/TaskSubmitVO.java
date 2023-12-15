package com.fzu.crowdsense.model.vo.getSubmitTask;

import lombok.Data;

import java.util.List;

@Data
public class TaskSubmitVO {
    private Long numberOfSubmit;

    private List<TaskSubmitDetail> taskSubmitDetails;
}
