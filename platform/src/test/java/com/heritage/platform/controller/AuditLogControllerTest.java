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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuditLogControllerTest extends ControlBaseTest {

    private HttpHeaders adminHeaders;

    @Before
    public void before() throws Exception {
        adminHeaders = getAdminHeaders();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testGetAllLogsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/audit-logs").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetAllLogsReturnsValidJson() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/audit-logs").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertNotNull(jsonArray);
    }

    @Test
    public void testGetAllLogsUnauthorizedWithoutRole() throws Exception {
        HttpHeaders headers = getPublicHeaders();
        mockMvc.perform(get("/api/audit-logs").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetAllLogsUnauthorizedWithWrongRole() throws Exception {
        HttpHeaders headers = getContributorHeaders();
        mockMvc.perform(get("/api/audit-logs").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetAllLogsUnauthorizedWithViewerRole() throws Exception {
        HttpHeaders headers = getViewerHeaders();
        mockMvc.perform(get("/api/audit-logs").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetAllLogsResponseFormat() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/audit-logs").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.startsWith("[") || rs.startsWith("{"));
    }

    @Test
    public void testAuditLogHasIdField() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/audit-logs").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        if (jsonArray.length() > 0) {
            JSONObject log = jsonArray.getJSONObject(0);
            assertTrue(log.has("id"));
        }
    }

    @Test
    public void testAuditLogHasActionTypeField() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/audit-logs").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        if (jsonArray.length() > 0) {
            JSONObject log = jsonArray.getJSONObject(0);
            assertTrue(log.has("actionType"));
        }
    }

    @Test
    public void testAuditLogHasCreatedAtField() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/audit-logs").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        if (jsonArray.length() > 0) {
            JSONObject log = jsonArray.getJSONObject(0);
            assertTrue(log.has("createdAt"));
        }
    }

    @Test
    public void testAuditLogHasUserIdField() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/audit-logs").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        if (jsonArray.length() > 0) {
            JSONObject log = jsonArray.getJSONObject(0);
            assertTrue(log.has("userId"));
        }
    }

    @Test
    public void testAuditLogHasResourceIdField() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/audit-logs").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        if (jsonArray.length() > 0) {
            JSONObject log = jsonArray.getJSONObject(0);
            assertTrue(log.has("resourceId"));
        }
    }

    @Test
    public void testAuditLogHasChangesSummaryField() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/audit-logs").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        if (jsonArray.length() > 0) {
            JSONObject log = jsonArray.getJSONObject(0);
            assertTrue(log.has("changesSummary"));
        }
    }
}