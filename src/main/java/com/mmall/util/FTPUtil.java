package com.mmall.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import lombok.Data;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class FTPUtil {

    private static String ftpIp;
    private static int ftpPort;
    private static String ftpUser;
    private static String ftpPasswd;

    @Value("${ftp.server.ip}")
    public void setFtpIp(String ftpIp) {
        FTPUtil.ftpIp = ftpIp;
    }

    @Value("${ftp.server.port}")
    public void setFtpPort(int ftpPort) {
        FTPUtil.ftpPort = ftpPort;
    }

    @Value("${ftp.user}")
    public void setFtpUser(String ftpUser) {
        FTPUtil.ftpUser = ftpUser;
    }

    @Value("${ftp.passwd}")
    public void setFtpPasswd(String ftpPasswd) {
        FTPUtil.ftpPasswd = ftpPasswd;
    }

    private static FTPClient ftpClient;
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    //Entrance of UploadFile Function.
    public static boolean uploadFile(List<File> fileList) throws IOException {
        logger.info("Start connecting FTP server...");
        boolean uploadResult = false;
        boolean connectResult = FTPUtil.connectFTPServer();
        if (connectResult) {
            logger.info("Start uploading files to FTP server...");
            uploadResult = FTPUtil.uploadFileExcecute("img", fileList);
        }
        return uploadResult;
    }

    //Connect to FTP server
    private static boolean connectFTPServer() {
        boolean isSucc = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpIp);
            isSucc = ftpClient.login(ftpUser, ftpPasswd);
        } catch (IOException e) {
            logger.error("connectFTPServer failed.", e);
        }
        return isSucc;
    }

    //Upload fileList to remotePath on FTP server
    private static boolean uploadFileExcecute(String remotePath, List<File> fileList)
            throws IOException {
        FileInputStream fis = null;
        boolean uploaded = false;
        try {
            ftpClient.changeWorkingDirectory(remotePath);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            for (File file : fileList) {
                fis = new FileInputStream(file);
                ftpClient.storeFile(file.getName(), fis);
            }
            uploaded = true;
        } catch (IOException e) {
            logger.error("Upload file failed.", e);
            e.printStackTrace();
        } finally {
            fis.close();
            ftpClient.disconnect();
        }
        return uploaded;
    }

}
