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

    private HttpHeaders httpHeaders;

    @Before
    public void before() throws Exception {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("content-type", "application/json;charset=UTF-8");
        httpHeaders.add("origin", "Access-Control-Allow-Origin");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testGetResourceComments() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources/1/comments").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONArray jsonArray = new JSONArray(rs);
        assert jsonArray.length() >= 0;
    }

    @Test
    public void testAddComment1() throws Exception {
        String commentJson = "{\"resourceId\": 1, \"username\": \"testuser\", \"content\": \"Test comment\", \"parentId\": 0}";
        MvcResult result = mockMvc.perform(post("/api/comments").headers(httpHeaders).content(commentJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
        assertEquals("Comment posted successfully!", jsonObject.getString("message"));
    }
    @Test
    public void testAddComment2() throws Exception {
        String commentJson = "{\"resourceId\":\"4\",\"username\":\"guest01\",\"content\":\"666\",\"parentId\":9,\"replyTo\":\"admin\"}";

        MvcResult result = mockMvc.perform(post("/api/comments").headers(httpHeaders).content(commentJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
        assertEquals("Comment posted successfully!", jsonObject.getString("message"));
    }

    @Test
    public void testEditComment1() throws Exception {
        String updateJson = "{\"content\": \"Updated comment\"}";
        MvcResult result = mockMvc.perform(put("/api/comments/1").headers(httpHeaders).content(updateJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }
    @Test
    public void testEditComment2() throws Exception {
        String updateJson = "{\"content\": \"Updated comment\"}";
        MvcResult result = mockMvc.perform(put("/api/comments/999").headers(httpHeaders).content(updateJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertEquals("Comment does not exist or has been deleted!", jsonObject.getString("message"));
    }
    @Test
    public void testEditComment3() throws Exception {
        String updateJson = "{\"content\": \"Updated comment\"}";
        MvcResult result = mockMvc.perform(put("/api/comments/30").headers(httpHeaders).content(updateJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }


    @Test
    public void testDeleteComment() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/comments/1").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
        assertEquals("Comment deleted.", jsonObject.getString("message"));
    }

    @Test
    public void testCommentAction() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/comments/1/action?type=like").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertEquals("success", rs);
    }

    @Test
    public void testReportComment() throws Exception {
        String reportJson = "{\"commentId\": 1, \"reporterUsername\": \"testuser\", \"reason\": \"Test reason\"}";
        MvcResult result = mockMvc.perform(post("/api/comments/report").headers(httpHeaders).content(reportJson))
                .andExpect(status().isOk())
                .andReturn();

        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
        assertEquals("Report submitted. Thank you for helping maintain the community!", jsonObject.getString("message"));
    }

    @Test
    public void testGetPendingReports() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/comment-reports").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONArray jsonArray = new JSONArray(rs);
        assert jsonArray.length() >= 0;
    }

    @Test
    public void testProcessReport() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/admin/comment-reports/1?status=1").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }
}
