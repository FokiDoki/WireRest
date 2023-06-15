package com.wireguard.api.inteface;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InterfaceControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    MockMvc mockMvc;

    @Test
    void getInterface() throws Exception {
        mockMvc.perform(get("/interface"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.privateKey").isString())
                .andExpect(jsonPath("$.publicKey").isString())
                .andExpect(jsonPath("$.listenPort").isNumber())
                .andExpect(jsonPath("$.fwMark").isNumber());
    }
}