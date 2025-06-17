package com.sparta.spartatigers.domain.item.model.entity;

import java.time.LocalDate;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;
import com.sparta.spartatigers.domain.item.dto.request.CreateItemRequestDto;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

import com.fasterxml.jackson.annotation.JsonCreator;

@Entity(name = "items")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "UNIQUE_USER_ITEM",
                    columnNames = {"user_id", "created_date"})
        })
public class Item extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column private String image;

    @Column private String seatInfo;

    @Column private String title;

    @Column private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column private LocalDate createdDate;

    public static Item of(CreateItemRequestDto dto, User user) {
        return new Item(
                dto.category(),
                dto.image(),
                dto.seatInfo(),
                dto.title(),
                dto.description(),
                Status.REGISTERED,
                user,
                LocalDate.now());
    }

    public void validateSenderIsNotOwner(User sender) {

        if (this.user.getId().equals(sender.getId())) {
            throw new ServerException(ExceptionCode.CANNOT_REQUEST_OWN_ITEM);
        }
    }

    public void validateReceiverIsOwner(User receiver) {

        if (!this.user.getId().equals(receiver.getId())) {
            throw new ServerException(ExceptionCode.RECEIVER_NOT_OWNER);
        }
    }

    public void complete() {
        this.status = Status.COMPLETED;
    }

    public void fail() {
        this.status = Status.FAILED;
    }

    public enum Category {
        GOODS,
        TICKET;

        @JsonCreator
        public static Category parsing(String inputValue) {
            return Stream.of(Category.values())
                    .filter(category -> category.toString().equals(inputValue.toUpperCase()))
                    .findFirst()
                    .orElse(null);
        }
    }

    public enum Status {
        REGISTERED,
        COMPLETED,
        FAILED
    }
}
