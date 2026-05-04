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

public class UserControllerTest extends ControlBaseTest {

    private HttpHeaders httpHeaders;

    @Before
    public void before() throws Exception {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("content-type", "application/json;charset=UTF-8");
        httpHeaders.add("origin", "Access-Control-Allow-Origin");
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testLogin1() throws Exception {
        String loginJson = "{\"username\": \"admin\", \"password\": \"123456\"}";
        MvcResult result = mockMvc.perform(post("/api/login").headers(httpHeaders).content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }
    @Test
    public void testLogin2() throws Exception {
        String loginJson = "{\"username\": \"admin\", \"password\": \"1234567\"}";
        MvcResult result = mockMvc.perform(post("/api/login").headers(httpHeaders).content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertEquals("Incorrect password!", jsonObject.get("message"));
    }
    @Test
    public void testLogin3() throws Exception {
        String loginJson = "{\"username\": \"admin121\", \"password\": \"1234567\"}";
        MvcResult result = mockMvc.perform(post("/api/login").headers(httpHeaders).content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertEquals("Account or email not found!", jsonObject.get("message"));
    }

    @Test
    public void testRegister1() throws Exception {
        String registerJson = "{\"username\": \"newuser\", \"password\": \"password123\", \"email\": \"newuser@example.com\", \"nickname\": \"New User\"}";
        MvcResult result = mockMvc.perform(post("/api/users/register").headers(httpHeaders).content(registerJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }
    @Test
    public void testRegister2() throws Exception {
        String registerJson = "{\"username\": \"admin\", \"password\": \"password123\", \"email\": \"newuser@example.com\", \"nickname\": \"New User\"}";
        MvcResult result = mockMvc.perform(post("/api/users/register").headers(httpHeaders).content(registerJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertEquals("Sorry, this username or email is already registered!", jsonObject.get("message"));
    }

    @Test
    public void testForgotPassword1() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/forgot-password?email=test@example.com").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }
    @Test
    public void testForgotPassword2() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/forgot-password?email=test1@qq.com").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }

    @Test
    public void testResetPassword() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/reset-password?email=test@example.com&code=123456&newPassword=password123").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }

    @Test
    public void testGetProfile() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/profile?username=admin").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject);
    }

    @Test
    public void testUpdateProfile() throws Exception {
        String profileJson = "{\"username\": \"test\", \"nickname\": \"Updated Nickname\", \"avatar\": \"https://ts1.tc.mm.bing.net/th/id/OIP-C.tAbl-BgNaY8m36l0tnJ5_QHaNK?rs=1&pid=ImgDetMain&o=7&rm=3\"}";
        MvcResult result = mockMvc.perform(put("/api/users/profile").headers(httpHeaders).content(profileJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }

    @Test
    public void testApplyForContributor() throws Exception {
        String applicationJson = "{\"username\": \"testuser\", \"reason\": \"I want to contribute\"}";
        MvcResult result = mockMvc.perform(post("/api/users/apply-role").headers(httpHeaders).content(applicationJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONArray jsonArray = new JSONArray(rs);
        assert jsonArray.length() >= 0;
    }

    @Test
    public void testUpdateRole() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/users/1/role?role=CONTRIBUTOR").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertTrue(rs.contains("Role updated successfully") || rs.contains("User not found"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/users/10").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        assertEquals("Account deleted", rs);
    }

    @Test
    public void testGetPendingApplications() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/role-applications").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();

        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONArray jsonArray = new JSONArray(rs);
        assert jsonArray.length() >= 0;
    }

    @Test
    public void testProcessApplication() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/admin/role-applications/1?status=1").headers(httpHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("rs:" + rs);
        JSONObject jsonObject = new JSONObject(rs);
        assertNotNull(jsonObject.get("success"));
    }
}
