package com.fzu.crowdsense.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.PageBean;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.mapper.MessageMapper;
import com.fzu.crowdsense.model.dto.MessageDTO;
import com.fzu.crowdsense.model.entity.Message;
import com.fzu.crowdsense.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MessageServiceImpl
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-05-02
 **/
@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    private MessageMapper messageMapper;


    /**
     * 添加消息
     *
     * @param userId  用户id
     * @param tId     任务Id或者任务提交Id
     * @param type    消息类型  0: tId 为 任务Id   1: tId 为 任务提交Id，且可编辑(发布者的数据审核通知)   2: tId 为 任务提交Id，仅查看(提交者的数据审核成功/失败通知)
     * @param title   标题
     * @param content 内容
     */
    @Override
    public void addMessage(Long userId, Long tId, Integer type, String title, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTId(tId);
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setStatus(0);
        message.setIsDeleted(0);
        message.setCreateTime(LocalDateTime.now());

        int i = messageMapper.insert(message);
        if (i == 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "消息添加失败");
        }

    }


    /**
     * 更新用户消息状态
     *
     * @param id         用户id
     * @param messageIds 消息id ，可以是String，也可以是String[]
     */
    @Override
    public void updateMessageStatus(Long id, Integer... messageIds) {

        int i = messageMapper.update(null, new LambdaUpdateWrapper<Message>()
                .eq(Message::getUserId, id)
                .in(Message::getId, (Object[]) messageIds)
                .set(Message::getStatus, 1));

        if (i != messageIds.length) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "部分消息状态改变失败");
        }
    }

    /**
     * 删除用户消息
     *
     * @param id         用户id
     * @param messageIds 消息id
     */
    @Override
    public void deleteMessage(Long id, Integer... messageIds) {
        int i = messageMapper.deleteBatchIds(Arrays.asList(messageIds));

        if (i != messageIds.length) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "部分消息删除失败");
        }
    }

    /**
     * 实现根据用户id(和type)获取该用户的信息
     *
     * @param userId    用户id
     * @param type      类型
     * @param timestamp 时间戳
     * @param pageNum   页数
     * @param pageSize  一页的信息数
     * @return PageBean
     */
    @Override
    public PageBean<MessageDTO> getMessageListByIdAndType(Long userId, Integer type, Long timestamp, Integer pageNum, Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();

        // 如果类型指定，则只查询指定类型，如果没有指定，则查询全部
        if (type != null) {
            queryWrapper.eq(Message::getType, type);
        }
        Timestamp dateTime = new Timestamp(timestamp);

        // 构建查询语句
        queryWrapper.eq(Message::getUserId, userId)
                .orderByAsc(Message::getStatus)
                .orderByDesc(Message::getCreateTime)
                .lt(Message::getCreateTime, dateTime);


        Page<Message> messagePage = messageMapper.selectPage(page, queryWrapper);

        if (messagePage.getRecords() == null || messagePage.getRecords().size() == 0) {
            return null;
        }

        // 将message转换成messageDTO
        List<MessageDTO> messageDTOS = messagePage.getRecords().stream()
                .map(message -> BeanUtil.copyProperties(message, MessageDTO.class))
                .collect(Collectors.toList());

        // 计算未读消息数
        queryWrapper.eq(Message::getStatus, 0);
        int unreadNum = Math.toIntExact(messageMapper.selectCount(queryWrapper));

        // 将结果封装进PageBean
        PageBean<MessageDTO> result = new PageBean<>();

        result.setTotalPage((int) messagePage.getPages());
        result.setCurrentPage(pageNum);
        result.setSize(pageSize);
        result.setTimestamp(timestamp);
        result.setType(type);
        result.setTotalNum((int) messagePage.getTotal());
        result.setUnreadNum(unreadNum);
        result.setResultList(messageDTOS);

        return result;
    }


}
