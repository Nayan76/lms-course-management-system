package com.example.lms.service;

import com.example.lms.controller.CommandHandler;
import com.example.lms.model.CourseOffering;
import com.example.lms.repo.CourseOfferingRepository;
import com.example.lms.repo.RegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class LmsServiceTest {

    @Autowired
    private LmsService lmsService;

    @Autowired
    private CourseOfferingRepository courseRepo;

    @Autowired
    private RegistrationRepository regRepo;

    @BeforeEach
    public void setup() {
        courseRepo.deleteAll();
        regRepo.deleteAll();
    }

    @Test
    void testAddCourseOffering_sucess() {
        String offeringId = lmsService.addCourseOffering("Java101", "Alice", "15092025", "2", "3");
        assertEquals("OFFERING-Java101-Alice", offeringId);
        assertTrue(courseRepo.findByCourseNameAndInstructor("Java101", "Alice").isPresent());
    }

    @Test
    void testRegister_success() {
        lmsService.addCourseOffering("Java101", "Alice", "15092025", "2", "3");
        List<String> result = lmsService.register("bob@example.com", "OFFERING-Java101-Alice");
        assertTrue(result.get(0).startsWith("REG-COURSE-bob-Java101"));
        assertTrue(result.get(0).endsWith("ACCEPTED"));
    }

    @Test
    void testRegister_courseFullError() {
        lmsService.addCourseOffering("Java101", "Alice", "15092025", "1", "1");
        lmsService.register("bob@example.com", "OFFERING-Java101-Alice");
        List<String> result = lmsService.register("carol@example.com", "OFFERING-Java101-Alice");
        assertEquals("COURSE_FULL_ERROR", result.get(0));
    }

    @Test
    void testCancel_beforeAllotment_success() {
        lmsService.addCourseOffering("Java101", "Alice", "15092025", "1", "2");

        // Register first
        List<String> regResult = lmsService.register("bob@example.com", "OFFERING-Java101-Alice");

        // Extract the actual registration ID from the result ("REG-COURSE-bob-Java101 ACCEPTED")
        String regId = regResult.get(0).split(" ")[0];

        // Now cancel the actual ID
        String cancelResult = lmsService.cancel(regId);

        // Validate success
        assertTrue(cancelResult.endsWith("CANCELED_ACCEPTED"));
    }



    @Test
    void testAllot_confirmsWhenMinMet() {
        lmsService.addCourseOffering("Java101", "Alice", "15092025", "1", "3");
        lmsService.register("bob@example.com", "OFFERING-Java101-Alice");
        List<String> allotOutput = lmsService.allot("OFFERING-Java101-Alice");
        assertTrue(allotOutput.get(0).contains("CONFIRMED"));
    }

    @Test
    void testAllot_courseCanceledWhenMinNotMet() {
        lmsService.addCourseOffering("Python101", "Eve", "15092025", "3", "5");
        lmsService.register("bob@example.com", "OFFERING-Python101-Eve");
        List<String> allotOutput = lmsService.allot("OFFERING-Python101-Eve");
        assertTrue(allotOutput.get(0).contains("COURSE_CANCELED"));
    }
}
