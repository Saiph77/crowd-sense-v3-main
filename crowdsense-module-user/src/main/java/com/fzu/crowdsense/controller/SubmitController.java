package com.fzu.crowdsense.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.DeleteRequest;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.constant.CommonConstant;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.model.entity.TaskSubmit;
import com.fzu.crowdsense.model.entity.User;
import com.fzu.crowdsense.model.enums.TaskStatusEnum;
import com.fzu.crowdsense.model.request.task.TaskUpdateRequest;
import com.fzu.crowdsense.model.request.tasksubmit.TaskSubmitAddRequest;
import com.fzu.crowdsense.model.request.tasksubmit.TaskSubmitQueryRequest;
import com.fzu.crowdsense.model.request.tasksubmit.TaskSubmitUpdateRequest;
import com.fzu.crowdsense.model.vo.SubmitVO;
import com.fzu.crowdsense.model.vo.getSubmitTask.TaskSubmitDetail;
import com.fzu.crowdsense.model.vo.getSubmitTask.TaskSubmitVO;
import com.fzu.crowdsense.service.TaskService;
import com.fzu.crowdsense.service.TaskSubmitService;
import com.fzu.crowdsense.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.fzu.crowdsense.constant.TaskConstant.COMPLETED;
import static com.fzu.crowdsense.constant.TaskConstant.UN_COMPLETED;
import static com.fzu.crowdsense.utils.DateConversionUtil.convertDateToString;

/**
 * 提交信息接口
 *
 * @author https://github.com/liyupi
 */
@Api(value = "提交任务接口", description = "status 0表示\"待审核\"，1表示\"已通过\"，2表示\"不合格\"")
@RestController
@RequestMapping("/task/submit")
@Slf4j
public class SubmitController {

    @Resource
    private TaskController taskController;

    @Resource
    private TaskSubmitService taskSubmitService;

    @Resource
    private UserService userService;

    @Resource
    private TaskService taskService;

    // TODO 根据提交任务ID 查询任务信息

    // TODO service层 添加审核方法

    // TODO 导出数据列表

    // region 增删改查

