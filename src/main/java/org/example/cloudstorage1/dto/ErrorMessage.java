package org.example.cloudstorage1.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorMessage {
    USERNAME_ALREADY_EXISTS("Данный логин уже занят"),
    WRONG_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    ACCOUNT_IS_DISABLED("Аккаунт заблокирован"),
    SYSTEM_ERROR("Произошла ошибка, повторите позже"),
    INVALID_FORMAT("Неверный формат логина или пароля");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    @JsonValue
    public String getMessage() {
        return message;
    }

}
