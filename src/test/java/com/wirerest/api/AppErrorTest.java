package com.wirerest.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppErrorTest {
    AppError appError;

    @BeforeEach
    void setUp() {
        appError = new AppError(1, "test");
    }


    @Test
    void getCode() {
        Assertions.assertEquals(1, appError.getCode());
    }

    @Test
    void getMessage() {
        Assertions.assertEquals("test", appError.getMessage());
    }

    @Test
    void setCode() {
        appError.setCode(0);
        Assertions.assertEquals(0, appError.getCode());
    }

    @Test
    void setMessage() {
        appError.setMessage("test2");
        Assertions.assertEquals("test2", appError.getMessage());
    }
}