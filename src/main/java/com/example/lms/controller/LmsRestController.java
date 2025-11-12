package com.example.lms.controller;

import com.example.lms.model.CourseOffering;
import com.example.lms.service.LmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "LMS Course Management API", description = "APIs for managing course offerings, registrations, cancellations, and allotments.")
@RestController
@RequestMapping("/api/lms")
@Validated
public class LmsRestController {

    private static final Logger log = LoggerFactory.getLogger(LmsRestController.class);
    private final LmsService lmsService;

    public LmsRestController(LmsService lmsService) {
        this.lmsService = lmsService;
    }

    // ✅ Add course offering
    @Operation(summary = "Add a new course offering", description = "Creates a new course offering with instructor, date, and capacity limits.")
    @PostMapping("/offerings")
    public ResponseEntity<Map<String, String>> addCourseOffering(
            @Parameter(description = "Course name", examples = @ExampleObject(value = "Java101")) @NotBlank @RequestParam String course,

            @Parameter(description = "Instructor name", examples = @ExampleObject(value = "Alice")) @NotBlank @RequestParam String instructor,

            @Parameter(description = "Date of the offering (DDMMYYYY)", examples = @ExampleObject(value = "15092025")) @NotBlank @RequestParam String date,

            @Parameter(description = "Minimum number of attendees", examples = @ExampleObject(value = "2")) @Min(1) @RequestParam int min,

            @Parameter(description = "Maximum number of attendees", examples = @ExampleObject(value = "3")) @Min(1) @RequestParam int max) {

        try {
            String id = lmsService.addCourseOffering(course, instructor, date, String.valueOf(min),
                    String.valueOf(max));
            log.info("New course offering created: {}", id);
            return ResponseEntity.ok(Map.of("message", id));
        } catch (IllegalArgumentException e) {
            log.error("Error creating course offering: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "INPUT_DATA_ERROR"));
        }
    }

    // ✅ Register for a course
    @Operation(summary = "Register a user for a course offering", description = "Registers a student to an available course offering.")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Parameter(description = "User email", examples = @ExampleObject(value = "bob@example.com")) @Email @NotBlank @RequestParam String email,

            @Parameter(description = "Course offering ID", examples = @ExampleObject(value = "OFFERING-Java101-Alice")) @NotBlank @RequestParam String offeringId) {

        log.info("New registration request for: {} -> {}", email, offeringId);
        List<String> result = lmsService.register(email, offeringId);
        return ResponseEntity.ok(Map.of("result", result));
    }

    // ✅ Cancel registration
    @Operation(summary = "Cancel a registration", description = "Cancels a user's course registration if possible.")
    @PostMapping("/cancel")
    public ResponseEntity<Map<String, String>> cancel(
            @Parameter(description = "Registration ID", examples = @ExampleObject(value = "REG-COURSE-bob-Java101")) @NotBlank @RequestParam String registrationId) {

        log.info("Cancellation request for registrationId={}", registrationId);
        String result = lmsService.cancel(registrationId);
        return ResponseEntity.ok(Map.of("message", result));
    }

    // ✅ Allot Course
    @Operation(summary = "Allot a course", description = "Allocates the course to all confirmed registrants.")
    @PostMapping("/allot")
    public ResponseEntity<Map<String, Object>> allot(
            @Parameter(description = "Offering ID", examples = @ExampleObject(value = "OFFERING-Java101-Alice")) @NotBlank @RequestParam String offeringId) {

        log.info("Allotment requested for offeringId={}", offeringId);
        List<String> result = lmsService.allot(offeringId);
        return ResponseEntity.ok(Map.of("result", result));
    }

    @Operation(
    summary = "Get all course offerings",
    description = "Fetches all existing course offerings stored in the database."
 )
   @GetMapping("/offerings")
   public ResponseEntity<List<CourseOffering>> getAllOfferings() {
      return ResponseEntity.ok(lmsService.getAllOfferings());
   }

}
