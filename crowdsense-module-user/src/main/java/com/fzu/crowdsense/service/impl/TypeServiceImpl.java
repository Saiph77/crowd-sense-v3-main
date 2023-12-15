package com.fzu.crowdsense.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ResultUtils;
import com.fzu.crowdsense.constant.TypeConstant;
import com.fzu.crowdsense.mapper.TypeMapper;
import com.fzu.crowdsense.model.entity.Type;
import com.fzu.crowdsense.service.TypeService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * (Type)表服务实现类
 *
 * @author makejava
 * @since 2023-05-12 09:13:28
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {



    @Override
    public BaseResponse<List<String>> getAllType() {
        //查询type表，状态为正常使用的type
        LambdaQueryWrapper<Type> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.eq(Type::getStatu, TypeConstant.NORMAL_USE);
        List<Type> typeList = list(typeWrapper);
        List<String> types = typeList.stream()
                .map(type -> type.getType())
                .collect(Collectors.toList());

        return ResultUtils.success(types);
    }

    @Override
    public BaseResponse<Boolean> addNewType(@RequestParam String typeName) {
        Type type = new Type();
        System.out.println("typenameos:     "+typeName+"**************");
        type.setType(typeName);
        type.setStatu(TypeConstant.NORMAL_USE);
        boolean result = save(type);
        return ResultUtils.success(result);
    }


}

