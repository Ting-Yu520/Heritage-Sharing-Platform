package com.heritage.platform.controller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Transactional
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ControlBaseTest {

    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;

    protected HttpHeaders getAdminHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("X-User-Role", "ADMIN");
        headers.add("X-User-Username", "admin");
        return headers;
    }

    protected HttpHeaders getContributorHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("X-User-Role", "CONTRIBUTOR");
        headers.add("X-User-Username", "guest01");
        return headers;
    }

    protected HttpHeaders getViewerHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("X-User-Role", "VIEWER");
        headers.add("X-User-Username", "viewer01");
        return headers;
    }

    protected HttpHeaders getPublicHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        return headers;
    }

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
}