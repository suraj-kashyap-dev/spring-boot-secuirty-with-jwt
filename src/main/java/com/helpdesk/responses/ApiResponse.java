package com.helpdesk.responses;

import lombok.Data;

import java.util.HashMap;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private HashMap<String, String> errors;
    private Meta meta;

    @Data
    public static class Meta {
        private int total;
        private int page;
        private int perPage;

        public Meta() {}

        public Meta(int total, int page, int perPage) {
            this.total = total;
            this.page = page;
            this.perPage = perPage;
        }
    }

    public ApiResponse() {
        this.success = true;
        this.errors = new HashMap<>();
    }

    
    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success(String message, T data, Meta meta) {
        ApiResponse<T> response = success(message, data);
        response.setMeta(meta);
        return response;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    public static <T> ApiResponse<T> error(String message, HashMap<String, String> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrors(errors);
        return response;
    }
}
