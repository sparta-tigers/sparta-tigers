package com.sparta.spartatigers.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.user.dto.request.LoginRequestDto;
import com.sparta.spartatigers.domain.user.dto.request.SignUpRequestDto;
import com.sparta.spartatigers.domain.user.dto.request.UpdateNicknameRequestDto;
import com.sparta.spartatigers.domain.user.dto.request.UpdatePasswordRequestDto;
import com.sparta.spartatigers.domain.user.dto.response.AuthResponseDto;
import com.sparta.spartatigers.domain.user.dto.response.ProfileResponseDto;
import com.sparta.spartatigers.domain.user.dto.response.UserInfoResponseDto;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;
import com.sparta.spartatigers.global.exception.ServerException;
import com.sparta.spartatigers.global.service.S3Service;
import com.sparta.spartatigers.global.util.FileUtil;
import com.sparta.spartatigers.global.util.JwtUtil;
import com.sparta.spartatigers.global.util.S3FolderType;
import com.sparta.spartatigers.global.util.S3Properties;

import com.amazonaws.services.s3.AmazonS3;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AmazonS3 amazonS3;
    private final S3Properties s3Properties;
    private final FileUtil fileUtil;
    private final S3Service s3Service;

    public UserInfoResponseDto getUserInfo(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));
        return UserInfoResponseDto.from(user);
    }

    @Transactional
    public void createUser(SignUpRequestDto signUpRequestDto) {
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new ServerException(ExceptionCode.EMAIL_ALREADY_USED);
        }

        if (userRepository.existsByNickname(signUpRequestDto.getNickname())) {
            throw new ServerException(ExceptionCode.NICKNAME_ALREADY_USED);
        }

        String defaultImagePath = s3Properties.getDefaultImagePath();
        User user = User.from(signUpRequestDto, encodedPassword, defaultImagePath);
        userRepository.save(user);
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        User user =
                userRepository
                        .findByEmail(loginRequestDto.getEmail())
                        .orElseThrow(
                                () -> new InvalidRequestException(ExceptionCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidRequestException(ExceptionCode.PASSWORD_NOT_MATCH);
        }
        String token = jwtUtil.generateToken(loginRequestDto.getEmail(), "ROLE_USER");
        return AuthResponseDto.from(token);
    }

    @Transactional
    public ProfileResponseDto updateProfile(MultipartFile file, Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        String currentImagePath = user.getPath();
        if (currentImagePath != null
                && !currentImagePath.endsWith(s3Properties.getDefaultImagePath())) {
            try {
                String key = fileUtil.extractS3KeyFromUrl(currentImagePath);
                amazonS3.deleteObject(s3Properties.getBucket(), key);
            } catch (Exception e) {
                throw new ServerException(ExceptionCode.FILE_DELETE_FAILED);
            }
        }

        String folderPath = s3Properties.getFolderPath(S3FolderType.USER);
        String originalFileName = file.getOriginalFilename();
        String fileName = fileUtil.createFileName(folderPath, originalFileName, user.getId());
        String filePath = s3Service.uploadFile(file, S3FolderType.USER, user.getId());

        user.updatePath(filePath);
        userRepository.save(user);

        return new ProfileResponseDto(fileName, filePath);
    }

    @Transactional
    public void deleteProfile(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        String currentImagePath = user.getPath();

        if (currentImagePath.endsWith(s3Properties.getDefaultImagePath())) {
            throw new InvalidRequestException(ExceptionCode.DEFAULT_IMAGE_CANNOT_BE_DELETED);
        }
        try {
            String key = fileUtil.extractS3KeyFromUrl(currentImagePath);
            amazonS3.deleteObject(s3Properties.getBucket(), key);
        } catch (Exception e) {
            throw new ServerException(ExceptionCode.FILE_DELETE_FAILED);
        }
        String defaultImageUrl =
                amazonS3.getUrl(s3Properties.getBucket(), s3Properties.getDefaultImagePath())
                        .toString();
        user.updatePath(defaultImageUrl);
        userRepository.save(user);
    }

    @Transactional
    public void updateNickname(UpdateNicknameRequestDto updateNicknameRequestDto, Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        user.updateNickname(updateNicknameRequestDto.getNickname());
    }

    @Transactional
    public void updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto, Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(
                updatePasswordRequestDto.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException(ExceptionCode.INVALID_PASSWORD);
        }

        if (passwordEncoder.matches(
                updatePasswordRequestDto.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException(ExceptionCode.SAME_AS_OLD_PASSWORD);
        }

        String encodedNewPassword =
                passwordEncoder.encode(updatePasswordRequestDto.getNewPassword());
        user.updatePassword(encodedNewPassword);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        user.deleted();
        userRepository.save(user);
    }

    public Long findUserIdByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND))
                .getId();
    }
}
