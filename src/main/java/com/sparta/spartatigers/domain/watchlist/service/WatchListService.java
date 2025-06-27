package com.sparta.spartatigers.domain.watchlist.service;

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
import com.sparta.spartatigers.global.service.S3Service;
import com.sparta.spartatigers.global.util.FileUtil;
import com.sparta.spartatigers.global.util.S3FolderType;
import com.sparta.spartatigers.global.util.S3Properties;

import com.amazonaws.services.s3.AmazonS3;

@Service
@RequiredArgsConstructor
public class WatchListService {

    private final WatchListRepository watchListRepository;
    private final MatchRepository matchRepository;
    private final FavoriteTeamRepository favoriteTeamRepository;
    private final UserRepository userRepository;
    private final FileUtil fileUtil;
    private final S3Properties s3Properties;
    private final WatchListFileRepository watchListFileRepository;
    private final S3Service s3Service;

    /**
     * 직관 기록 등록 서비스
     *
     * @param request 유저 요청 객체
     * @param authUser 유저 객체
     * @return {@link CreateWatchListResponseDto}
     */
    @Transactional
    public CreateWatchListResponseDto create(CreateWatchListRequestDto request, User authUser) {
        Match match = matchRepository.findByIdWithTeamsAndStadium(request.getMatch().getId());
        if (match == null) {
            throw new InvalidRequestException(ExceptionCode.MATCH_NOT_FOUND);
        }
        WatchList watchList = WatchList.from(match, request, authUser);

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
     * @param userId 유저 아이디
     * @return {@link WatchListResponseDto} 페이지
     */
    @Transactional(readOnly = true)
    public Page<WatchListResponseDto> findAll(Pageable pageable, Long userId) {
        Page<WatchList> all = watchListRepository.findAllByUserIdWithMatchDetails(userId, pageable);

        return all.map(WatchListResponseDto::of);
    }

    /**
     * 직관 기록 단건 조회 서비스
     *
     * @param watchListId 직관 기록 식별자
     * @param userId 유저 아이디
     * @return {@link WatchListResponseDto}
     */
    @Transactional(readOnly = true)
    public WatchListResponseDto findOne(Long watchListId, Long userId) {
        WatchList findWatchList =
                watchListRepository.findDetailByIdAndOwnerOrThrow(watchListId, userId);

        return WatchListResponseDto.of(findWatchList);
    }

    /**
     * 직관 기록 수정 서비스
     *
     * @param watchListId 직관 기록 식별자
     * @param request 요청 DTO
     * @param userId 유저 아이디
     * @return {@link WatchListResponseDto}
     */
    @Transactional
    public WatchListResponseDto update(
            Long watchListId, UpdateWatchListRequestDto request, Long userId) {
        WatchList findWatchList =
                watchListRepository.findDetailByIdAndOwnerOrThrow(watchListId, userId);

        findWatchList.update(request.getRecord().getContent(), request.getRecord().getRate());

        return WatchListResponseDto.of(findWatchList);
    }

    /**
     * 직관 기록 삭제
     *
     * @param watchListId 직관 기록 식별자
     * @param userId 유저 아이디
     */
    @Transactional
    public void delete(Long watchListId, Long userId) {
        WatchList findWatchList =
                watchListRepository.findDetailByIdAndOwnerOrThrow(watchListId, userId);

        watchListRepository.delete(findWatchList);
    }

    /**
     * 직관 기록 검색 서비스
     *
     * @param request 검색 요청 객체
     * @param userId 유저 아이디
     * @return {@link Page<WatchListResponseDto>}
     */
    @Transactional(readOnly = true)
    public Page<WatchListResponseDto> search(
            Pageable pageable, SearchWatchListRequestDto request, Long userId) {
        Page<WatchList> all =
                watchListRepository.findAllByKeyword(
                        userId, request.getTeamName(), request.getStadiumName(), pageable);

        return all.map(WatchListResponseDto::of);
    }

    /**
     * 응원하는 팀에 한정하여 직관 통계 데이터를 제공하는 서비스
     *
     * @param userId 유저 아이디
     * @return {@link StatsResponseDto}
     */
    @Transactional(readOnly = true)
    public StatsResponseDto getStats(Long userId) {
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
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        String fileUrl = s3Service.uploadFile(file, S3FolderType.RECORD, userId);
        String folderPath = s3Properties.getFolderPath(S3FolderType.RECORD);
        String originalFileName = file.getOriginalFilename();
        String fileName = fileUtil.createFileName(folderPath, originalFileName, user.getId());

        WatchListFile image = WatchListFile.create(fileName, fileUrl, originalFileName, userId);
        watchListFileRepository.save(image);

        return new CreateWatchListImageResponseDto(fileUrl);
    }
}
