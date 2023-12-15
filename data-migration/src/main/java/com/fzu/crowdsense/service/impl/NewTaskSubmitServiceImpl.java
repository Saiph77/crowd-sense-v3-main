package com.fzu.crowdsense.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.entity.NewTaskSubmit;
import com.fzu.crowdsense.mapper.NewTaskSubmitMapper;
import com.fzu.crowdsense.service.NewTaskSubmitService;
import org.springframework.stereotype.Service;

/**
 * @author Zaki
 * @since 2023-07-18
 **/
@DS("new")
@Service
public class NewTaskSubmitServiceImpl extends ServiceImpl<NewTaskSubmitMapper, NewTaskSubmit> implements NewTaskSubmitService {
}
