package com.fzu.crowdsense.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.exception.BusinessException;
import com.fzu.crowdsense.mapper.TaskSubmitMapper;
import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.model.entity.TaskSubmit;
import com.fzu.crowdsense.model.enums.TaskStatusEnum;
import com.fzu.crowdsense.service.TaskService;
import com.fzu.crowdsense.service.TaskSubmitService;
import com.fzu.crowdsense.utils.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.fzu.crowdsense.common.ErrorCode.SYSTEM_ERROR;
import static com.fzu.crowdsense.constant.SystemConstants.SUBMIT_IMAGES_SUB_PATH;
import static com.fzu.crowdsense.constant.TaskConstant.COMPLETED;

/**
* @author bopeng
* @description 针对表【task_submit】的数据库操作Service实现
* @createDate 2023-04-06 23:47:37
*/
@Service
public class TaskSubmitServiceImpl extends ServiceImpl<TaskSubmitMapper, TaskSubmit>
    implements TaskSubmitService{

    @Resource
    private TaskService taskService;

    @Override
    public List<String> updateSubmitImages(Long submitId, MultipartFile[] images) {

        TaskSubmit taskSubmit = getById(submitId);

        List<String> paths = new ArrayList<>();

        for (MultipartFile image : images) {
            if (StrUtil.isNotEmpty(taskSubmit.getFilesPath())) {
                //获取旧文件名
                String[] oldFilePath = taskSubmit.getFilesPath().split("/");
                String oldFileName = oldFilePath[oldFilePath.length - 1];
                //删除旧文件
                FileUtils.delete(SUBMIT_IMAGES_SUB_PATH, oldFileName);
            }

            try {
                String path = FileUtils.upload(SUBMIT_IMAGES_SUB_PATH, image);
//                //更新redis缓存
//                stringRedisTemplate.opsForValue().set(TASK_INFO + taskId, JSONUtil.toJsonStr(task));
                paths.add(path);
            } catch (IOException e) {
//                log.error("更新任务图片失败=====》{}", e.getLocalizedMessage());
                throw new BusinessException(SYSTEM_ERROR, e.getLocalizedMessage());
            }
        }

        String path = String.join(",",paths);
        taskSubmit.setFilesPath(path);
        boolean i = updateById(taskSubmit);
        if (!i) {
            log.error("更新任务图片失败");
            throw new BusinessException(SYSTEM_ERROR, "更新任务图片失败");
        }

        return paths;
    }

    @Override
    public void checkSubmitStatus(Long taskId, Integer status) {
        Task task = taskService.getById(taskId);

        if (status != 1 || task.getRootId() == -1){
            return;
        }

        LambdaQueryWrapper<TaskSubmit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TaskSubmit::getTaskId,taskId);
        lambdaQueryWrapper.eq(TaskSubmit::getStatus, TaskStatusEnum.CHECKED.getValue());
        Long count = count(lambdaQueryWrapper);
        task.setCurrentPassed(count);

        if(count + 1 == task.getMaxPassed()){
            task.setSubmitStatus(COMPLETED);

            Long rootId = task.getRootId();
            Task rootTask = taskService.getById(rootId);
            rootTask.setCurrentPassed(rootTask.getCurrentPassed()+1);
            taskService.updateById(rootTask);
        }

        taskService.updateById(task);

    }

    @Override
    public Long getTaskIdBySubmitId(Long submitId) {
        TaskSubmit taskSubmit = getById(submitId);
        return taskSubmit.getTaskId();
    }
}




