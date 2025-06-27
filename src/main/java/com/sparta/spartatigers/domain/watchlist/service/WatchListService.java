package com.sparta.spartatigers.domain.watchlist.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.favoriteteam.model.entity.FavoriteTeam;
import com.sparta.spartatigers.domain.favoriteteam.repository.FavoriteTeamRepository;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.match.repository.MatchRepository;
import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.SearchWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.UpdateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.CreateWatchListImageResponseDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.CreateWatchListResponseDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.StatsResponseDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.WatchListResponseDto;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchListFile;
import com.sparta.spartatigers.domain.watchlist.repository.WatchListFileRepository;
import com.sparta.spartatigers.domain.watchlist.repository.WatchListRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;
import com.sparta.spartatigers.global.exception.ServerException;
import com.sparta.spartatigers.global.util.FileUtil;
import com.sparta.spartatigers.global.util.S3FolderType;
import com.sparta.spartatigers.global.util.S3Properties;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class WatchListService {

    private final WatchListRepository watchListRepository;
    private final MatchRepository matchRepository;
    private final FavoriteTeamRepository favoriteTeamRepository;
    private final UserRepository userRepository;
    private final FileUtil fileUtil;
    private final AmazonS3 amazonS3;
    private final S3Properties s3Properties;
    private final WatchListFileRepository watchListFileRepository;

    /**
     * 직관 기록 등록 서비스
     *
     * @param request 유저 요청 객체
     * @param principal 유저 정보
     * @return {@link CreateWatchListResponseDto}
     */
    @Transactional
    public CreateWatchListResponseDto create(
            CreateWatchListRequestDto request, CustomUserPrincipal principal) {

        Match match = matchRepository.findByIdWithTeamsAndStadium(request.getMatch().getId());
        if (match == null) {
            throw new InvalidRequestException(ExceptionCode.MATCH_NOT_FOUND);
        }
        WatchList watchList = WatchList.from(match, request, principal.getUser());

        watchListRepository.save(watchList);

        // HTML 내 img 태그 값을 통해서 watchlist 번호 매칭
        List<String> imageUrls = extractImageUrls(request.getRecord().getContent());

        List<WatchListFile> files = watchListFileRepository.findAllByFileUrlIn(imageUrls);

        for (WatchListFile file : files) {
            file.setWatchList(watchList);
            file.setUsed(true);
        }

        return CreateWatchListResponseDto.of(watchList);
    }

    private List<String> extractImageUrls(String content) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = Pattern.compile("<img[^>]+src=[\"']?([^\"'>]+)[\"']?").matcher(content);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }

    /**
     * 직관 기록 다건 조회 서비스
     *
     * @param pageable 페이지 정보
     * @param principal 유저 정보
     * @return {@link WatchListResponseDto} 페이지
     */
    @Transactional(readOnly = true)
    public Page<WatchListResponseDto> findAll(Pageable pageable, CustomUserPrincipal principal) {

        Long userId = CustomUserPrincipal.getUserId(principal);
        Page<WatchList> all = watchListRepository.findAllByUserIdWithMatchDetails(userId, pageable);

        return all.map(WatchListResponseDto::of);
    }

    /**
     * 직관 기록 단건 조회 서비스
     *
     * @param watchListId 직관 기록 식별자
     * @param principal 유저 정보
     * @return {@link WatchListResponseDto}
     */
    @Transactional(readOnly = true)
    public WatchListResponseDto findOne(Long watchListId, CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);
        WatchList findWatchList =
                watchListRepository.findDetailByIdAndOwnerOrThrow(watchListId, userId);

        return WatchListResponseDto.of(findWatchList);
    }

    /**
     * 직관 기록 수정 서비스
     *
     * @param watchListId 직관 기록 식별자
     * @param request 요청 DTO
     * @param principal 유저 정보
     * @return {@link WatchListResponseDto}
     */
    @Transactional
    public WatchListResponseDto update(
            Long watchListId, UpdateWatchListRequestDto request, CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);
        WatchList findWatchList =
                watchListRepository.findDetailByIdAndOwnerOrThrow(watchListId, userId);

        findWatchList.update(request.getRecord().getContent(), request.getRecord().getRate());

        return WatchListResponseDto.of(findWatchList);
    }

    /**
     * 직관 기록 삭제
     *
     * @param watchListId 직관 기록 식별자
     * @param principal 유저 정보
     */
    @Transactional
    public void delete(Long watchListId, CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);
        WatchList findWatchList =
                watchListRepository.findDetailByIdAndOwnerOrThrow(watchListId, userId);

        watchListRepository.delete(findWatchList);
    }

    /**
     * 직관 기록 검색 서비스
     *
     * @param request 검색 요청 객체
     * @param principal 유저 정보
     * @return {@link Page<WatchListResponseDto>}
     */
    @Transactional(readOnly = true)
    public Page<WatchListResponseDto> search(
            Pageable pageable, SearchWatchListRequestDto request, CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);
        Page<WatchList> all =
                watchListRepository.findAllByKeyword(
                        userId, request.getTeamName(), request.getStadiumName(), pageable);

        return all.map(WatchListResponseDto::of);
    }

    /**
     * 응원하는 팀에 한정하여 직관 통계 데이터를 제공하는 서비스
     *
     * @param principal 유저 정보
     * @return {@link StatsResponseDto}
     */
    @Transactional(readOnly = true)
    public StatsResponseDto getStats(CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);

        User user = userRepository.findByIdOrElseThrow(userId);
        FavoriteTeam favoriteTeam = favoriteTeamRepository.findByUserIdOrElseThrow(userId);
        Team myTeam = favoriteTeam.getTeam();

        List<WatchList> watchLists = watchListRepository.findAllByUser(user);

        StatsAccumulator accumulator = new StatsAccumulator(myTeam);
        for (WatchList wl : watchLists) {
            accumulator.accumulate(wl);
        }

        return StatsResponseDto.of(accumulator);
    }

    /*
    파일 업로드
     */
    @Transactional
    public CreateWatchListImageResponseDto upload(MultipartFile file, Long userId) {
        fileUtil.validateExtension(file);
        fileUtil.validateFileSize(file);

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        String folderPath = s3Properties.getFolderPath(S3FolderType.RECORD);
        String originalFileName = file.getOriginalFilename();

        String fileName = fileUtil.createFileName(folderPath, originalFileName, user.getId());
        String bucket = s3Properties.getBucket();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new ServerException(ExceptionCode.FILE_UPLOAD_FAILED);
        }
        String fileUrl = amazonS3.getUrl(bucket, fileName).toString();

        WatchListFile image = WatchListFile.create(fileName, fileUrl, originalFileName, userId);
        watchListFileRepository.save(image);

        return new CreateWatchListImageResponseDto(fileUrl);
    }
}
