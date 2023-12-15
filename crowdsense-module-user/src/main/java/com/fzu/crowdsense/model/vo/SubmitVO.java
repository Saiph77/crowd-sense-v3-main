package com.fzu.crowdsense.model.vo;

import com.fzu.crowdsense.model.vo.getSubmitTask.TaskSubmitDetail;
import lombok.Data;

import java.util.List;

@Data
public class SubmitVO {

    private int total;

    private List<TaskSubmitDetail> lists;

}
