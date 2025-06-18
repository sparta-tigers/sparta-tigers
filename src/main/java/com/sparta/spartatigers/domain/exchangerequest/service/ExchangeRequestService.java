package com.sparta.spartatigers.domain.exchangerequest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

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

        requestItem.validateSenderIsNotOwner(sender);
        requestItem.validateReceiverIsOwner(receiver);

        checkDuplicateExchangeRequest(sender.getId(), receiver.getId(), requestItem.getId());

        ExchangeRequest exchangeRequest = ExchangeRequest.of(requestItem, sender, receiver);

        exchangeRequestRepository.save(exchangeRequest);
    }

    @Transactional(readOnly = true)
    public Page<SendRequestResponseDto> findAllSendRequest(
            CustomUserPrincipal principal, Pageable pageable) {

        User user = principal.getUser();

        Page<ExchangeRequest> exchangeRequestList =
                exchangeRequestRepository.findAllSendRequest(user.getId(), pageable);

        return exchangeRequestList.map(SendRequestResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<ReceiveRequestResponseDto> findAllReceiveRequest(
            CustomUserPrincipal principal, Pageable pageable) {

        User user = principal.getUser();

        Page<ExchangeRequest> exchangeRequestList =
                exchangeRequestRepository.findAllReceiveRequest(user.getId(), pageable);

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
        exchangeRequest.validateReceiverIsOwner(user);
        exchangeRequest.updateStatus(request.status());

        if (exchangeRequest.getStatus() == ExchangeStatus.ACCEPTED) {
            directRoomService.createRoom(exchangeRequestId, user.getId());
        }
    }

    @Transactional
    public void completeExchange(Long exchangeRequestId, CustomUserPrincipal principal) {

        User user = principal.getUser();

        ExchangeRequest exchangeRequest =
                exchangeRequestRepository.findAcceptedRequestByIdOrElseThrow(exchangeRequestId);
        exchangeRequest.validateReceiverIsOwner(user);

        Item item = itemRepository.findItemByIdOrElseThrow(exchangeRequest.getItem().getId());
        item.complete();
        exchangeRequest.complete();

        directRoomService.deleteRoomByExchangeRequestId(exchangeRequestId);

        locationService.deleteLocation(user.getId());
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
