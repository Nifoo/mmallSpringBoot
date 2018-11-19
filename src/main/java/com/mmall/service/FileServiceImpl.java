package com.mmall.service;

import com.google.common.collect.Lists;
import com.mmall.util.FTPUtil;
import com.mysql.cj.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file, String path) {
        if(file==null || StringUtils.isNullOrEmpty(path)){
            logger.error("No file selected or empty path!");
            return null;
        }
        //Original Local fileName and Extension
        String fileName = file.getOriginalFilename();
        String fileNameExt = fileName.substring(fileName.lastIndexOf(".") + 1);
        //New fileName
        String uploadFileName = UUID.randomUUID().toString() + "." + fileNameExt;

        logger.info("Start uploading file to Tomcat. fileName:{}, path:{}, uploadFileName:{}", fileName, path,
                uploadFileName);

        //Create Dir and set privilege
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs(); //mkdirs() can make deep dir like a/b/c... while mkdir() can only handle the current folder level?
        }
        File targetFile = new File(path, uploadFileName);

        try {
            //1. upload file to targetFile on Tomcat (under Tomcat folder? maybe)
            file.transferTo(targetFile);

            //2. upload targetFile on Tomcat to FTP server
            boolean uploadResult = FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            //3. delete targetFile on Tomcat folder
            targetFile.delete();

            if(!uploadResult) return null;

        } catch (IOException e) {
            logger.error("Upload file error.", e);
            return null;
        }
        return targetFile.getName();
    }
}
