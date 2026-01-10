package org.example.cloudstorage1.exception;

public class FolderConflictException extends RuntimeException{
    public FolderConflictException(String message){
        super("Conflict exception: " + message);
    }
}
