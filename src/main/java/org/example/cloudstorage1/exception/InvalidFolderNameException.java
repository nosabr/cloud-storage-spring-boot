package org.example.cloudstorage1.exception;

public class InvalidFolderNameException extends RuntimeException {
    public InvalidFolderNameException(String message) {
        super(message);
    }
}
