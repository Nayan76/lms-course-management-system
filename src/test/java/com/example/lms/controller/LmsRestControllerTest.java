package com.example.lms.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LmsRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddOfferingAndRegister() throws Exception {

        // 1. Create offering
        mockMvc.perform(
                post("/api/lms/offerings")
                        .param("course", "Node101")
                        .param("instructor", "Tom")
                        .param("date", "15092025")
                        .param("min", "1")
                        .param("max", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OFFERING-Node101-Tom"));

        // 2. Register student
        mockMvc.perform(
                post("/api/lms/register")
                        .param("email", "dev@example.com")
                        .param("offeringId", "OFFERING-Node101-Tom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0]", containsString("ACCEPTED")));
    }

    @Test
    void testCancelRegistration() throws Exception {
        // 1. Create offering
        mockMvc.perform(
                post("/api/lms/offerings")
                        .param("course", "Java101")
                        .param("instructor", "Alice")
                        .param("date", "15092025")
                        .param("min", "1")
                        .param("max", "3"));

        // 2. Register student
        var result = mockMvc.perform(
                post("/api/lms/register")
                        .param("email", "bob@example.com")
                        .param("offeringId", "OFFERING-Java101-Alice"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract registration ID
        // result example: {"result": ["REG-COURSE-bob-Java101 ACCEPTED"]}
        String regId = result.substring(result.indexOf("REG-"), result.indexOf(" ACCEPTED"));

        // 3. Cancel the registration
        mockMvc.perform(
                post("/api/lms/cancel")
                        .param("registrationId", regId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("CANCELED")));
    }

    @Test
    void testAllotCourse() throws Exception {

        // 1. Create offering
        mockMvc.perform(
                post("/api/lms/offerings")
                        .param("course", "Java101")
                        .param("instructor", "Alice")
                        .param("date", "15092025")
                        .param("min", "1")
                        .param("max", "2"));

        // 2. Register student
        mockMvc.perform(
                post("/api/lms/register")
                        .param("email", "bob@example.com")
                        .param("offeringId", "OFFERING-Java101-Alice"));

        // 3. Allot offering
        mockMvc.perform(
                post("/api/lms/allot")
                        .param("offeringId", "OFFERING-Java101-Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0]", containsString("CANCELLED")));
    }
}
