package com.heritage.platform.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NotificationControllerTest extends ControlBaseTest {

    private HttpHeaders httpHeaders;

    @Before
    public void before() throws Exception {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("content-type", "application/json;charset=UTF-8");
        httpHeaders.add("origin", "Access-Control-Allow-Origin");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testGetNotifications() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/notifications?username=testuser").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONArray jsonArray = new JSONArray(rs);
        assert jsonArray.length() >= 0;
    }

    @Test
    public void testMarkAsRead() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/notifications/1/read").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertEquals("success", rs);
    }

    @Test
    public void testMarkAllAsRead() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/notifications/mark-all-read?username=testuser").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertEquals("success", rs);
    }

    @Test
    public void testUpdatePreferences() throws Exception {
        String preferencesJson = "{\"notifyReview\": 1, \"notifyComment\": 1, \"notifySystem\": 1}";

        MvcResult result = mockMvc.perform(put("/api/users/preferences?username=admin").headers(httpHeaders).content(preferencesJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertEquals("Notification preferences saved!", jsonObject.get("message"));
    }
}
