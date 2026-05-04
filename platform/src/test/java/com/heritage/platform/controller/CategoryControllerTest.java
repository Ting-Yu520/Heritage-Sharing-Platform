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

    private HttpHeaders httpHeaders;

    @Before
    public void before() throws Exception {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("content-type", "application/json;charset=UTF-8");
        httpHeaders.add("origin", "Access-Control-Allow-Origin");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testGetCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/categories").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONArray jsonArray = new JSONArray(rs);
        assert jsonArray.length() >= 0;
    }

    @Test
    public void testSaveCategory1() throws Exception {
        String categoryJson = "{\"name\": \"TestCategory\", \"description\": \"Test Description\"}";

        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(httpHeaders).content(categoryJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
        assertEquals("Category created successfully!", jsonObject.getString("message"));
    }
    @Test
    public void testSaveCategory2() throws Exception {
        String categoryJson = "{\"name\": \"Folk Activities\", \"description\": \"Test Description\"}";
        MvcResult result = mockMvc.perform(post("/api/admin/categories").headers(httpHeaders).content(categoryJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertEquals("Category name already exists. Please choose another one!", jsonObject.getString("message"));
    }


    @Test
    public void testDeleteCategory() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/admin/categories/1").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
        assertEquals("Category permanently deleted!", jsonObject.getString("message"));
    }
}
