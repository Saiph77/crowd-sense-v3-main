package com.fzu.crowdsense.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * MessageDTO
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-05-02
 **/

@Data
public class MessageDTO implements Serializable {

    private static final long serialVersionUID = 3619820335938207037L;


    @JsonProperty("id")
    private int id;


    /**
     * 任务Id或者任务提交Id
     */
    @JsonProperty("tId")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tId;

    /**
     * 消息类型
     * 0: tId 为 大任务Id
     * 1: tId 为 小任务Id
     * 2: tId 为 任务提交Id，且可编辑(发布者的数据审核通知)
     * 3: tId 为 任务提交Id，仅查看(提交者的数据审核成功/失败通知)
     */
    @JsonProperty("type")
    private int type;


    /**
     * 标题
     */
    @JsonProperty("title")
    private String title;

    /**
     * 内容
     */
    @JsonProperty("content")
    private String content;

    /**
     * 状态：0 未读，1 已读
     */
    @JsonProperty("status")
    private int status;

    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
