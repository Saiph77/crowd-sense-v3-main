package com.fzu.crowdsense.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * Message
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-05-02
 **/
@TableName(value = "message")
@Data
public class Message {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 任务Id或者任务提交Id
     */
    private Long tId;

    /**
     * 消息类型
     * 0: tId 为 大任务Id
     * 1: tId 为 小任务Id
     * 2: tId 为 任务提交Id，且可编辑(发布者的数据审核通知)
     * 3: tId 为 任务提交Id，仅查看(提交者的数据审核成功/失败通知)
     */
    private int type;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 状态：0 未读，1 已读
     */
    private int status;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 逻辑删除
     */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
