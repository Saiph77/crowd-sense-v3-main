package com.fzu.crowdsense.model.vo;

import lombok.Data;

@Data
public class ContributionVO {

    //累计发布任务数量
    private Integer NumberOfPublishTask;
    //获得提交记录的数量
    private Integer NumberOfReceive;
    //参与任务数量
    private Integer NumberOfParticipateInTask;
    //记录被接受数量
    private Integer NumberOfBeReceivedTask;
    //最常完成任务类型
    private String TaskType;
    //第一次发布任务的时间
    private String MostSubmitTime;
    //第一次完成任务的时间
    private String MostCompleteTime;
    //最常提交任务的地点为

}
