package com.fzu.crowdsense.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * UserDTO
 * <p>
 *
 * @author Zaki
 * @version 2.0
 * @since 2023-03-12
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */

    private String id;

    /**
     * 电话
     */
    private String phone;

    /**
     * 用户头像
     */
    private String icon;


    /**
     * 用户昵称
     */
    private String nickName;


    /**
     * 签名
     */
    private String signature;

    /**
     * 地址
     */
    private String address;


    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 用户自定义首页
     */
    private String home;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
