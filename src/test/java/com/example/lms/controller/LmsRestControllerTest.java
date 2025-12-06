package com.example.lms.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        @Autowired
        private ObjectMapper objectMapper; // Add this for safe JSON parsing

        @Test
        void testAddOfferingAndRegister() throws Exception {
                mockMvc.perform(
                                post("/api/lms/offerings")
                                                .param("course", "Node101")
                                                .param("instructor", "Tom")
                                                .param("date", "15092025")
                                                .param("min", "1")
                                                .param("max", "3"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("OFFERING-Node101-Tom"));

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

                // 2. Register student and extract registration ID safely
                String responseContent = mockMvc.perform(
                                post("/api/lms/register")
                                                .param("email", "bob@example.com")
                                                .param("offeringId", "OFFERING-Java101-Alice"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result[0]", containsString("ACCEPTED")))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                // Safely extract registration ID using Jackson
                JsonNode jsonNode = objectMapper.readTree(responseContent);
                String fullResult = jsonNode.get("result").get(0).asText();
                String regId = fullResult.split(" ")[0]; // "REG-COURSE-bob-Java101"

                // 3. Cancel registration
                mockMvc.perform(
                                post("/api/lms/cancel")
                                                .param("registrationId", regId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message", containsString("CANCELLED_ACCEPTED")));
                // ↑ Fixed spelling: now expects full correct string
        }

        @Test
        void testAllotCourse() throws Exception {
                // Use UNIQUE course and instructor to avoid conflicts
                String course = "Scala101-" + System.currentTimeMillis(); // guaranteed unique
                String instructor = "Martin";
                String offeringId = "OFFERING-" + course + "-" + instructor;

                // 1. Create offering
                mockMvc.perform(
                                post("/api/lms/offerings")
                                                .param("course", course)
                                                .param("instructor", instructor)
                                                .param("date", "15092025")
                                                .param("min", "1")
                                                .param("max", "2"))
                                .andExpect(status().isOk());

                // 2. Register one student with unique email
                mockMvc.perform(
                                post("/api/lms/register")
                                                .param("email", "unique" + System.currentTimeMillis() + "@example.com")
                                                .param("offeringId", offeringId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result[0]", containsString("ACCEPTED")));

                // 3. Allot → should be CONFIRMED
                mockMvc.perform(
                                post("/api/lms/allot")
                                                .param("offeringId", offeringId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result[0]", containsString("CONFIRMED")));
        }
}