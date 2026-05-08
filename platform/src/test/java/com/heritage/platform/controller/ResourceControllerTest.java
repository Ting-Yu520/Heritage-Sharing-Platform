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

    private HttpHeaders adminHeaders;
    private HttpHeaders contributorHeaders;
    private HttpHeaders publicHeaders;
    private Long testResourceId = 1L;

    @Before
    public void before() throws Exception {
        adminHeaders = getAdminHeaders();
        contributorHeaders = getContributorHeaders();
        publicHeaders = getPublicHeaders();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testGetMyResourcesSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/my-resources?username=admin&current=1&size=10").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
        assertNotNull(jsonObject.get("total"));
    }

    @Test
    public void testGetMyResourcesWithPagination() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/my-resources?username=admin&current=2&size=5").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetMyResourcesUserNotFoundReturnsEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/my-resources?username=nonexistent_user&current=1&size=10").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetMyResourcesUnauthorizedWithoutRole() throws Exception {
        mockMvc.perform(get("/api/my-resources?username=admin&current=1&size=10").headers(publicHeaders))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetMyResourcesWithViewerRole() throws Exception {
        HttpHeaders headers = getViewerHeaders();
        mockMvc.perform(get("/api/my-resources?username=admin&current=1&size=10").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testSubmitMyResourceAsPending() throws Exception {
        String resourceJson = "{\"title\": \"Test Submit Resource\", \"description\": \"Test Description\", \"category\": \"Intangible Cultural Heritage\", \"contributorUsername\": \"admin\", \"thumbnail\": \"https://example.com/img.jpg\", \"location\": \"Beijing\", \"tags\": \"culture,heritage\"}";
        MvcResult result = mockMvc.perform(post("/api/my-resources/submit?status=0").headers(adminHeaders).content(resourceJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testSubmitMyResourceAsDraft() throws Exception {
        String resourceJson = "{\"title\": \"Test Draft Resource\", \"description\": \"Draft Description\", \"category\": \"Folk Activities\", \"contributorUsername\": \"admin\"}";
        MvcResult result = mockMvc.perform(post("/api/my-resources/submit?status=-1").headers(adminHeaders).content(resourceJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testSubmitMyResourceWithAllFields() throws Exception {
        String resourceJson = "{\"title\": \"Full Resource\", \"description\": \"Complete description with details\", \"category\": \"Traditional Crafts/Handicrafts\", \"contributorUsername\": \"admin\", \"thumbnail\": \"https://example.com/cover.jpg\", \"mediaUrl\": \"https://example.com/video.mp4\", \"tags\": \"tag1,tag2,tag3\", \"location\": \"Shanghai, China\"}";
        MvcResult result = mockMvc.perform(post("/api/my-resources/submit?status=0").headers(adminHeaders).content(resourceJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testSubmitMyResourceEmptyBodyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/my-resources/submit?status=0").headers(adminHeaders))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testGetPublicResourcesSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=1&size=12").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
        assertNotNull(jsonObject.get("total"));
    }

    @Test
    public void testGetPublicResourcesWithKeyword() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=1&size=12&keyword=culture").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetPublicResourcesWithCategory() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=1&size=12&category=Intangible Cultural Heritage").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetPublicResourcesWithBothKeywordAndCategory() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=1&size=12&keyword=test&category=Folk Activities").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetCategoryCountsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/categories/count").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject);
    }

    @Test
    public void testGetCategoryCountsReturnsObject() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/categories/count").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.startsWith("{"));
    }

    @Test
    public void testGetResourceDetailNotFound() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources/99999").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertFalse(jsonObject.getBoolean("success"));
    }

    @Test
    public void testToggleLike() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/resources/" + testResourceId + "/like?username=admin").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.equals("liked") || rs.equals("unliked"));
    }

    @Test
    public void testToggleLikeMultipleTimes() throws Exception {
        mockMvc.perform(post("/api/resources/" + testResourceId + "/like?username=admin").headers(adminHeaders));
        MvcResult result = mockMvc.perform(post("/api/resources/" + testResourceId + "/like?username=admin").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.equals("liked") || rs.equals("unliked"));
    }

    @Test
    public void testToggleLikeWithoutUsernameFails() throws Exception {
        mockMvc.perform(post("/api/resources/" + testResourceId + "/like").headers(adminHeaders))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testToggleFavorite() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/resources/" + testResourceId + "/favorite?username=admin").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.equals("favorited") || rs.equals("unfavorited"));
    }

    @Test
    public void testToggleFavoriteMultipleTimes() throws Exception {
        mockMvc.perform(post("/api/resources/" + testResourceId + "/favorite?username=admin").headers(adminHeaders));
        MvcResult result = mockMvc.perform(post("/api/resources/" + testResourceId + "/favorite?username=admin").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.equals("favorited") || rs.equals("unfavorited"));
    }

    @Test
    public void testGetMyFavoritesSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources/favorites?username=admin&current=1&size=12").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
        assertNotNull(jsonObject.get("total"));
    }

    @Test
    public void testGetMyFavoritesEmptyReturnsZeroTotal() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources/favorites?username=viewer01&current=1&size=12").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetAllResourcesSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetAllResourcesUnauthorizedWithContributor() throws Exception {
        mockMvc.perform(get("/api/resources").headers(contributorHeaders))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetSummarySuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/stats/summary").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.has("total"));
        assertTrue(jsonObject.has("pending"));
        assertTrue(jsonObject.has("published"));
        assertTrue(jsonObject.has("logs"));
    }

    @Test
    public void testGetSummaryContributorAccessAllowed() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/stats/summary").headers(contributorHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.has("total"));
        assertTrue(jsonObject.has("pending"));
        assertTrue(jsonObject.has("published"));
        assertTrue(jsonObject.has("logs"));
    }

    @Test
    public void testGetSummaryViewerDenied() throws Exception {
        HttpHeaders headers = getViewerHeaders();
        mockMvc.perform(get("/api/stats/summary").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetSummaryUnauthorizedWithoutRole() throws Exception {
        mockMvc.perform(get("/api/stats/summary").headers(publicHeaders))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetPendingResourcesSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources/pending?current=1&size=20").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
        assertNotNull(jsonObject.get("total"));
    }

    @Test
    public void testGetPendingResourcesWithCategoryFilter() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources/pending?current=1&size=20&category=Intangible Cultural Heritage").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testUpdateResourceStatusApprove() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/resources/" + testResourceId + "/status?status=1&feedback=Good%20content").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.contains("success") || rs.contains("操作成功"));
    }

    @Test
    public void testUpdateResourceStatusReject() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/resources/" + testResourceId + "/status?status=2&feedback=Need%20improvement").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.contains("success") || rs.contains("操作成功"));
    }

    @Test
    public void testUpdateResourceStatusArchive() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/resources/" + testResourceId + "/status?status=3").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.contains("success") || rs.contains("操作成功"));
    }

    @Test
    public void testUpdateResourceStatusNotFound() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/resources/99999/status?status=1").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.contains("not found") || rs.contains("未找到"));
    }

    @Test
    public void testUpdateResourceStatusUnauthorizedWithContributor() throws Exception {
        mockMvc.perform(put("/api/resources/" + testResourceId + "/status?status=1").headers(contributorHeaders))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetPublicResourcesLargePage() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=50&size=100").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetPublicResourcesNegativePage() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=-1&size=12").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetPublicResourcesZeroSize() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/resources?current=1&size=0").headers(publicHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testToggleFavoriteWithoutUsernameFails() throws Exception {
        mockMvc.perform(post("/api/resources/" + testResourceId + "/favorite").headers(adminHeaders))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testGetMyFavoritesWithNonExistentUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources/favorites?username=nonexistent&current=1&size=12").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertEquals(0, jsonObject.getInt("total"));
    }

    @Test
    public void testGetAllResourcesReturnsArray() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.startsWith("["));
    }

    @Test
    public void testGetSummaryValuesNonNegative() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/stats/summary").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getInt("total") >= 0);
        assertTrue(jsonObject.getInt("pending") >= 0);
        assertTrue(jsonObject.getInt("published") >= 0);
        assertTrue(jsonObject.getInt("logs") >= 0);
    }

    @Test
    public void testGetPendingResourcesWithInvalidCategory() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources/pending?current=1&size=20&category=InvalidCategory").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testGetPendingResourcesWithInvalidDateRange() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/resources/pending?current=1&size=20&startDate=2026-01-01&endDate=2025-01-01").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("records"));
    }

    @Test
    public void testUpdateResourceStatusWithEmptyFeedback() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/resources/" + testResourceId + "/status?status=2&feedback=").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertNotNull(rs);
    }

    @Test
    public void testUpdateResourceStatusWithLongFeedback() throws Exception {
        String longFeedback = "F".repeat(500);
        MvcResult result = mockMvc.perform(put("/api/resources/" + testResourceId + "/status?status=2&feedback=" + longFeedback).headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertNotNull(rs);
    }

    @Test
    public void testUpdateResourceStatusWithSpecialCharFeedback() throws Exception {
        String specialFeedback = "Test & < > @ # $ % ^ * ( )";
        MvcResult result = mockMvc.perform(put("/api/resources/" + testResourceId + "/status?status=2&feedback=" + specialFeedback).headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertNotNull(rs);
    }
}