package com.fzu.crowdsense.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzu.crowdsense.model.entity.TaskSubmit;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author bopeng
* @description 针对表【task_submit】的数据库操作Service
* @createDate 2023-04-06 23:47:37
*/
public interface TaskSubmitService extends IService<TaskSubmit> {

    List<String> updateSubmitImages(Long submitId, MultipartFile[] images);

    void checkSubmitStatus(Long taskId, Integer status);

    Long getTaskIdBySubmitId(Long submitId);
}
