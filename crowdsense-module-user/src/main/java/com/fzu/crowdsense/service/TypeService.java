package com.fzu.crowdsense.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.model.entity.Type;

import java.util.List;


/**
 * (Type)表服务接口
 *
 * @author makejava
 * @since 2023-05-12 09:13:28
 */
public interface TypeService extends IService<Type> {

    BaseResponse<List<String>> getAllType();


    BaseResponse<Boolean> addNewType(String typeName);
}
