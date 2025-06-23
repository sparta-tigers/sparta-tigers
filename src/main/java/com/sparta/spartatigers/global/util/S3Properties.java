package com.sparta.spartatigers.global.util;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ConfigurationProperties(prefix = "cloud.aws.s3")
@Component
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class S3Properties {
    private String bucket;
    private Folders folders;
    private Upload upload;
    private String defaultImagePath;

    @Getter
    @Setter
    public static class Folders {
        private String user;
        private String record;
    }

    @Getter
    @Setter
    public static class Upload {
        private Long maxSize;
        private List<String> allowedExtensions;
    }

    public String getFolderPath(S3FolderType type) {
        return switch (type) {
            case USER -> folders.getUser();
            case RECORD -> folders.getRecord();
        };
    }
}
