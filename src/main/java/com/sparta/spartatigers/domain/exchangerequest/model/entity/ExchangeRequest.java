package com.sparta.spartatigers.domain.exchangerequest.model.entity;

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

    public enum ExchangeStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}
