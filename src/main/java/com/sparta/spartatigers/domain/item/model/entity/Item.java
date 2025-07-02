package com.sparta.spartatigers.domain.item.model.entity;

import java.time.LocalDate;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;
import com.sparta.spartatigers.domain.item.dto.request.CreateItemRequestDto;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

import com.fasterxml.jackson.annotation.JsonCreator;

@Entity(name = "items")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "UNIQUE_USER_ITEM",
                    columnNames = {"user_id", "created_date"})
        },
        indexes = {
            @Index(
                    name = "idx_item_user_status_created",
                    columnList = "user_id, status, createdAt DESC"),
            @Index(name = "idx_item_status_created_date", columnList = "status, createdDate")
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

    @Version private Long version;

    public Item(
            Item.Category category,
            String image,
            String seatInfo,
            String title,
            String description,
            Status status,
            User user,
            LocalDate createdDate) {

        this.category = category;
        this.image = image;
        this.seatInfo = seatInfo;
        this.title = title;
        this.description = description;
        this.status = status;
        this.user = user;
        this.createdDate = createdDate;
    }

    public static Item of(CreateItemRequestDto dto, User user, String image) {
        return new Item(
                dto.category(),
                image,
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

    public void validateUserIsOwner(User user) {

        if (!this.user.getId().equals(user.getId())) {
            throw new ServerException(ExceptionCode.ITEM_FORBIDDEN);
        }
    }

    public void complete() {
        this.status = Status.COMPLETED;
        this.createdDate = null;
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
