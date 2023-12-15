package com.fzu.crowdsense.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzu.crowdsense.common.BaseResponse;
import com.fzu.crowdsense.common.ErrorCode;
import com.fzu.crowdsense.model.entity.Favorites;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wms
 * @since 2023-03-11
 */
public interface FavoritesService extends IService<Favorites> {

    BaseResponse<ErrorCode> addCollection(Long id, Long user_id, Long task_id);

    BaseResponse<ErrorCode> deleteByUserId(Long userId);

    BaseResponse<ErrorCode> deleteByTaskId(Long taskId);

    BaseResponse<Boolean> checkFavorites(Long userId, Long taskId);

    BaseResponse<Long> getCountByTaskId(Long taskId);

    BaseResponse<Long> getCountByUserId(Long userId);
}
