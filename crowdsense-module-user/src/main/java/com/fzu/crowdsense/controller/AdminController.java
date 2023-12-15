package com.fzu.crowdsense.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzu.crowdsense.annotation.AuthCheck;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.constant.CommonConstant;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.model.entity.TaskSubmit;
import com.fzu.crowdsense.model.enums.TaskStatusEnum;
import com.fzu.crowdsense.model.request.admin.SubmitCheckRequest;
import com.fzu.crowdsense.model.request.admin.TaskCheckRequest;
import com.fzu.crowdsense.model.request.task.TaskQueryRequest;
import com.fzu.crowdsense.model.request.tasksubmit.TaskSubmitQueryRequest;
import com.fzu.crowdsense.service.TaskService;
import com.fzu.crowdsense.service.TaskSubmitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(value = "管理员接口", description = "审核任务、提交")
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Resource
    private TaskService taskService;

    @Resource
    private TaskSubmitService taskSubmitService;

    // 管理员获取未审核的任务列表
    @ApiOperation(value = "分页查询未审核的任务列表", notes = "注意传参checkStatus为0")
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list/unchecked/task/page")
    public BaseResponse<Page<Task>> listUncheckedTaskByPage(@RequestBody TaskQueryRequest taskQueryRequest,
                                                   HttpServletRequest request) {
        long current = taskQueryRequest.getCurrent();
        long size = taskQueryRequest.getPageSize();
        System.out.println("current:"+current+" size:"+size);
        // 限制爬虫
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Page<Task> taskPage = taskService.page(new Page<>(current, size), getQueryWrapper(taskQueryRequest));
        return ResultUtils.success(taskPage);
    }

    // 任务发布者 获取未审核的提交列表
    @ApiOperation(value = "分页获取本人创建任务中 未审核的提交列表", notes = "传任务id即可")
    @GetMapping("/my/task/unchecked/submit/page")
    public BaseResponse<Page<TaskSubmit>> listMyTaskSubmitByPage(TaskSubmitQueryRequest taskQueryRequest,
                                                                 HttpServletRequest request) {
        if (taskQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = Long.valueOf((String) StpUtil.getLoginId());
        taskQueryRequest.setSubmitterId(id);
        // taskQueryRequest 统一封装了查询字段的分页信息 之后直接从对象中取出即可
        long current = taskQueryRequest.getCurrent();
        long size = taskQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<TaskSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",TaskStatusEnum.UNCHECKED.getValue());

        Page<TaskSubmit> taskPage = taskSubmitService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(taskPage);
    }

    // 审核任务
    @PostMapping("/check/task")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "任务审核接口")
    public BaseResponse<Boolean> checkTask(@RequestBody TaskCheckRequest taskCheckRequest,
                                            HttpServletRequest request) {
        if (taskCheckRequest == null || taskCheckRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = taskCheckRequest.getId();
        // 判断是否存在
        Task oldTask = taskService.getById(id);
        if (oldTask == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断该任务的type是否在正常使用
//        String type = oldTask.getType();
//        if (!typeService.getAllType().getData().contains(type)){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前任务类型不可用");
//        }

        Task task = new Task();
        task.setId(id);
        task.setInvalidationReason(taskCheckRequest.getInvalidationReason());
        task.setOnlineStatus(taskCheckRequest.getCheckStatus());
        // TODO 完善审核时间
        boolean result = taskService.updateById(task);
        // TODO 消息模块通知审核情况
        return ResultUtils.success(result);
    }

    // 审核提交
    @PostMapping("/check/submit")
    @ApiOperation(value = "数据提交审核接口")
    public BaseResponse<Boolean> checkSubmit(@RequestBody SubmitCheckRequest submitCheckRequest,
                                            HttpServletRequest request) {
        if (submitCheckRequest == null || submitCheckRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = submitCheckRequest.getId();
        // 判断是否存在
        Task oldTask = taskService.getById(id);
        if (oldTask == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断该任务的type是否在正常使用
//        String type = oldTask.getType();
//        if (!typeService.getAllType().getData().contains(type)){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前任务类型不可用");
//        }

        TaskSubmit taskSubmit = new TaskSubmit();
        taskSubmit.setId(id);
        taskSubmit.setReason(submitCheckRequest.getReason());
        taskSubmit.setStatus(submitCheckRequest.getCheckStatus());
        // TODO 设置审核时间
        boolean result = taskSubmitService.updateById(taskSubmit);
        // TODO 消息模块通知审核情况
        return ResultUtils.success(result);
    }


    // TODO 代码复用 封装进service层
    private QueryWrapper<Task> getQueryWrapper(TaskQueryRequest taskQueryRequest) {
        // 检查参数是否为空
        if (taskQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 创建任务查询对象
        Task taskQuery = new Task();
        // 将请求对象的属性复制到任务查询对象中
        BeanUtils.copyProperties(taskQueryRequest, taskQuery);

        // 获取搜索关键词、排序字段和排序顺序
        String sortField = "";
        if (taskQueryRequest.getSortField() == null){
            sortField = "createTime";
        }else{
            sortField = taskQueryRequest.getSortField();
        }
        String sortOrder = taskQueryRequest.getSortOrder();
        String title = taskQuery.getTitle();
        String details = taskQuery.getDetails();

        // 清空任务查询对象的标题和详情属性，后面会单独处理模糊搜索

        // 创建查询包装器对象，并传入任务查询对象
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();

        // 设置标题模糊搜索条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        // 设置详情模糊搜索条件
        queryWrapper.like(StringUtils.isNotBlank(details), "details", details);
        // 设置排序条件
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        // 返回查询包装器对象
        return queryWrapper;
    }


}
