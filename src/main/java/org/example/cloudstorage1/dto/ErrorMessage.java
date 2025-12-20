package org.example.cloudstorage1.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorMessage {
    USERNAME_ALREADY_EXISTS("Данный логин уже занят"),
    EMAIL_ALREADY_EXISTS("Почта уже зарегистрирована в системе"),
    WRONG_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    ACCOUNT_IS_DISABLED("Аккаунт заблокирован"),
    SYSTEM_ERROR("Произошла ошибка, повторите позже");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    @JsonValue
    public String getMessage() {
        return message;
    }

}
