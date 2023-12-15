package com.fzu.crowdsense.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.mapper.FeedbackMapper;
import com.fzu.crowdsense.model.entity.Feedback;
import com.fzu.crowdsense.service.FeedbackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.fzu.crowdsense.common.ErrorCode.*;


/**
 * <p>
 * 预留的一张表，目前只设置提交反馈，也就是新增记录功能。 服务实现类
 * </p>
 *
 * @author wms
 * @since 2023-03-11
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {
    @Resource
    private FeedbackMapper feedbackMapper;

    @Override
    public BaseResponse<ErrorCode> addCollection(String advice, String contactDetails, Long feedbackUserId) {
        if (advice == null || contactDetails == null || feedbackUserId == null){
            return ResultUtils.error(PARAMS_ERROR);
        }
        //实现add功能
        Feedback feedback = new Feedback();

        LocalDateTime localDateTime = LocalDateTime.now();
        feedback.setAdvice(advice);
        feedback.setContactDetails(contactDetails);
        feedback.setFeedbackUserId(feedbackUserId);
        feedback.setCreateTime(localDateTime);
        feedback.setUpdateTime(localDateTime);

        boolean saveResult = this.save(feedback);
        if (saveResult){
            return ResultUtils.success(SUCCESS);
        }else{
            return ResultUtils.error(SYSTEM_ERROR);
        }
    }

    @Override
    public BaseResponse updateFeedBackById(Long id, String advice, String contactDetails) {
        //判断为空
        if (id == null){
            return ResultUtils.error(PARAMS_ERROR);
        }
        //判断id是否合法
        if (id <= 0) {
            return ResultUtils.error(PARAMS_ERROR);
        }
        //判断id是否存在
        if (this.getById(id) == null){
            return ResultUtils.error(PARAMS_ERROR);
        }

        UpdateWrapper<Feedback> updateWrapper = new UpdateWrapper<>();
        if (advice != null){
            updateWrapper.lambda().set(Feedback::getAdvice,advice);
        }
        if (contactDetails != null){
            updateWrapper.lambda().set(Feedback::getContactDetails,contactDetails);
        }
        updateWrapper.lambda().eq(Feedback::getId,id);//判断依据
        feedbackMapper.update(null,updateWrapper);
        return ResultUtils.success(SUCCESS);
    }
}
