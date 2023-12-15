package com.fzu.crowdsense;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 本模块仅用作数据库数据迁移使用<br/>
 * 需要转移的数据库，表为： crowdsense_v2（task_submit,task_publish） ===》crowdsense（task_submit,task_publish）
 *
 * @author Zaki
 * @version 1.0
 * @since 2023-07-15
 **/
@SpringBootApplication
@MapperScan("com.fzu.crowdsense.mapper")
public class DataMigrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataMigrationApplication.class);
    }
}
