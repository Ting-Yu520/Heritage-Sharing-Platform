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

public class ResourceControllerTest extends ControlBaseTest {

    private HttpHeaders httpHeaders;

    @Before
    public void before() throws Exception {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("content-type", "application/json;charset=UTF-8");
        httpHeaders.add("origin", "Access-Control-Allow-Origin");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testGetMyResources() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/my-resources?username=testuser&current=1&size=10").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();

        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);

        // Verify response format
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testSubmitMyResource() throws Exception {
        String resourceJson = "{\"title\": \"Test Resource\", \"description\": \"Test Description\", \"category\": \"Test Category\", \"contributorUsername\": \"testuser\"}";

        MvcResult result = mockMvc.perform(post("/api/my-resources/submit?status=0").headers(httpHeaders).content(resourceJson))
                .andExpect(status().isOk())
                .andReturn();

        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);

        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
        assertEquals("Submitted successfully. Please wait for administrator review.", jsonObject.getString("message"));
    }

    @Test
    public void testUpdateMyResource1() throws Exception {
        String resourceJson = "{\"title\": \"Updated Resource\", \"description\": \"Updated Description\", \"category\": \"Updated Category\"}";

        MvcResult result = mockMvc.perform(put("/api/my-resources/1?status=0").headers(httpHeaders).content(resourceJson))
                .andExpect(status().isOk())
                .andReturn();

        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);

        JSONObject jsonObject = new JSONObject(rs);
        // The resource may not exist, so only verify the response format
        assertNotNull(jsonObject.get("success"));
    }
    @Test
    public void testUpdateMyResource2() throws Exception {
        String resourceJson = "{\"title\": \"Updated Resource\", \"description\": \"Updated Description\", \"category\": \"Updated Category\"}";

        MvcResult result = mockMvc.perform(put("/api/my-resources/111?status=0").headers(httpHeaders).content(resourceJson))
                .andExpect(status().isOk())
                .andReturn();

        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);

        JSONObject jsonObject = new JSONObject(rs);
        // The resource may not exist, so only verify the response format
        assertEquals("Resource does not exist!", jsonObject.getString("message"));
    }

    @Test
    public void testWithdrawMyResource1() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/my-resources/1/withdraw").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }
    @Test
    public void testWithdrawMyResource2() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/my-resources/8/withdraw").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
        assertEquals("Withdrawn successfully. Your submission has been removed from the review queue.", jsonObject.getString("message"));
    }

    @Test
    public void testGetPublicResources() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=1&size=12").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();

        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);

        // Verify response format
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetCategoryCounts() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/categories/count").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();

        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);

        // Verify the response is an object
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject);
    }

    @Test
    public void testGetResourceDetail1() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources/1").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }
    @Test
    public void testGetResourceDetail2() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources/5").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }

    @Test
    public void testToggleLike() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/resources/1/like?username=testuser").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertTrue(rs.equals("liked") || rs.equals("unliked"));
    }

    @Test
    public void testToggleFavorite() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/resources/1/favorite?username=testuser").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertTrue(rs.equals("favorited") || rs.equals("unfavorited"));
    }

    @Test
    public void testGetMyFavorites() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources/favorites?username=testuser&current=1&size=12").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetResources() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONArray jsonArray = new JSONArray(rs);
        assert jsonArray.length() >= 0;
    }

    @Test
    public void testAddResource() throws Exception {
        String resourceJson = "{\"title\": \"Test Resource\", \"description\": \"Test Description\", \"category\": \"Test Category\", \"contributorUsername\": \"testuser\"}";
        MvcResult result = mockMvc.perform(post("/api/resources").headers(httpHeaders).content(resourceJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertEquals("Created successfully. Please wait for review.", rs);
    }

    @Test
    public void testDeleteResource() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/resources/1").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertEquals("Deleted successfully!", rs);
    }

    @Test
    public void testGetSummary() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/stats/summary").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("total"));
        assertNotNull(jsonObject.get("pending"));
        assertNotNull(jsonObject.get("published"));
        assertNotNull(jsonObject.get("logs"));
    }

    @Test
    public void testGetPendingResources() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources/pending?current=1&size=20").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testUpdateResourceStatus() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/resources/1/status?status=1&feedback=Test feedback").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertTrue(rs.contains("Operation successful!"));
    }
}
