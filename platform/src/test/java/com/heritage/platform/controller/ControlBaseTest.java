package com.heritage.platform.controller;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Transactional
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ControlBaseTest {
    @Autowired
    protected WebApplicationContext wac;
    public MockMvc mockMvc;
}
