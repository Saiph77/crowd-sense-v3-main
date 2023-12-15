package com.fzu.crowdsense.utils;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static com.fzu.crowdsense.constant.SystemConstants.FILE_ROOT_PATH;



/**
 * <p>
 * 文件上传和删除
 * <p>
 *
 * @author Zaki
 * @version 2.0
 * @since 2023-03-13
 **/
@Slf4j
public class FileUtils {

    /**
     * 上传文件
     *
     * @param subPath 子路径
     * @param file    文件
     * @return 完整路径
     * @throws IOException IOException
     */
    public static String upload(String subPath, MultipartFile file) throws IOException {
        String oldFileName = file.getOriginalFilename();


        String newFileName = UUID.randomUUID().toString(true) + oldFileName.substring(oldFileName.lastIndexOf("."));



        File dest = new File(FILE_ROOT_PATH + subPath + newFileName);



        // 判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            boolean b = dest.getParentFile().mkdir();
            if(!b){
                log.error("创建目录失败===》{}",dest.getAbsolutePath());
                return null;
            }
        }
        // 保存文件
        file.transferTo(dest);

        return subPath + newFileName;
    }

    /**
     * 删除文件
     *
     * @param subPath  子路径
     * @param fileName 文件名
     * @return 是否成功删除
     */
    public static boolean delete(String subPath, String fileName) {
        String path = FILE_ROOT_PATH + subPath + fileName;
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}


