package com.fzu.crowdsense.model.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * (Type)表实体类
 *
 * @author makejava
 * @since 2023-05-12 09:13:20
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("type")
public class Type  {
    @TableId
    private Integer id;

    
    private String type;
    //0表示正常使用，1表示停止使用
    private String statu;



}
