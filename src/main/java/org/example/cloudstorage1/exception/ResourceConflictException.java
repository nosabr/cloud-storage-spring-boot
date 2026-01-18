package org.example.cloudstorage1.exception;

public class ResourceConflictException extends RuntimeException{
    public ResourceConflictException(String message){
        super("Conflict exception: " + message);
    }
}
