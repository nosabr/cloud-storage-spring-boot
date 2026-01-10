package org.example.cloudstorage1.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.dto.ErrorMessage;
import org.example.cloudstorage1.dto.ErrorResponse;
import org.example.cloudstorage1.exception.FolderConflictException;
import org.example.cloudstorage1.exception.FolderNotFoundException;
import org.example.cloudstorage1.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled() {
        log.warn("DisabledException");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ErrorMessage.ACCOUNT_IS_DISABLED));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException() {
        log.warn("MethodArgumentNotValidException");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorMessage.INVALID_FORMAT));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException() {
        log.warn("BadCredentialsException");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorMessage.INVALID_FORMAT));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException() {
        log.warn("UsernameAlreadyUsedException");
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ErrorMessage.USERNAME_ALREADY_EXISTS));
    }

    @ExceptionHandler(FolderConflictException.class)
    public ResponseEntity<ErrorResponse> handleFolderConflictException() {
        log.warn("FolderConflictException");
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ErrorMessage.FOLDER_ALREADY_EXISTS));
    }

    @ExceptionHandler(FolderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFolderNotFoundException() {
        log.warn("FolderNotFoundException");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ErrorMessage.FOLDER_NOT_FOUND));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException() {
        log.warn("AuthenticationException");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ErrorMessage.WRONG_LOGIN_OR_PASSWORD));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException() {
        log.warn("GenericException");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ErrorMessage.SYSTEM_ERROR));
    }
}
