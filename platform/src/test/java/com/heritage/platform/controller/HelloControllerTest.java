package com.heritage.platform.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HelloControllerTest extends ControlBaseTest {

    private HttpHeaders httpHeaders;

    @Before
    public void before() throws Exception {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("content-type", "application/json;charset=UTF-8");
        httpHeaders.add("origin", "Access-Control-Allow-Origin");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testSayHello() throws Exception {
        MvcResult result = mockMvc.perform(get("/hello").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertEquals("Awesome! The backend of my Heritage Curation Platform is up and running!", rs);
    }
}
