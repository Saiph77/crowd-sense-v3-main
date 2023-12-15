package com.fzu.crowdsense.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.PageBean;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.model.dto.MessageDTO;
import com.fzu.crowdsense.model.dto.MessageUpdateDTO;
import com.fzu.crowdsense.service.MessageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * 消息接口
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-05-02
 **/

@RestController
@RequestMapping("/message")
public class MessageController {


    @Resource
    private MessageService messageService;


    /**
     * 获取当前用户信息列表
     *
     * @param userId    用户id
     * @param type      消息类型
     * @param timestamp 时间戳
     * @param page      页数
     * @param size      一页所呈现的信息数
     * @return list
     */
    @GetMapping("/list")
    private BaseResponse< PageBean<MessageDTO>> getUserMessageList(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "type", defaultValue = "-1") Integer type,
            @RequestParam(value = "timestamp", defaultValue = "-1") Long timestamp,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        Long id = Long.valueOf((String) StpUtil.getLoginId());
        if (!Objects.equals(userId, id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id错误");
        }

        // 如果类型指定，则只查询指定类型，如果没有指定，则查询全部
        type = (type==-1)? null:type;


        // 如果时间戳未指定，则默认使用当前时间
        timestamp = (timestamp == -1) ? System.currentTimeMillis() : timestamp;



      PageBean<MessageDTO> list =  messageService.getMessageListByIdAndType(userId,type,timestamp, page, size);

        return ResultUtils.success(list);
    }


    /**
     * 改变消息状态
     *
     * @param messageUpdateDTO messageUpdateDTO
     * @return success
     */
    @PutMapping("/read")
    private BaseResponse<String> ChangeMessageStatus(@Valid @RequestBody MessageUpdateDTO messageUpdateDTO) {
        Long id = Long.valueOf((String) StpUtil.getLoginId());
        Long userId = messageUpdateDTO.getUserId();
        if (!Objects.equals(id, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id错误");
        }
        Integer[] messageIds = messageUpdateDTO.getMessageIds();

        messageService.updateMessageStatus(id, messageIds);

        return ResultUtils.success(null);
    }

    /**
     * 删除消息
     *
     * @param messageUpdateDTO messageUpdateDTO
     * @return success
     */
    @DeleteMapping("/delete")
    private BaseResponse<String> DeleteMessage(@Valid @RequestBody MessageUpdateDTO messageUpdateDTO) {
        Long id = Long.valueOf((String) StpUtil.getLoginId());
        Long userId = messageUpdateDTO.getUserId();
        if (!Objects.equals(id, userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id错误");
        }
        Integer[] messageIds = messageUpdateDTO.getMessageIds();

        messageService.deleteMessage(id, messageIds);

        return ResultUtils.success(null);
    }

}
