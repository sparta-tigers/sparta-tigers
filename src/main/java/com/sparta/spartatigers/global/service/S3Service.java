package com.sparta.spartatigers.global.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;
import com.sparta.spartatigers.global.util.FileUtil;
import com.sparta.spartatigers.global.util.S3FolderType;
import com.sparta.spartatigers.global.util.S3Properties;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    private final S3Properties s3Properties;
    private final FileUtil fileUtil;

    public String uploadFile(MultipartFile file, S3FolderType folderType, Long userId) {
        if (file == null || file.isEmpty()) {
            return s3Properties.getDefaultImagePath();
        }
        fileUtil.validateExtension(file);
        fileUtil.validateFileSize(file);

        String folderPath = s3Properties.getFolderPath(folderType);
        String originalFileName = file.getOriginalFilename();
        String fileName = fileUtil.createFileName(folderPath, originalFileName, userId);
        String bucket = s3Properties.getBucket();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new ServerException(ExceptionCode.FILE_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }
}
