package ru.creditbank.common.library.enums;

import lombok.Getter;

public enum CreditStatusEnum {
    PENDING("В ожидании"),
    APPROVED("Одобрено"),
    REJECTED("Отказано");

    @Getter
    private final String description;

    CreditStatusEnum(String description) {
        this.description = description;
    }
}
