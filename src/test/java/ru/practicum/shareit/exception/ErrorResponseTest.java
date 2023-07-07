package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void getError() {
        ErrorResponse errorResponse = new ErrorResponse("errorResp");
        assertEquals("errorResp", errorResponse.getError());
    }
}