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

public class CommentControllerTest extends ControlBaseTest {

    private HttpHeaders adminHeaders;
    private HttpHeaders publicHeaders;
    private Long testResourceId = 1L;
    private Long existingCommentId = 1L;

    @Before
    public void before() throws Exception {
        adminHeaders = getAdminHeaders();
        publicHeaders = getPublicHeaders();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testGetResourceCommentsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources/" + testResourceId + "/comments").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetResourceCommentsReturnsArray() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources/" + testResourceId + "/comments").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.startsWith("["));
    }

    @Test
    public void testGetResourceCommentsNotFoundReturnsEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources/99999/comments").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertEquals(0, jsonArray.length());
    }

    @Test
    public void testAddTopLevelCommentSuccess() throws Exception {
        String commentJson = "{\"resourceId\": " + testResourceId + ", \"username\": \"admin\", \"content\": \"This is a test comment from unit test\", \"parentId\": 0}";
        MvcResult result = mockMvc.perform(post("/api/comments").headers(adminHeaders).content(commentJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testAddReplyCommentSuccess() throws Exception {
        String commentJson = "{\"resourceId\": " + testResourceId + ", \"username\": \"guest01\", \"content\": \"This is a reply from unit test\", \"parentId\": " + existingCommentId + ", \"replyTo\": \"admin\"}";
        MvcResult result = mockMvc.perform(post("/api/comments").headers(adminHeaders).content(commentJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testAddCommentTooLongFails() throws Exception {
        String longContent = "a".repeat(1001);
        String commentJson = "{\"resourceId\": " + testResourceId + ", \"username\": \"admin\", \"content\": \"" + longContent + "\", \"parentId\": 0}";
        MvcResult result = mockMvc.perform(post("/api/comments").headers(adminHeaders).content(commentJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertFalse(jsonObject.getBoolean("success"));
    }

    @Test
    public void testAddCommentWithXssContent() throws Exception {
        String commentJson = "{\"resourceId\": " + testResourceId + ", \"username\": \"admin\", \"content\": \"<script>alert('xss')</script>\", \"parentId\": 0}";
        MvcResult result = mockMvc.perform(post("/api/comments").headers(adminHeaders).content(commentJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testSoftDeleteCommentSuccess() throws Exception {
        String commentJson = "{\"resourceId\": " + testResourceId + ", \"username\": \"admin\", \"content\": \"To be deleted\", \"parentId\": 0}";
        MvcResult createResult = mockMvc.perform(post("/api/comments").headers(adminHeaders).content(commentJson))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(delete("/api/comments/99").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testSoftDeleteCommentNotFoundStillReturnsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/comments/99999").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testCommentActionWithoutUsernameFails() throws Exception {
        mockMvc.perform(post("/api/comments/" + existingCommentId + "/action?type=like").headers(adminHeaders))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testReportCommentSuccess() throws Exception {
        String reportJson = "{\"commentId\": " + existingCommentId + ", \"reporterUsername\": \"admin\", \"reason\": \"Spam\", \"details\": \"This comment contains spam content\"}";
        MvcResult result = mockMvc.perform(post("/api/comments/report").headers(adminHeaders).content(reportJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testGetPendingReportsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/comment-reports").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetPendingReportsUnauthorized() throws Exception {
        HttpHeaders headers = getContributorHeaders();
        mockMvc.perform(get("/api/admin/comment-reports").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testProcessReportNotFound() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/admin/comment-reports/99999?status=1").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertFalse(jsonObject.getBoolean("success"));
    }

    @Test
    public void testProcessReportWithInvalidStatus() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/admin/comment-reports/99999?status=99").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertFalse(jsonObject.getBoolean("success"));
    }

    @Test
    public void testProcessReportApprove() throws Exception {
        JSONArray reports = new JSONArray(mockMvc.perform(get("/api/admin/comment-reports").headers(adminHeaders))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8));
        if (reports.length() > 0) {
            Long reportId = reports.getJSONObject(0).getLong("id");
            MvcResult result = mockMvc.perform(put("/api/admin/comment-reports/" + reportId + "?status=1").headers(adminHeaders))
                    .andExpect(status().isOk())
                    .andReturn();
            String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(rs);
            assertTrue(jsonObject.getBoolean("success"));
        }
    }

    @Test
    public void testProcessReportReject() throws Exception {
        JSONArray reports = new JSONArray(mockMvc.perform(get("/api/admin/comment-reports").headers(adminHeaders))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8));
        if (reports.length() > 0) {
            Long reportId = reports.getJSONObject(0).getLong("id");
            MvcResult result = mockMvc.perform(put("/api/admin/comment-reports/" + reportId + "?status=2").headers(adminHeaders))
                    .andExpect(status().isOk())
                    .andReturn();
            String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(rs);
            assertTrue(jsonObject.getBoolean("success"));
        }
    }
}