    /**
     * 创建任务
     * @param taskAddRequest
     * @return
     */
    @ApiOperation(value = "添加任务提交信息")
    @PostMapping("/add")
    public BaseResponse<Long> addTaskSubmit(@RequestBody TaskSubmitAddRequest taskAddRequest,MultipartFile[] images) {
        if (taskAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TaskSubmit taskSubmit = new TaskSubmit();
        //获取用户信息
        Long userId = Long.valueOf((String) StpUtil.getLoginId());
        //获取任务信息
        Task task = taskService.getById(taskAddRequest.getTaskId());
        if (!task.getSubmitStatus().equals(UN_COMPLETED)){
            throw  new BusinessException(ErrorCode.OPERATION_ERROR,"当前任务不允许提交，可能是因为已完成或者已过期");
        }


        BeanUtils.copyProperties(taskAddRequest, taskSubmit);
        taskSubmit.setSubmitterId(userId);
        taskSubmit.setRootTaskId(task.getRootId());
        taskSubmit.setType(task.getType());
        // TODO 校验

        // 获取用户id 后填入字段信息
        taskSubmit.setStatus(TaskStatusEnum.UNCHECKED.getValue());
        // 将用户添加的字段信息存入字段数据库
        boolean result = taskSubmitService.save(taskSubmit);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(taskSubmit.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "根据任务id删除任务")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTaskSubmit(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        // 判断是否存在
        TaskSubmit oldTaskSubmit = taskSubmitService.getById(id);
        if (oldTaskSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldTaskSubmit.getStatus() != TaskStatusEnum.UNCHECKED.getValue()){
            throw new BusinessException(ErrorCode.STATUS_ERROR,"该任务不可删除");
        }

        boolean b = taskSubmitService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员和本人）代码抽象到Service层
     *
     * @param taskUpdateRequest
     * @return
     */
    @ApiOperation(value = "更新任务")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTaskSubmit(@RequestBody TaskSubmitUpdateRequest taskUpdateRequest) {
        if (taskUpdateRequest == null || taskUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        TaskSubmit task = new TaskSubmit();

        if (task.getStatus() != null){
            if (task.getStatus() == TaskStatusEnum.UNCHECKED.getValue()){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"只可更新未审核的提交");
            }
        }

        // bean拷贝 将前端发来要更新的信息封装进task对象中
        // copyProperties(Object source, Object target)
        BeanUtils.copyProperties(taskUpdateRequest, task);
        // TODO 参数校验
        // taskSubmitService.validAndHandleTaskSubmit(task, false);
        long id = taskUpdateRequest.getId();
        // 判断是否存在
        TaskSubmit oldTaskSubmit = taskSubmitService.getById(id);
        if (oldTaskSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 更新之后需要发布者重新审核
//        task.setStatus(TaskStatusEnum.UNCHECKED.getValue());
        if (task.getStatus() != null){
            Long taskId = oldTaskSubmit.getTaskId();
            Integer status = taskUpdateRequest.getStatus();

            checkSubmitStatus(taskId,status);
        }


        boolean result = taskSubmitService.updateById(task);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id获取单个submit信息")
    @PostMapping("/get")
    public BaseResponse<TaskSubmit> getTaskSubmitById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TaskSubmit task = taskSubmitService.getById(id);
        return ResultUtils.success(task);
    }

    /**
     * 获取列表
     * TODO 管理员接口细分 在service层封装
     * @param taskQueryRequest
     * @return
     */
    @ApiOperation(value = "获取任务提交列表", notes = "请求对象中有的字段都能查 暂不支持模糊查询 这个接口不支持分页 不传分页参数")
    @PostMapping("/list")
    public BaseResponse<List<TaskSubmit>> listTaskSubmit(@RequestBody TaskSubmitQueryRequest taskQueryRequest) {
        List<TaskSubmit> taskList = taskSubmitService.list(getQueryWrapper(taskQueryRequest));
        return ResultUtils.success(taskList);
    }

    /**
     * 分页获取列表(当前用户看到自身的提交）
     * 获取公开的数据内容
     * TODO 提供导出excel功能供科研工作者使用
     * @param taskQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "分页获取任务提交列表", notes = "请求对象中有的字段都能查 暂不支持模糊查询")
    @PostMapping("/list/page")
    public BaseResponse<TaskSubmitVO> listTaskSubmitByPage(@RequestBody TaskSubmitQueryRequest taskQueryRequest,
                                                                     HttpServletRequest request) {
        long current = taskQueryRequest.getCurrent();
        long size = taskQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        LambdaQueryWrapper<TaskSubmit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //获取用户信息
        Long userId = Long.valueOf((String) StpUtil.getLoginId());
        lambdaQueryWrapper.eq(TaskSubmit::getSubmitterId,userId);
        //status
        Integer status = taskQueryRequest.getStatus();
        if (status != null){
            lambdaQueryWrapper.eq(TaskSubmit::getStatus,status);
        }
        //添加一行代码，使结果按照createTime排序
        lambdaQueryWrapper.orderByDesc(TaskSubmit::getCreateTime);

        Page<TaskSubmit> taskPage =
                taskSubmitService.page(new Page<>(current, size), lambdaQueryWrapper);

        List<TaskSubmit> taskSubmitList = taskPage.getRecords();
        List<TaskSubmitDetail> taskSubmitDetails = new ArrayList<>();

        for (TaskSubmit submit : taskSubmitList){
            TaskSubmitDetail taskSubmitDetail = new TaskSubmitDetail();
            //获取任务信息
            Task task = taskService.getById(submit.getTaskId());

            if (task == null){
                throw new BusinessException(ErrorCode.NULL_ERROR,"存在已被删除的任务");
            }

            taskSubmitDetail.setSubmitId(submit.getId());
            taskSubmitDetail.setSubmitLatitude(submit.getLatitude());
            taskSubmitDetail.setSubmitLongitude(submit.getLongitude());
            taskSubmitDetail.setTaskName(task.getTitle());
            taskSubmitDetail.setSubmitTime(convertDateToString(submit.getCreateTime()));
            taskSubmitDetail.setDescription(submit.getDescription());
            taskSubmitDetail.setStatus(submit.getStatus());
            if (submit.getNumericalValue() != null){
                taskSubmitDetail.setNumber(submit.getNumericalValue());
            }
            if (submit.getFilesPath() != null){
                taskSubmitDetail.setFilePath(submit.getFilesPath());
            }

            taskSubmitDetails.add(taskSubmitDetail);
        }

        TaskSubmitVO taskSubmitVO = new TaskSubmitVO();
        taskSubmitVO.setTaskSubmitDetails(taskSubmitDetails);
        taskSubmitVO.setNumberOfSubmit(taskPage.getTotal());


        return ResultUtils.success(taskSubmitVO);
    }

    /**
     * 分页获取列表(任务发布者查看自身任务的提交）
     * @param taskQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "分页获取任务提交列表", notes = "请求对象中有的字段都能查 暂不支持模糊查询")
    @PostMapping("/list/page/publish")
    public BaseResponse<SubmitVO> listPublisherTaskSubmitByPage(@RequestBody TaskSubmitQueryRequest taskQueryRequest,
                                                                HttpServletRequest request) {
        long current = taskQueryRequest.getCurrent();
        long size = taskQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        LambdaQueryWrapper<TaskSubmit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //获取用户信息
        Long taskId = taskQueryRequest.getTaskId();

        Task task = taskService.getById(taskId);
        if(task.getRootId() != -1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请传入大任务id");
        }

        List<Long> smallTaskIds = new ArrayList<>();
        LambdaQueryWrapper<Task> getSmallTaskIds = new LambdaQueryWrapper<>();
        getSmallTaskIds.eq(Task::getRootId,taskId);
        List<Task> smallTasks = taskService.list(getSmallTaskIds);

        List<TaskSubmitDetail> taskSubmits = new ArrayList<>();

        for (Task smallTask:smallTasks){
            TaskSubmitQueryRequest tt = new TaskSubmitQueryRequest();
            tt.setTaskId(smallTask.getId());
            if (taskQueryRequest.getStatus() != null){
                tt.setStatus(taskQueryRequest.getStatus());
            }
            List<TaskSubmitDetail> submits = listPublisherSmallTaskSubmitByPage(tt).getData();
            taskSubmits.addAll(submits);
        }
        SubmitVO submitVO = new SubmitVO();
        submitVO.setTotal(taskSubmits.size());

        List<TaskSubmitDetail> result = listToPage((int)current,(int)size,taskSubmits).getRecords();
        sortListBySubmitTime(result);

        submitVO.setLists(result);



        return ResultUtils.success(submitVO);
    }

    public static void sortListBySubmitTime(List<TaskSubmitDetail> list) {
        Collections.sort(list, new Comparator<TaskSubmitDetail>() {
            @Override
            public int compare(TaskSubmitDetail o1, TaskSubmitDetail o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date1 = sdf.parse(o1.getSubmitTime());
                    Date date2 = sdf.parse(o2.getSubmitTime());

                    // 使用Date的compareTo方法进行比较，返回值为正数表示date1晚于date2，负数表示date1早于date2
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0; // 出现异常时返回0
            }
        });
    }


    public <T> Page<T> listToPage(int currentPage, int pageSize, List<T> list) {
        int listSize = list.size();
        int startIndex = Math.min((currentPage - 1) * pageSize, listSize);
        int endIndex = Math.min(startIndex + pageSize, listSize);
        List<T> records = list.subList(startIndex, endIndex);

        Page<T> page = new Page<>(currentPage, pageSize);
        page.setRecords(records);
        page.setTotal(listSize);

        return page;
    }

    public BaseResponse<List<TaskSubmitDetail>> listPublisherSmallTaskSubmitByPage(TaskSubmitQueryRequest taskQueryRequest) {

        LambdaQueryWrapper<TaskSubmit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //获取用户信息
        Long taskId = taskQueryRequest.getTaskId();
        lambdaQueryWrapper.eq(TaskSubmit::getTaskId,taskId);
        //status
        Integer status = taskQueryRequest.getStatus();
        if (status != null){
            lambdaQueryWrapper.eq(TaskSubmit::getStatus,status);
        }

        List<TaskSubmit> taskSubmitList = taskSubmitService.list(lambdaQueryWrapper);

        List<TaskSubmitDetail> taskSubmitDetails = new ArrayList<>();

        for (TaskSubmit submit : taskSubmitList){
            TaskSubmitDetail taskSubmitDetail = new TaskSubmitDetail();
            //获取任务信息
            Task task = taskService.getById(submit.getTaskId());

            taskSubmitDetail.setSubmitId(submit.getId());
            taskSubmitDetail.setTaskName(task.getTitle());
            taskSubmitDetail.setSubmitTime(convertDateToString(submit.getCreateTime()));
            taskSubmitDetail.setDescription(submit.getDescription());
            taskSubmitDetail.setTaskId(submit.getTaskId());
            taskSubmitDetail.setTaskLatitude(task.getLatitude());
            taskSubmitDetail.setTaskLongitude(task.getLongitude());
            taskSubmitDetail.setSubmitLatitude(submit.getLatitude());
            taskSubmitDetail.setSubmitLongitude(submit.getLongitude());
            taskSubmitDetail.setStatus(submit.getStatus());

            TaskUpdateRequest taskUpdateRequest = new TaskUpdateRequest();
            taskUpdateRequest.setId(taskId);
            taskSubmitDetail.setMaxPassed(taskController.selectTaskSchedule(taskUpdateRequest).getData().getMaxPassed());
            taskSubmitDetail.setCurrentPassed(taskController.selectTaskSchedule(taskUpdateRequest).getData().getCurrentPassed());

            User user = userService.getById(submit.getSubmitterId());
            taskSubmitDetail.setSubmitterId(user.getId());
            taskSubmitDetail.setSubmitterName(user.getNickName());

            if (submit.getNumericalValue() != null){
                taskSubmitDetail.setNumber(submit.getNumericalValue());
            }
            if (submit.getFilesPath() != null){
                taskSubmitDetail.setFilePath(submit.getFilesPath());
            }

            taskSubmitDetails.add(taskSubmitDetail);
        }


        return ResultUtils.success(taskSubmitDetails);
    }

    /**
     * 获取当前用户可选的全部资源列表
     *
     * @param taskQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "获取本人提交列表", notes = "submitterId后端写死为本人id status 0表示\"待审核\"，1表示\"已通过\"，2表示\"不合格\"")
    @PostMapping("/my/list")
    public BaseResponse<List<TaskSubmit>> listMyTaskSubmit(@RequestBody TaskSubmitQueryRequest taskQueryRequest,
                                                         HttpServletRequest request) {
        TaskSubmit taskQuery = new TaskSubmit();
        if (taskQueryRequest != null) {
            BeanUtils.copyProperties(taskQueryRequest, taskQuery);
        }
        // 查询本人提交的
        Long userId = Long.valueOf((String) StpUtil.getLoginId());
        taskQuery.setSubmitterId(userId);
        QueryWrapper<TaskSubmit> queryWrapper = getQueryWrapper(taskQueryRequest);

        // TODO 确认需要查询的字段
        List<TaskSubmit> submitList = taskSubmitService.list(queryWrapper);

        return ResultUtils.success(submitList);
    }

    /**
     * 分页获取当前用户可选的资源列表
     *
     * @param taskQueryRequest 统一封装了查询字段的分页信息 之后直接从对象中取出即可
     * @param request
     * @return
     */
    @ApiOperation(value = "分页获取本人提交列表", notes = "对象中有的字段都能查 暂不支持模糊查询")
    @PostMapping("/my/list/page")
    public BaseResponse<Page<TaskSubmit>> listMyTaskSubmitByPage(@RequestBody TaskSubmitQueryRequest taskQueryRequest,
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
        QueryWrapper<TaskSubmit> queryWrapper = getQueryWrapper(taskQueryRequest);

        Page<TaskSubmit> taskPage = taskSubmitService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(taskPage);
    }

    // endregion

    @PostMapping("/getTaskIdBySubmitId")
    public BaseResponse<Long> getTaskIdBySubmitId(Long submitId){
        return ResultUtils.success(taskSubmitService.getTaskIdBySubmitId(submitId));
    }




    /**
     * 获取查询包装类
     * 对于查询请求的统一封装处理
     * 这里是支持字符串的模糊查询 和两种字段名的查询
     * @param taskQueryRequest
     * @return
     */
    private QueryWrapper<TaskSubmit> getQueryWrapper(@RequestBody TaskSubmitQueryRequest taskQueryRequest) {
        if (taskQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        TaskSubmit taskQuery = new TaskSubmit();
        BeanUtils.copyProperties(taskQueryRequest, taskQuery);
        // 模糊查询
        // String searchName = taskQueryRequest.getSearchText();
        String sortField = taskQueryRequest.getSortField();
        String sortOrder = taskQueryRequest.getSortOrder();
        // 模糊搜索
//        taskQuery.setTitle(null);
//        taskQuery.setDetails(null);
        QueryWrapper<TaskSubmit> queryWrapper = new QueryWrapper<>(taskQuery);
//        queryWrapper.like(StringUtils.isNotBlank(title), "name", title);
//        queryWrapper.like(StringUtils.isNotBlank(details), "fieldName", details);
//        if (StringUtils.isNotBlank(searchName)) {
//            queryWrapper.like("name", searchName).or().like("fieldName", searchName);
//        }
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 更新任务图片
     * 路径：C:\root\resource\files\xxxxxx
     * @return url
     */
    @PostMapping("/updateImages/{submitId}")
    public BaseResponse<List<String>> updateSubmitImages(@PathVariable("submitId") Long submitId, MultipartFile[] images) {
        if (images == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long id = Long.valueOf((String) StpUtil.getLoginId());

        List<String> paths = taskSubmitService.updateSubmitImages(submitId,images);

        return ResultUtils.success(paths);
    }


    public void checkSubmitStatus(Long taskId, Integer status) {
        Task task = taskService.getById(taskId);

        if (status != 1 || task.getRootId() == -1){
            return;
        }

        LambdaQueryWrapper<TaskSubmit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TaskSubmit::getTaskId,taskId);
        lambdaQueryWrapper.eq(TaskSubmit::getStatus, TaskStatusEnum.CHECKED.getValue());
        Long count = taskSubmitService.count(lambdaQueryWrapper);
        task.setCurrentPassed(count+1);

        if(count + 1 == task.getMaxPassed()){
            task.setSubmitStatus(COMPLETED);

            Long rootId = task.getRootId();
            Task rootTask = taskService.getById(rootId);
            rootTask.setCurrentPassed(rootTask.getCurrentPassed()+1);
            if (rootTask.getCurrentPassed().equals(rootTask.getNumberOfSmallTask())){
                rootTask.setSubmitStatus(COMPLETED);
            }

            taskService.updateById(rootTask);
        }

        taskService.updateById(task);

    }

}
