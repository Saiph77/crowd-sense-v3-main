package com.fzu.crowdsense.model.vo.getPublishTask;

import lombok.Data;

import java.util.List;

@Data
public class PublisherTaskVO {
    private String publisherName;
    //总任务数
    private Long totalTaskNumber;
    //任务信息
    private List<PublisherTaskDetail> publisherTaskDetails;
}
