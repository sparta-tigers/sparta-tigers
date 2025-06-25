package com.sparta.spartatigers.domain.exchangerequest.model.entity;

import java.util.stream.Stream;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;
import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

import com.fasterxml.jackson.annotation.JsonCreator;

@Entity(name = "exchange_request")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private ExchangeStatus status;

    public static ExchangeRequest of(Item item, User sender, User receiver) {

        return new ExchangeRequest(item, sender, receiver, ExchangeStatus.PENDING);
    }

    public void validateReceiverIsOwner(User receiver) {

        if (!this.receiver.getId().equals(receiver.getId())) {
            throw new ServerException(ExceptionCode.RECEIVER_FORBIDDEN);
        }
    }

    public void updateStatus(ExchangeStatus status) {
        this.status = status;
    }

    public void complete() {
        this.status = ExchangeStatus.COMPLETED;
    }

    public enum ExchangeStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        COMPLETED;

        @JsonCreator
        public static ExchangeStatus parsing(String inputValue) {
            return Stream.of(ExchangeStatus.values())
                    .filter(
                            exchangeStatus ->
                                    exchangeStatus.toString().equals(inputValue.toUpperCase()))
                    .findFirst()
                    .orElse(null);
        }
    }
}
