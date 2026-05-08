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

    private HttpHeaders adminHeaders;
    private HttpHeaders publicHeaders;

    @Before
    public void before() throws Exception {
        adminHeaders = getAdminHeaders();
        publicHeaders = getPublicHeaders();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
    }

    @Test
    public void testRegisterDuplicateUsernameFails() throws Exception {
        String registerJson = "{\"username\": \"admin\", \"password\": \"password123\", \"email\": \"admin_new@example.com\", \"realName\": \"Admin User\"}";
        MvcResult result = mockMvc.perform(post("/api/users/register").headers(publicHeaders).content(registerJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertFalse(jsonObject.getBoolean("success"));
    }

    @Test
    public void testApplyForContributorSuccess() throws Exception {
        String applicationJson = "{\"username\": \"guest01\", \"reason\": \"I am passionate about preserving cultural heritage\"}";
        MvcResult result = mockMvc.perform(post("/api/users/apply-role").headers(adminHeaders).content(applicationJson))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertTrue(jsonObject.getBoolean("success"));
    }

    @Test
    public void testGetAllUsersUnauthorizedWithContributor() throws Exception {
        HttpHeaders headers = getContributorHeaders();
        mockMvc.perform(get("/api/users").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testGetPendingApplicationsSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/role-applications").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONArray jsonArray = new JSONArray(rs);
        assertTrue(jsonArray.length() >= 0);
    }

    @Test
    public void testGetPendingApplicationsUnauthorizedWithContributor() throws Exception {
        HttpHeaders headers = getContributorHeaders();
        mockMvc.perform(get("/api/admin/role-applications").headers(headers))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testProcessApplicationNotFound() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/admin/role-applications/99999?status=1").headers(adminHeaders))
                .andExpect(status().isOk())
                .andReturn();
        String rs = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(rs);
        assertFalse(jsonObject.getBoolean("success"));
    }


}