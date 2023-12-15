package com.fzu.crowdsense.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.model.entity.Feedback;


/**
 * <p>
 * 预留的一张表，目前只设置提交反馈，也就是新增记录功能。 服务类
 * </p>
 *
 * @author wms
 * @since 2023-03-11
 */
public interface FeedbackService extends IService<Feedback> {

    BaseResponse<ErrorCode> addCollection(String advice, String contactDetails, Long feedbackUserId);

    BaseResponse updateFeedBackById(Long id, String advice, String contactDetails);
}
