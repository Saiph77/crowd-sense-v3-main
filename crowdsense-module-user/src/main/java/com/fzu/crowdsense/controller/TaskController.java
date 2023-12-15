package com.fzu.crowdsense.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import com.fzu.crowdsense.model.enums.ReviewStatusEnum;
import com.fzu.crowdsense.model.enums.TaskStatusEnum;
import com.fzu.crowdsense.model.request.task.*;
import com.fzu.crowdsense.model.vo.TaskScheduleVO;
import com.fzu.crowdsense.model.vo.TaskVO;
import com.fzu.crowdsense.model.vo.getPublishTask.PublisherTaskDetail;
import com.fzu.crowdsense.model.vo.getPublishTask.PublisherTaskVO;
import com.fzu.crowdsense.service.TaskService;
import com.fzu.crowdsense.service.TaskSubmitService;
import com.fzu.crowdsense.service.TypeService;
import com.fzu.crowdsense.service.UserService;
import com.fzu.crowdsense.utils.BeanCopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fzu.crowdsense.constant.TaskConstant.*;

/**
 * 任务发布接口
 *
 * @author https://github.com/liyupi
 */
@Api(value = "任务接口", description = "根据日期范围查询暂未实现")
@RestController
@RequestMapping("/task/publish")
@Slf4j
public class TaskController {

    @Resource
    private TaskService taskService;

    @Resource
    private UserService userService;

    @Resource
    private TypeService typeService;

    @Resource
    private FavoritesController favoritesController;

    @Resource
    private TaskSubmitService taskSubmitService;

