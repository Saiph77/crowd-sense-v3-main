package com.fzu.crowdsense.model.vo.getPublishTask;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PublisherTaskDetail {

    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;

    private String title;

    private String details;

    private String imagesPath;

    /**
     *大任务中已完成小任务的数量，小任务的该项值为-1
     */
    private Long completedSmallTask;

    /**
     *包含小任务总数量，小任务的该项值为-1
     */
    private Long NumberOfSmallTask;

//    //当前通过量
//    private Long currentPassed;
//
//    //任务通过数量限制
//    private Long maxPassed;

    private Date endTime;

    private List<PublisherTaskDetail> smallTasks;

}
