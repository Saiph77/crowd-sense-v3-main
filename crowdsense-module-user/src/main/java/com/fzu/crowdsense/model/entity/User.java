package com.fzu.crowdsense.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    /**
     * id
     */

    @Id
    private Long id;

    /**
     * 电话
     */
    private String phone;

    /**
     * 密码
     */
    private String password;


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
     * 用户角色 user - 普通用户 admin - 管理员
     */
    private String role;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic(value = "0", delval = "1")
    private Integer isDelete;


    @TableField(exist = false)
    @Transient
    private static final long serialVersionUID = 1L;
}