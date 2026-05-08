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

    private HttpHeaders adminHeaders;

    @Before
    public void before() throws Exception {
        adminHeaders = getAdminHeaders();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testGetNotificationsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/notifications?username=admin").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetNotificationsReturnsArray() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/notifications?username=admin").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.startsWith("["));
    }

    @Test
    public void testGetNotificationsEachHasId() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/notifications?username=admin").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject notification = jsonArray.getJSONObject(i);
            assertTrue(notification.has("id"));
        }
    }

    @Test
    public void testGetNotificationsEachHasContent() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/notifications?username=admin").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject notification = jsonArray.getJSONObject(i);
            assertTrue(notification.has("content"));
        }
    }

    @Test
    public void testGetNotificationsEachHasIsRead() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/notifications?username=admin").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject notification = jsonArray.getJSONObject(i);
            assertTrue(notification.has("isRead"));
        }
    }

    @Test
    public void testGetNotificationsUserNotFoundReturnsEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/notifications?username=nonexistent_user_xyz").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertEquals(0, jsonArray.length());
    }

    @Test
    public void testGetNotificationsMissingUsernameReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/notifications").headers(adminHeaders))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testMarkAsReadSuccess() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/notifications/1/read").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals("success", rs);
    }

    @Test
    public void testMarkAsReadNotFoundReturnsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/notifications/99999/read").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals("success", rs);
    }

    @Test
    public void testMarkAllAsReadSuccess() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/notifications/mark-all-read?username=admin").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals("success", rs);
    }

    @Test
    public void testMarkAllAsReadUserNotFoundReturnsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/notifications/mark-all-read?username=nonexistent").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals("success", rs);
    }

    @Test
    public void testMarkAllAsReadMissingUsernameReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/notifications/mark-all-read").headers(adminHeaders))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}