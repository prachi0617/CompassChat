package com.compasschat.common.base;

public record ApiResponse<T>(boolean success, String message, T data) {

    /** Happy path with a goodie inside. */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Operation successful", data);
    }

    /** Happy path with a custom note. */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /** Something went wrong; empty envelope with an explanation. */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
