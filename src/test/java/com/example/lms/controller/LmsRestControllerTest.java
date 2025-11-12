package com.example.lms.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
                properties = "spring.profiles.active=test"
)
@AutoConfigureMockMvc
class LmsRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
                .andExpect(content().string("OFFERING-Node101-Tom"));

        mockMvc.perform(
                        post("/api/lms/register")
                                .param("email", "dev@example.com")
                                .param("offeringId", "OFFERING-Node101-Tom"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ACCEPTED")));
    }

    @Test
    void testCancelRegistration() throws Exception {
        mockMvc.perform(post("/api/lms/cancel")
                .param("registrationId", "REG-COURSE-bob-Java101"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CANCELED_REJECTED")));
    }

    @Test
    void testAllotCourse() throws Exception {
        mockMvc.perform(post("/api/lms/allot")
                .param("offeringId", "OFFERING-Java101-Alice"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CONFIRMED")));
    }
}
