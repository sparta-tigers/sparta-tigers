package com.sparta.spartatigers.global.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

import com.amazonaws.services.s3.AmazonS3;

@Component
@RequiredArgsConstructor
public class FileUtil {
    private final S3Properties s3Properties;

    public String createFileName(String folderPath, String originalFileName, Long userId) {
        String uuidSubstring = UUID.randomUUID().toString().substring(0, 5);
        String safeOriginalName = originalFileName.replaceAll("\\s+", "_");
        return folderPath + userId + "/" + uuidSubstring + "_" + safeOriginalName;
    }

    public String extractS3KeyFromUrl(String url) {
        try {
            URL s3Url = new URL(url);
            String rawPath = s3Url.getPath().substring(1); // /user/xxx.png → user/xxx.png
            return URLDecoder.decode(rawPath, StandardCharsets.UTF_8);
        } catch (MalformedURLException e) {
            throw new ServerException(ExceptionCode.FILE_DELETE_FAILED);
        }
    }

    public void validateExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ServerException(ExceptionCode.INVALID_FILE_FORMAT);
        }

        String ext =
                originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!s3Properties.getUpload().getAllowedExtensions().contains(ext)) {
            throw new ServerException(ExceptionCode.INVALID_FILE_FORMAT);
        }
    }

    public void validateFileSize(MultipartFile file) {
        if (file.getSize() > s3Properties.getUpload().getMaxSize()) {
            throw new ServerException(ExceptionCode.FILE_SIZE_EXCEEDED);
        }
    }
}
