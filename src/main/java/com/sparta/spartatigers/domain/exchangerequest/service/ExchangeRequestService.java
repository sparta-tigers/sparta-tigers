package com.sparta.spartatigers.domain.exchangerequest.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.service.DirectRoomService;
import com.sparta.spartatigers.domain.exchangerequest.dto.request.ExchangeRequestDto;
import com.sparta.spartatigers.domain.exchangerequest.dto.request.UpdateExchangeRequestDto;
import com.sparta.spartatigers.domain.exchangerequest.dto.response.ReceiveRequestResponseDto;
import com.sparta.spartatigers.domain.exchangerequest.dto.response.SendRequestResponseDto;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest.ExchangeStatus;
import com.sparta.spartatigers.domain.exchangerequest.repository.ExchangeRequestRepository;
import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.repository.ItemRepository;
import com.sparta.spartatigers.domain.item.service.LocationService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRequestService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final DirectRoomService directRoomService;
    private final LocationService locationService;

    @Transactional
    public void createExchangeRequest(ExchangeRequestDto request, CustomUserPrincipal principal) {

        User sender = principal.getUser();
        User receiver = getReceiver(request.receiverId());
        Item requestItem = itemRepository.findByIdWithLockOrElseThrow(request.itemId());
        log.debug(
                "[createExchangeRequest] senderId: {}, receiverId: {}, itemId: {}",
                sender.getId(),
                receiver.getId(),
                requestItem.getId());

        requestItem.validateSenderIsNotOwner(sender);
        requestItem.validateReceiverIsOwner(receiver);

        checkDuplicateExchangeRequest(sender.getId(), receiver.getId(), requestItem.getId());

        ExchangeRequest exchangeRequest = ExchangeRequest.of(requestItem, sender, receiver);
        exchangeRequestRepository.save(exchangeRequest);
        log.info(
                "[createExchangeRequest] 교환 요청 완료 - exchangeRequestId: {}, itemId: {}",
                exchangeRequest.getId(),
                requestItem.getId());
    }

    @Transactional(readOnly = true)
    public Page<SendRequestResponseDto> findAllSendRequest(
            CustomUserPrincipal principal, Pageable pageable) {

        User user = principal.getUser();
        log.debug("[findAllSendRequest] 사용자 ID: {}", user.getId());

        Page<ExchangeRequest> exchangeRequestList =
                exchangeRequestRepository.findAllSendRequest(user.getId(), pageable);
        log.info("[findAllSendRequest] 보낸 요청 수: {}", exchangeRequestList.getTotalElements());

        return exchangeRequestList.map(SendRequestResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<ReceiveRequestResponseDto> findAllReceiveRequest(
            CustomUserPrincipal principal, Pageable pageable) {

        User user = principal.getUser();
        log.debug("[findAllReceiveRequest] 사용자 ID: {}", user.getId());

        Page<ExchangeRequest> exchangeRequestList =
                exchangeRequestRepository.findAllReceiveRequest(user.getId(), pageable);
        log.info("[findAllReceiveRequest] 받은 요청 수: {}", exchangeRequestList.getTotalElements());

        return exchangeRequestList.map(ReceiveRequestResponseDto::from);
    }

    @Transactional
    public void updateRequestStatus(
            Long exchangeRequestId,
            UpdateExchangeRequestDto request,
            CustomUserPrincipal principal) {

        User user = principal.getUser();
        ExchangeRequest exchangeRequest =
                exchangeRequestRepository.findExchangeRequestByIdOrElseThrow(exchangeRequestId);
        log.debug(
                "[updateRequestStatus] 요청 상태 변경 - requestId: {}, userId: {}",
                exchangeRequestId,
                user.getId());

        exchangeRequest.validateReceiverIsOwner(user);
        exchangeRequest.updateStatus(request.status());

        if (exchangeRequest.getStatus() == ExchangeStatus.ACCEPTED) {
            directRoomService.createRoom(exchangeRequestId, user.getId());
            log.info(
                    "[updateRequestStatus] 교환 요청 수락 - room 생성 완료, requestId: {}",
                    exchangeRequestId);
        }

        if (exchangeRequest.getStatus() == ExchangeStatus.REJECTED) {
            exchangeRequestRepository.delete(exchangeRequest);
            log.info(
                    "[updateRequestStatus] 교환 요청 거절 - 교환 요청 삭제 완료, requestId: {}",
                    exchangeRequestId);
        }
    }

    @Transactional
    public void completeExchange(Long exchangeRequestId, CustomUserPrincipal principal) {

        User user = principal.getUser();

        ExchangeRequest exchangeRequest =
                exchangeRequestRepository.findAcceptedRequestByIdOrElseThrow(exchangeRequestId);
        exchangeRequest.validateReceiverIsOwner(user);

        Item item = itemRepository.findItemByIdOrElseThrow(exchangeRequest.getItem().getId());
        log.debug(
                "[completeExchange] 교환 완료 - itemId: {}, requestId: {}, userId: {}",
                item.getId(),
                exchangeRequestId,
                user.getId());
        item.complete();

        List<Long> exchangeRequestIds =
                exchangeRequestRepository.findAllExchangeRequestIds(item.getId());

        for (Long requestId : exchangeRequestIds) {
            directRoomService.deleteRoomByExchangeRequestId(requestId);
            log.debug("[completeExchange] 관련 채팅방 삭제 - requestId: {}", requestId);
        }
        exchangeRequest.complete();

        exchangeRequestRepository.deleteAllByItemId(item.getId());

        Map<String, Object> data = Map.of("itemId", item.getId(), "userId", item.getUser().getId());
        locationService.notifyUsersNearBy(item.getUser().getId(), "REMOVE_ITEM", data);

        log.info(
                "[completeExchange] 교환 완료 - itemId: {}, requestId: {}",
                item.getId(),
                exchangeRequestId);
    }

    private User getReceiver(Long receiverId) {

        return userRepository
                .findById(receiverId)
                .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));
    }

    private void checkDuplicateExchangeRequest(Long senderId, Long receiverId, Long itemId) {

        boolean isExisted =
                exchangeRequestRepository.findExchangeRequest(senderId, receiverId, itemId);

        if (isExisted) {
            throw new ServerException(ExceptionCode.EXCHANGE_REQUEST_DUPLICATED);
        }
    }
}
