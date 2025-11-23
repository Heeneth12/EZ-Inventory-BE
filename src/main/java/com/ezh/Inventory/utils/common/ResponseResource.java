package com.ezh.Inventory.utils.common;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;

/**
 * Class Name : ResponseResource.java
 * Description : Custom Rest Response class with simple response structure
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseResource<T> {

    private int code;
    private String message;
    private T data;

    private ResponseResource() {}

    public static <T> ResponseResource<T> success(HttpStatus status, T data, String message) {
        ResponseResource<T> response = new ResponseResource<>();
        response.code = status.value();
        response.message = message;
        response.data = data;
        return response;
    }

    public static <T> ResponseResource<T> success(HttpStatus status, T data) {
        ResponseResource<T> response = new ResponseResource<>();
        response.code = status.value();
        response.message = HttpStatus.valueOf(status.value()).getReasonPhrase();
        response.data = data;
        return response;
    }

    public static <T> ResponseResource<T> error(HttpStatus status, String message) {
        ResponseResource<T> response = new ResponseResource<>();
        response.code = status.value();
        response.message = message;
        response.data = null;
        return response;
    }
}
