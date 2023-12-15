package com.fzu.crowdsense.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzu.crowdsense.algorithms.resource.Participant;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.model.entity.Task;
import com.fzu.crowdsense.model.entity.User;
import com.fzu.crowdsense.model.vo.TaskVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
* @author bopeng
* @description 针对表【task】的数据库操作Service
* @createDate 2023-04-07 00:03:23
*/
public interface TaskService extends IService<Task> {


    List<TaskVO> getSmallTaskList(Long bigTaskId, Long pageNum, Long pageSize);

    List<TaskVO> getChildren(Long id);

    Map<Integer, List<Task>> recommendTask(User user, Double l1, Double l2);

    List<Task> getTaskAllocation(User user);

    Long countByTaskId(Long taskId);


    List<Task> getTaskRecommend(User user, long current, long pageSize);

    BaseResponse<List<Long>> getRandomUserIdByTaskId(Long taskId);

    BaseResponse<TaskVO> getTaskVoByTaskId(Long taskId);

    List<String> updateTaskImages(Long taskId, MultipartFile[] images);

    BaseResponse<List<Participant>> allcoationMiniTask(Long taskId);
}
