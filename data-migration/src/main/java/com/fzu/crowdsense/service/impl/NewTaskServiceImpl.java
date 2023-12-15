package com.fzu.crowdsense.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.entity.NewTask;
import com.fzu.crowdsense.mapper.NewTaskMapper;
import com.fzu.crowdsense.service.NewTaskService;
import org.springframework.stereotype.Service;

/**
 *
 * @author Zaki
 * @since 2023-07-18
 **/
@DS("new")
@Service
public class NewTaskServiceImpl extends ServiceImpl<NewTaskMapper, NewTask> implements NewTaskService {

}
