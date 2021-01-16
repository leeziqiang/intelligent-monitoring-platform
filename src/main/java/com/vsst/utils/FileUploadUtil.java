package com.vsst.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Data
@AllArgsConstructor
public class FileUploadUtil {
    private String saveFilePath;//从前端上传的图片保存在后端的路径
    /**
     * 上传文件
     *
     * @param multipartFile
     * @return 文件存储路径
     */
    public static String upload(MultipartFile multipartFile,String saveFilePath)  {
        // 文件存储位置，文件的目录要存在才行，可以先创建文件目录，然后进行存储

        String filePath = saveFilePath + multipartFile.getOriginalFilename();
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 文件存储
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
