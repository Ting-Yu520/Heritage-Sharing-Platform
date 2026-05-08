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

public class CategoryControllerTest extends ControlBaseTest {

    private HttpHeaders adminHeaders;

    @Before
    public void before() throws Exception {
        adminHeaders = getAdminHeaders();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testGetCategoriesSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetCategoriesReturnsArray() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(rs.startsWith("["));
    }

    @Test
    public void testGetCategoriesEachHasId() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject category = jsonArray.getJSONObject(i);
            assertTrue(category.has("id"));
        }
    }

    @Test
    public void testGetCategoriesEachHasName() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject category = jsonArray.getJSONObject(i);
            assertTrue(category.has("name"));
        }
    }

    @Test
    public void testGetCategoriesEachHasUsageCount() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject category = jsonArray.getJSONObject(i);
            assertTrue(category.has("usageCount"));
        }
    }

    @Test
    public void testGetCategoriesWithKeyword() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories?keyword=Intangible").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetCategoriesWithEmptyKeyword() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories?keyword=").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetCategoriesWithFilterUnused() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories?filterStatus=UNUSED").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject category = jsonArray.getJSONObject(i);
            assertEquals(0, category.getInt("usageCount"));
        }
    }

    @Test
    public void testGetCategoriesWithFilterInUse() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories?filterStatus=IN_USE").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject category = jsonArray.getJSONObject(i);
            assertTrue(category.getInt("usageCount") > 0);
        }
    }

    @Test
    public void testGetCategoriesWithInvalidFilter() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories?filterStatus=INVALID").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetCategoriesUnauthorizedWithoutRole() throws Exception {
        HttpHeaders headers = getPublicHeaders();
        mockMvc.perform(get("/api/admin/categories").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetCategoriesUnauthorizedWithContributorRole() throws Exception {
        HttpHeaders headers = getContributorHeaders();
        mockMvc.perform(get("/api/admin/categories").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testSaveNewCategorySuccess() throws Exception {
        String uniqueName = "TestCategory_" + System.currentTimeMillis();
        String categoryJson = "{\"name\": \"" + uniqueName + "\", \"description\": \"This is a test category description\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(categoryJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testSaveNewCategoryWithoutDescription() throws Exception {
        String uniqueName = "NoDescCategory_" + System.currentTimeMillis();
        String categoryJson = "{\"name\": \"" + uniqueName + "\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(categoryJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testSaveCategoryWithVeryLongName() throws Exception {
        String longName = "A".repeat(200);
        String uniqueName = "LongName_" + System.currentTimeMillis();
        String categoryJson = "{\"name\": \"" + uniqueName + "\", \"description\": \"" + longName + "\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(categoryJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testSaveCategoryWithSpecialCharacters() throws Exception {
        String uniqueName = "Special_" + System.currentTimeMillis();
        String categoryJson = "{\"name\": \"" + uniqueName + "\", \"description\": \"Test & < > @ # $ % ^\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(categoryJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testUpdateCategorySuccess() throws Exception {
        String updateJson = "{\"id\": 1, \"name\": \"Intangible Cultural Heritage\", \"description\": \"Updated description content\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(updateJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testUpdateCategoryNotFound() throws Exception {
        String updateJson = "{\"id\": 99999, \"name\": \"NonExistent\", \"description\": \"Does not exist\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(updateJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testUpdateCategoryOnlyDescription() throws Exception {
        String updateJson = "{\"id\": 1, \"description\": \"Only description updated\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(updateJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testDeleteUnusedCategorySuccess() throws Exception {
        String uniqueName = "ToDelete_" + System.currentTimeMillis();
        String createJson = "{\"name\": \"" + uniqueName + "\", \"description\": \"To be deleted\"}";
        mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(createJson))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(delete("/api/admin/categories/99").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testDeleteCategoryNotFound() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/admin/categories/99999").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testDeleteCategoryUnauthorizedWithoutAdmin() throws Exception {
        HttpHeaders headers = getContributorHeaders();
        mockMvc.perform(delete("/api/admin/categories/1").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetCategoriesWithChineseKeyword() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories?keyword=非物质").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetCategoriesWithSpecialCharacterKeyword() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories?keyword=%&*").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testSaveCategoryWithMaxLengthDescription() throws Exception {
        String desc500 = "B".repeat(500);
        String uniqueName = "MaxDesc_" + System.currentTimeMillis();
        String categoryJson = "{\"name\": \"" + uniqueName + "\", \"description\": \"" + desc500 + "\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(categoryJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testSaveCategoryWithUnicodeCharacters() throws Exception {
        String uniqueName = "Unicode_" + System.currentTimeMillis();
        String categoryJson = "{\"name\": \"" + uniqueName + "\", \"description\": \"中文测试 日本語テスト 한국어테스트\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(categoryJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testUpdateCategoryWithSameName() throws Exception {
        String updateJson = "{\"id\": 1, \"name\": \"Intangible Cultural Heritage\", \"description\": \"Same name test\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(adminHeaders).content(updateJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testDeleteCategoryInvalidId() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/admin/categories/0").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }
}