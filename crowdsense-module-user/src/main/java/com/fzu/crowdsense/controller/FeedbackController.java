package com.fzu.crowdsense.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.constant.QueryPageParam;
import com.fzu.crowdsense.model.entity.Feedback;
import com.fzu.crowdsense.model.request.feedback.AddFeedBackRequest;
import com.fzu.crowdsense.model.request.feedback.SelectFeedBackRequest;
import com.fzu.crowdsense.model.request.feedback.UpdateFeedBackById;
import com.fzu.crowdsense.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.fzu.crowdsense.common.ErrorCode.NULL_ERROR;
import static com.fzu.crowdsense.common.ErrorCode.PARAMS_ERROR;


/**
 * <p>
 * 预留的一张表，目前只设置提交反馈，也就是新增记录功能。 前端控制器
 * </p>
 *
 * @author fzu.crowdsense
 * @since 2023-03-11
 */
@Api
@RestController
@RequestMapping("/feedback")
public class FeedbackController {
//很奇妙，这里竟然apipost跑不通，但是postman跑的通，不理解


    @Autowired
    private FeedbackService feedbackService;

    //增
    @ApiOperation("增")
    @PostMapping("/add")
    @ApiImplicitParam(name = "AddFeedBackRequest", type = "body", dataTypeClass = AddFeedBackRequest.class, required = true)
    public BaseResponse<ErrorCode> addFeedBack(@RequestBody AddFeedBackRequest addFeedBackRequest) {
        if (addFeedBackRequest == null) {
            return ResultUtils.error(NULL_ERROR);
        }
        String advice = addFeedBackRequest.getAdvice();
        String contactDetails = addFeedBackRequest.getContactDetails();
        Long feedbackUserId = Long.valueOf((String) StpUtil.getLoginId());

        BaseResponse result = feedbackService.addCollection(advice, contactDetails, feedbackUserId);

        return result;
    }

    //删
    @ApiOperation("删")
    @PostMapping("/delete")
    @ApiImplicitParam(name = "id", dataTypeClass = Integer.class, required = true)
    public BaseResponse<Boolean> deleteFeedBackById(Long id) {
        if (id <= 0) {
            return ResultUtils.error(PARAMS_ERROR);
        }
        return ResultUtils.success(feedbackService.removeById(id));
    }

    //改
    @ApiOperation("更新")
    @PostMapping("/update")
    @ApiImplicitParam(name = "UpdateFeedBackById", type = "body", dataTypeClass = UpdateFeedBackById.class, required = true)
    public BaseResponse updateFeedBackById(@RequestBody UpdateFeedBackById updateFeedBackById, HttpServletRequest request) {
        if (updateFeedBackById == null) {
            return ResultUtils.error(NULL_ERROR);
        }
        Long id = updateFeedBackById.getId();
        String advice = updateFeedBackById.getAdvice();
        String contactDetails = updateFeedBackById.getContactDetails();

        return feedbackService.updateFeedBackById(id, advice, contactDetails);
    }

    //查(根据advice,contactDetails,feedbackUserId)
    @PostMapping("/select")
    public BaseResponse<List<Feedback>> selectFeedBackById(@RequestBody SelectFeedBackRequest selectFeedBackRequest) {
        //判断是否为空
        if (selectFeedBackRequest == null) {
            return ResultUtils.error(NULL_ERROR);
        }

        Long id = selectFeedBackRequest.getId();
        String advice = selectFeedBackRequest.getAdvice();
        String contactDetails = selectFeedBackRequest.getContactDetails();
        Long feedbackUserId = selectFeedBackRequest.getFeedbackUserId();
        Long current = selectFeedBackRequest.getCurrent();
        Long pageSize = selectFeedBackRequest.getPageSize();
        QueryPageParam queryPageParam = new QueryPageParam();

        LambdaQueryWrapper<Feedback> lambdaQueryWrapper = new LambdaQueryWrapper();

        //根据生成时间逆序排序
        lambdaQueryWrapper.orderByDesc(Feedback::getCreateTime);
        //查询当前用户
        Long userId = Long.valueOf((String) StpUtil.getLoginId());
        lambdaQueryWrapper.eq(Feedback::getFeedbackUserId,userId);

        if (id != null) {
            lambdaQueryWrapper.like(Feedback::getId, id);
        }

        if (advice != null) {
            lambdaQueryWrapper.like(Feedback::getAdvice, advice);
        }

        if (contactDetails != null) {
            lambdaQueryWrapper.like(Feedback::getContactDetails, contactDetails);
        }

        if (feedbackUserId != null) {
            lambdaQueryWrapper.like(Feedback::getFeedbackUserId, feedbackUserId);
        }

        queryPageParam.setPageNum(current.intValue());

        queryPageParam.setPageSize(pageSize.intValue());

        Page<Feedback> page = new Page();
        page.setCurrent(queryPageParam.getPageNum());
        page.setSize(queryPageParam.getPageSize());

        IPage<Feedback> result = feedbackService.page(page, lambdaQueryWrapper);

        return ResultUtils.success(result.getRecords());
    }
}
