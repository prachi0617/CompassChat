package com.compasschat.common.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void shouldSetSuccessTrueAndDefaultMessage_whenOkWithDataOnly() {
        ApiResponse<String> response = ApiResponse.ok("payload");

        assertTrue(response.success());
        assertEquals("Operation successful", response.message());
        assertEquals("payload", response.data());
    }

    @Test
    void shouldSetSuccessTrueAndCustomMessage_whenOkWithMessage() {
        ApiResponse<Integer> response = ApiResponse.ok("Channel created", 42);

        assertTrue(response.success());
        assertEquals("Channel created", response.message());
        assertEquals(42, response.data());
    }

    @Test
    void shouldSetSuccessFalseAndNullData_whenError() {
        ApiResponse<Void> response = ApiResponse.error("Something went wrong");

        assertFalse(response.success());
        assertEquals("Something went wrong", response.message());
        assertNull(response.data());
    }

    @Test
    void shouldAllowNullData_whenOkWithNullData() {
        ApiResponse<String> response = ApiResponse.ok((String) null);

        assertTrue(response.success());
        assertNull(response.data());
    }
}
