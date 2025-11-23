package com.ezh.Inventory.utils.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CommonException{

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND);
    }
}