    /**
     * 发布
     *
     * @param
     * @param request
     * @return
     */
    @PostMapping("/online")
    @ApiOperation(value = "任务发布接口")
    public BaseResponse<Boolean> onlineTask(@RequestBody TaskOnlineRequest taskStatusRequest,
                                                     HttpServletRequest request) {
        if (taskStatusRequest == null || taskStatusRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = taskStatusRequest.getId();
        // 判断是否存在
        Task oldTask = taskService.getById(id);
        if (oldTask == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断该任务的type是否在正常使用
        String type = oldTask.getType();
        if (!typeService.getAllType().getData().contains(type)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前任务类型不可用");
        }

        //  判断是否审核通过
        if (oldTask.getCheckStatus() != ReviewStatusEnum.PASS.getValue()) {
            throw new BusinessException(ErrorCode.STATUS_ERROR);
        }
        // TODO 修改（仅本人和管理员可修改）
        Task task = new Task();
        task.setId(id);
        task.setOnlineStatus(TaskStatusEnum.ONLINE.getValue());
        boolean result = taskService.updateById(task);
        return ResultUtils.success(result);
    }

    /**
     * 下线
     *
     * @param
     * @param request
     * @return
     */
    @ApiOperation(value = "任务下线接口")
    @PostMapping("/offline")
    public BaseResponse<Boolean> offlineTask(@RequestBody TaskOnlineRequest taskStatusRequest,
                                                      HttpServletRequest request) {
        if (taskStatusRequest == null || taskStatusRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = taskStatusRequest.getId();
        // 判断是否存在
        Task oldTask = taskService.getById(id);
        if (oldTask == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        Task task = new Task();
        task.setId(id);
        task.setOnlineStatus(TaskStatusEnum.OFFLINE.getValue());
        boolean result = taskService.updateById(task);
        return ResultUtils.success(result);
    }

    // TODO service层 校验

    // TODO 根据父任务ID 查询子任务信息


    /**
     * 查询大任务包含的小任务数量
     * @param id
     */
    @GetMapping("/count/smallTask")
    public BaseResponse<Long> getCountOfSmallTask(Long id,
                                                  HttpServletRequest request) {
        //判断为空
        if (id == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        Task task = taskService.getById(id);
        //判断对象存在
        if (task == null){
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }
        //判断是否为为大任务
        if (task.getRootId() != -1){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"这是个小任务");
        }
        Long count = task.getNumberOfSmallTask();
        return ResultUtils.success(count);
    }

    // region 增删改查

    /**
     * 创建任务
     * @return
     */
    @ApiOperation(value = "创建任务")
    @PostMapping("/add")
    public BaseResponse<Long> addTask(@RequestBody TaskAddRequest taskAddRequest, MultipartFile[] images) {
        if (taskAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断该任务的type是否在正常使用
        String type = taskAddRequest.getType();
        if (!typeService.getAllType().getData().contains(type)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前任务类型不可用");
        }
        Long rootId = taskAddRequest.getRootId();
        //判断是否是子任务
        if (!rootId.equals(BIGTASK_ROOTID)){
            //查询父任务
            Task rootTask = getTaskById(rootId).getData();
            //更新父任务中的子任务数量
            Long numberOfSmallTask = rootTask.getNumberOfSmallTask();
            rootTask.setNumberOfSmallTask(++numberOfSmallTask);
            taskService.updateById(rootTask);
            //更新父任务中的maxPassed参数
            //将小任务的已完成小任务和包含小任务数量锁定为-1，无实际意义
            taskAddRequest.setNumberOfSmallTask(-1L);
            taskAddRequest.setCompletedSmallTask(-1L);
        }
        Task task = new Task();
        BeanUtils.copyProperties(taskAddRequest, task);
        // 校验
        //taskService.validAndHandleTask(task, true);
        // 获取用户id 后填入字段信息
        Long id = Long.valueOf((String) StpUtil.getLoginId());
        task.setPublisherId(id);
        task.setCheckStatus(TaskStatusEnum.CHECKED.getValue());
        task.setOnlineStatus(ONLINE);
        // 将用户添加的字段信息存入字段数据库
        boolean result = taskService.save(task);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
//        if (images != null){
//            taskService.updateTaskImages(task.getId(), images);
//        }
        return ResultUtils.success(task.getId());
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
    public BaseResponse<Boolean> deleteTask(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = Long.valueOf((String) StpUtil.getLoginId());
        long id = deleteRequest.getId();
        // 判断是否存在
        Task oldTask = taskService.getById(id);
        if (oldTask == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Long rootId = oldTask.getRootId();
        // 仅本人或管理员可删除
        if (!oldTask.getPublisherId().equals(userId) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        boolean b = taskService.removeById(id);
        if (b){
            favoritesController.deleteCollectionByTaskId(id);
            Task rootTask = taskService.getById(rootId);
            Long numberOfSmallTask = rootTask.getNumberOfSmallTask();
            rootTask.setNumberOfSmallTask(--numberOfSmallTask);
            rootTask.setMaxPassed(numberOfSmallTask);
            taskService.updateById(rootTask);
            return ResultUtils.success(b);
        }else{
            return ResultUtils.error(ErrorCode.OPERATION_ERROR,"删除失败");
        }
    }

    /**
     * 更新（仅本人和管理员）
     *
     * @param taskUpdateRequest
     * @return
     */
    @ApiOperation(value = "任务更新接口")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTask(@RequestBody TaskUpdateRequest taskUpdateRequest) {
        if (taskUpdateRequest == null || taskUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Task task = new Task();
        // bean拷贝 将前端发来要更新的信息封装进task对象中
        // copyProperties(Object source, Object target)
        BeanUtils.copyProperties(taskUpdateRequest, task);
        // 参数校验
        //taskService.validAndHandleTask(task, false);
        long id = taskUpdateRequest.getId();
        // 判断是否存在
        Task oldTask = taskService.getById(id);
        if (oldTask == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 更新之后需要管理员重新审核
        task.setCheckStatus(TaskStatusEnum.UNCHECKED.getValue());
        boolean result = taskService.updateById(task);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据任务id获取任务信息", notes = "只返回当前任务的信息")
    @PostMapping("/get")
    public BaseResponse<Task> getTaskById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Task task = taskService.getById(id);
        return ResultUtils.success(task);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param taskQueryRequest
     * @return
     */
    //TODO bug搜索不到
    @ApiOperation(value = "查询所有任务列表", notes = "需要查什么就传什么 能填的字段都能查 但暂不支持模糊查询")
    @PostMapping("/list")
    public BaseResponse<List<Task>> listTask(@RequestBody TaskQueryRequest taskQueryRequest) {
        List<Task> taskList = taskService.list(getQueryWrapper(taskQueryRequest));
        return ResultUtils.success(taskList);
    }

    /**
     * 分页获取列表
     * 获取可导入的字段内容
     * @param taskQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "分页查询父任务列表", notes = "rootId后端写死为-1，这里只能查已发布的任务")
    @PostMapping("/list/page")
    public BaseResponse<Page<Task>> listTaskByPage(@RequestBody TaskQueryRequest taskQueryRequest,
                                                             HttpServletRequest request) {
        long current = taskQueryRequest.getCurrent();
        long size = taskQueryRequest.getPageSize();
        taskQueryRequest.setRootId(-1L);
        System.out.println("current:"+current+" size:"+size);
        // 限制爬虫
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        taskQueryRequest.setCheckStatus(TaskStatusEnum.CHECKED.getValue());

        QueryWrapper<Task> queryWrapper = getQueryWrapper(taskQueryRequest);
        queryWrapper.eq("rootId",-1L);

        Page<Task> taskPage =
                taskService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(taskPage);
    }

    /**
     * 获取用户可见的任务列表
     *
     * @param taskQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "展示用户可见的大任务列表", notes = "公开且审核通过的大任务, 暂未做分页")
    @PostMapping("/list/publish/bigTask")
    public BaseResponse<List<Task>> listMyTask(@RequestBody TaskQueryRequest taskQueryRequest,
                                                         HttpServletRequest request) {
        Task taskQuery = new Task();
        if (taskQueryRequest != null) {
            BeanUtils.copyProperties(taskQueryRequest, taskQuery);
        }
        // 先查询所有审核通过的 审核未通过的单独写一个接口
        taskQuery.setRootId(-1L);
        taskQuery.setCheckStatus(ReviewStatusEnum.PASS.getValue());
        taskQuery.setOnlineStatus(ReviewStatusEnum.PASS.getValue());

        QueryWrapper<Task> queryWrapper = getQueryWrapper(taskQueryRequest);
        List<Task> taskList = taskService.list(queryWrapper);

        return ResultUtils.success(taskList);
    }

    /**
     * 获取当前用户可完成的任务列表
     *
     * @param
     * @param request
     * @return
     */
    @ApiOperation(value = "获取大任务下的可完成的小任务", notes = "返回的大任务对象中包含了小任务的集合")
    @PostMapping("/list/page/publish/smallTask")
    public BaseResponse<TaskVO> listMyTaskByPage(Long bigTaskId,
                                                     HttpServletRequest request) {
        if (bigTaskId == null || bigTaskId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        Task bigTask = taskService.getById(bigTaskId);
        List<TaskVO> childTaskList = taskService.getChildren(bigTaskId);
        TaskVO taskVO = new TaskVO();
        BeanUtils.copyProperties(bigTask, taskVO);
        taskVO.setChildTask(childTaskList);

//        Long publisherId = bigTask.getPublisherId();
//        User publisher = userService.getById(publisherId);
//        String publisherName = publisher.getNickName();
//        taskVO.setPublisherName(publisherName);
        return ResultUtils.success(taskVO);
    }

    /**
     * 分页获取当前用户创建的任务列表
     *
     * @param
     * @param request
     * @return
     */
    @ApiOperation(value = "获取当前用户创建的任务", notes = "返回的大任务对象中包含了小任务的集合")
    @PostMapping("/my/add/list/page")
    public BaseResponse<List<TaskVO>> listMyAddTaskByPage(@RequestBody MyTaskQueryRequest myTaskQueryRequest, HttpServletRequest request) {
        if (myTaskQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Task taskQuery = new Task();
        BeanUtils.copyProperties(myTaskQueryRequest, taskQuery);
        Long id = Long.valueOf((String) StpUtil.getLoginId());
        taskQuery.setPublisherId(id);
        taskQuery.setRootId(-1L);
        // 先查大任务的id
        QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>(taskQuery);
        List<Task> taskList = taskService.list(taskQueryWrapper);
        List<TaskVO> taskVOList = taskList.stream().map(task -> {
            TaskVO taskVO = new TaskVO();
            BeanUtils.copyProperties(task, taskVO);
//            taskVO.setPublisherName(userService.getById(task.getPublisherId()).getNickName());
            taskVO.setChildTask(taskService.getChildren(task.getId()));
            return taskVO;
        }).collect(Collectors.toList());

        return ResultUtils.success(taskVOList);
    }


    // endregion

    /**
     * 获取查询包装类
     * 对于查询请求的统一封装处理
     * 这里是支持字符串的模糊查询 和两种字段名的查询
     * @param taskQueryRequest
     * @return
     */
    // TODO 完善查询处理
    private QueryWrapper<Task> getQueryWrapper(@RequestBody TaskQueryRequest taskQueryRequest) {
        // 检查参数是否为空
        if (taskQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 创建任务查询对象
        Task taskQuery = new Task();
        // 将请求对象的属性复制到任务查询对象中
        BeanUtils.copyProperties(taskQueryRequest, taskQuery);

        // 获取搜索关键词、排序字段和排序顺序sortNumber
        String sortField = SORT_NUMBER.get(taskQueryRequest.getSortNumber()*2);
        String sortOrder = SORT_NUMBER.get(taskQueryRequest.getSortNumber()*2 + 1);
        String title = taskQuery.getTitle();
        String details = taskQuery.getDetails();
        String type = taskQuery.getType();
        Integer submitStatus = taskQuery.getSubmitStatus();

        // 清空任务查询对象的标题和详情属性，后面会单独处理模糊搜索

        // 创建查询包装器对象，并传入任务查询对象
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();

        // 设置标题模糊搜索条件

        // 设置标题模糊搜索条件
        if (StringUtils.isNotBlank(title) || StringUtils.isNotBlank(details)) {
            queryWrapper.and(wrapper -> wrapper.like(StringUtils.isNotBlank(title), "title", title)
                    .or()
                    .like(StringUtils.isNotBlank(details), "details", details));
        }
        queryWrapper.eq(StringUtils.isNotBlank(type), "type", type);
        // 设置排序条件
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        //设置submitStatus
        queryWrapper.eq(submitStatus != null,"submitStatus",submitStatus);

        // 返回查询包装器对象
        return queryWrapper;
    }

//    /**
//     * 随机分配任务（推荐任务—）
//     * @param taskAllocationRequest
//     */
//    @GetMapping("/allocation")
//    public BaseResponse<List<Task>> taskAllocation(TaskAllocationRequest taskAllocationRequest,
//                                                  HttpServletRequest request) {
//        Long userId = taskAllocationRequest.getUserId();
//
//        return taskService.getTaskAllcation(userId);

//    }

    /**
     * 推荐任务
     * @param taskAllocationRequest
     * @return
     */
    @PostMapping("/taskAllocation")
    private BaseResponse<List<Task>> taskAllocation(@RequestBody TaskAllocationRequest taskAllocationRequest){
        if (taskAllocationRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
//        System.out.println(taskAllocationRequest.getUserId());
//        if (taskAllocationRequest.getUserId()<0){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户id有误");
//        }
        Long userId = Long.valueOf((String) StpUtil.getLoginId());
//        Long userId = taskAllocationRequest.getUserId();
        User user = userService.getById(userId);
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        long current = taskAllocationRequest.getCurrent();
        long pageSize = taskAllocationRequest.getPageSize();

        return ResultUtils.success(taskService.getTaskRecommend(user,taskAllocationRequest.getCurrent(),taskAllocationRequest.getPageSize()));
    }

    /**
     * 根据任务id分配随机用户
     */
    @PostMapping("/allocate")
    public BaseResponse<List<Long>> listMyTaskSubmitByPage(Long taskId){
        return taskService.getRandomUserIdByTaskId(taskId);
    }

    /**
     * 更新任务图片
     * 路径：C:\root\resource\images\xxxxxx
     * @return url
     */
    @PostMapping("/update/taskImages/{taskId}")
    public BaseResponse<List<String>> updateTaskImages(@PathVariable("taskId") Long taskId, MultipartFile[] images) {
        if (images == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long id = Long.valueOf((String) StpUtil.getLoginId());

        List<String> paths = taskService.updateTaskImages(taskId,images);

        return ResultUtils.success(paths);
    }

    /**
     * 查询当前用户发布的任务
     * 路径：C:\root\resource\images\xxxxxx
     * @return url
     */
    @PostMapping("/get/taskList")
    public BaseResponse<PublisherTaskVO> getPublisherTask(@RequestBody PublisherTaskRequest publisherTaskRequest) {
        //获取用户信息
        Long id = Long.valueOf((String) StpUtil.getLoginId());
        User user = userService.getById(id);

        LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Task::getPublisherId,id);
        lambdaQueryWrapper.eq(Task::getRootId,BIGTASK_ROOTID);

        long current = publisherTaskRequest.getCurrent();
        long size = publisherTaskRequest.getPageSize();
        int group = publisherTaskRequest.getGroup();

        if (group != 3){
            lambdaQueryWrapper.eq(Task::getSubmitStatus,group);
        }

        Page<Task> taskPage =
                taskService.page(new Page<>(current, size), lambdaQueryWrapper);

        List<Task> taskList = taskPage.getRecords();
        List<PublisherTaskDetail> taskVOList = new ArrayList<>();

        for (Task task : taskList) {
            PublisherTaskDetail publisherTaskVO = BeanCopyUtils.copyBean(task, PublisherTaskDetail.class);

//            //获取小任务信息
//            LambdaQueryWrapper<Task> smallTaskQuery = new LambdaQueryWrapper<>();
//            smallTaskQuery.eq(Task::getRootId,task.getId());
//            List<Task> smallTasks = taskService.list(smallTaskQuery);
//            List<PublisherTaskDetail> smallTasksVO = BeanCopyUtils.copyBeanList(smallTasks,PublisherTaskDetail.class);
//
//            publisherTaskVO.setSmallTasks(smallTasksVO);

            taskVOList.add(publisherTaskVO);
        }

        PublisherTaskVO publisherTaskVO = new PublisherTaskVO();
        publisherTaskVO.setPublisherName(user.getNickName());
        publisherTaskVO.setTotalTaskNumber(taskPage.getTotal());
        publisherTaskVO.setPublisherTaskDetails(taskVOList);

        return ResultUtils.success(publisherTaskVO);
    }


    /**
     * 查询任务进度
     * @return url
     */
    @PostMapping("/select/task/schedule")
    public BaseResponse<TaskScheduleVO> selectTaskSchedule (@RequestBody TaskUpdateRequest taskUpdateRequest) {
        Long id = taskUpdateRequest.getId();

        Task task = taskService.getById(id);

        if(task == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        TaskScheduleVO taskScheduleVO = new TaskScheduleVO();
        taskScheduleVO.setMaxPassed(task.getMaxPassed());

        taskScheduleVO.setCurrentPassed(task.getCurrentPassed());

        return ResultUtils.success(taskScheduleVO);
    }

    /**
     * 查询任务进度
     * @return url
     */
    @PostMapping("/update/offset")
    public BaseResponse<Boolean> updateToOffset () {

        LambdaQueryWrapper<Task> taskLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<TaskSubmit> submitQueryWrapper = new LambdaQueryWrapper<>();
        List<Task> tasks = new ArrayList<>();

        //更新小任务的currentPassed和submitStatus
        taskLambdaQueryWrapper.clear();
        taskLambdaQueryWrapper.ne(Task::getRootId,BIGTASK_ROOTID);
        tasks = taskService.list(taskLambdaQueryWrapper);
        for (Task task:tasks){
            Long taskId = task.getId();


            submitQueryWrapper.eq(TaskSubmit::getTaskId,taskId);

            Long count = taskSubmitService.count(submitQueryWrapper);
            task.setCurrentPassed(count);
            if (task.getCurrentPassed() == task.getMaxPassed() && task.getSubmitStatus() == UN_COMPLETED){
                task.setSubmitStatus(COMPLETED);
            }
            if (task.getCurrentPassed() < task.getMaxPassed() && task.getSubmitStatus() != TIMEOUT){
                task.setSubmitStatus(UN_COMPLETED);
            }

            taskService.updateById(task);
        }

        //更新大任务的maxPassed和currentPassed和submitStatus
        taskLambdaQueryWrapper.clear();
        taskLambdaQueryWrapper.eq(Task::getRootId,BIGTASK_ROOTID);
        tasks = taskService.list(taskLambdaQueryWrapper);
        for (Task task:tasks){
            Long taskId = task.getId();

            LambdaQueryWrapper<Task> tQueryWrapper = new LambdaQueryWrapper<>();
            tQueryWrapper.eq(Task::getRootId,taskId);
            Long count = taskService.count(tQueryWrapper);
            task.setNumberOfSmallTask(count);

            tQueryWrapper.eq(Task::getSubmitStatus,COMPLETED);
            count = taskService.count(tQueryWrapper);
            task.setCurrentPassed(count);

            if (task.getCurrentPassed().equals(task.getNumberOfSmallTask()) && task.getSubmitStatus().equals(UN_COMPLETED)){
                task.setSubmitStatus(COMPLETED);
            }
            if (task.getCurrentPassed() < task.getMaxPassed() && !task.getSubmitStatus().equals(TIMEOUT)){
                task.setSubmitStatus(UN_COMPLETED);
            }

            taskService.updateById(task);
        }

        //删除submit中不存在的task
        submitQueryWrapper.clear();
        List<TaskSubmit> submitList = taskSubmitService.list();

        for (TaskSubmit submit : submitList){
            Long taskId = submit.getTaskId();
            if (taskService.countByTaskId(taskId) == 0){
                taskSubmitService.removeById(submit);
            }
        }



        return ResultUtils.success(true);
    }

}
