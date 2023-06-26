package com.wireguard.api;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

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