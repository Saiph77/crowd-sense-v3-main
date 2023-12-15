package com.fzu.crowdsense.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzu.crowdsense.common.PageBean;
import com.fzu.crowdsense.model.dto.MessageDTO;
import com.fzu.crowdsense.model.entity.Message;

/**
 * Message interface
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-05-02
 **/
public interface MessageService extends IService<Message> {

    /**
     * 添加消息
     *
     * @param userId  用户id
     * @param tId     任务Id或者任务提交Id
     * @param type    消息类型  0: tId 为 任务Id   1: tId 为 任务提交Id，且可编辑(发布者的数据审核通知)   2: tId 为 任务提交Id，仅查看(提交者的数据审核成功/失败通知)
     * @param title   标题
     * @param content 内容
     */
    void addMessage(Long userId, Long tId, Integer type, String title, String content);


    /**
     * 更新单个用户的消息状态
     *
     * @param id         用户id
     * @param messageIds 消息id
     */
    void updateMessageStatus(Long id, Integer... messageIds);

    /**
     * 删除单个用户的部分消息
     *
     * @param id         用户id
     * @param messageIds 消息id
     */
    void deleteMessage(Long id, Integer... messageIds);

    /**
     * 根据用户id(和type)获取该用户的信息
     *
     * @param userId    用户id
     * @param type      类型
     * @param timestamp 时间戳
     * @param pageNum   页数
     * @param pageSize  一页的信息数
     * @return PageBean
     */
    PageBean<MessageDTO> getMessageListByIdAndType(Long userId, Integer type, Long timestamp, Integer pageNum, Integer pageSize);
}
