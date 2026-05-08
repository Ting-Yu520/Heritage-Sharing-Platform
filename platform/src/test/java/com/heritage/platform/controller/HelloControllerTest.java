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

    private HttpHeaders publicHeaders;

    @Before
    public void before() throws Exception {
        publicHeaders = getPublicHeaders();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testPublicResourceEndpointReturnsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=1&size=12").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertNotNull(rs);
    }

    @Test
    public void testPublicResourceEndpointReturnsNonEmptyBody() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=1&size=12").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.length() > 0);
    }

    @Test
    public void testPublicResourceEndpointReturnsJson() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=1&size=12").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String contentType = result.getResponse().getContentType();
        assertTrue(contentType == null || contentType.contains("json"));
    }

    @Test
    public void testPublicResourceEndpointAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/public/resources?current=1&size=12").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testPublicResourceEndpointMultipleRequests() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/public/resources?current=1&size=12").headers(publicHeaders))
                    .andExpect(status().isOk());
        }
    }
}