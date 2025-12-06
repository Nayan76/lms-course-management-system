package com.example.lms.service;

import com.example.lms.model.CourseOffering;
import com.example.lms.model.Registration;
import com.example.lms.repo.CourseOfferingRepository;
import com.example.lms.repo.RegistrationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class LmsService {

    private final CourseOfferingRepository offeringRepo;
    private final RegistrationRepository regRepo;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMMyyyy");

    public LmsService(CourseOfferingRepository offeringRepo, RegistrationRepository regRepo) {
        this.offeringRepo = offeringRepo;
        this.regRepo = regRepo;
    }

    public String addCourseOffering(String course, String instructor, String dateStr, String minStr, String maxStr) {
        // Input validation
        if (isBlank(course, instructor, dateStr, minStr, maxStr)) {
            throw new IllegalArgumentException();
        }

        LocalDate date;
        int min, max;
        try {
            date = LocalDate.parse(dateStr, fmt);
            min = Integer.parseInt(minStr);
            max = Integer.parseInt(maxStr);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }

        if (min > max || min < 0 || max <= 0) {
            throw new IllegalArgumentException();
        }

        // Prevent duplicate offering (same course + instructor)
        if (offeringRepo.findByCourseNameAndInstructor(course, instructor).isPresent()) {
            return String.format("OFFERING-%s-%s", course, instructor);
        }

        String offeringId = "OFFERING-" + course + "-" + instructor;

        var offering = CourseOffering.builder()
                .courseName(course)
                .instructor(instructor)
                .date(date)
                .minEmployees(min)
                .maxEmployees(max)
                .offeringId(offeringId)
                .allotted(false)
                .build();

        offeringRepo.save(offering);
        return offeringId;
    }

    @Transactional
    public List<String> register(String email, String offeringId) {
        if (isBlank(email, offeringId)) {
            return List.of("INPUT_DATA_ERROR");
        }

        var offeringOpt = offeringRepo.findByOfferingId(offeringId);
        if (offeringOpt.isEmpty()) {
            return List.of("INPUT_DATA_ERROR");
        }

        CourseOffering offering = offeringOpt.get();

        if (regRepo.existsByEmailAndOfferingId(email, offeringId)) {
            return List.of("INPUT_DATA_ERROR");
        }

        if (offering.isAllotted()) {
            return List.of("COURSE_FULL_ERROR");
        }

        long activeCount = regRepo.findByOfferingIdAndCancelledFalse(offeringId).size();
        if (activeCount >= offering.getMaxEmployees()) {
            return List.of("COURSE_FULL_ERROR");
        }

        String empName = email.split("@")[0];
        String regId = String.format("REG-COURSE-%s-%s", empName, offering.getCourseName());

        Registration registration = new Registration(regId, email, empName, offeringId, "ACCEPTED");
        regRepo.save(registration);

        return List.of(regId + " ACCEPTED");
    }

    @Transactional
    public String cancel(String registrationId) {
        if (isBlank(registrationId)) {
            return "INPUT_DATA_ERROR";
        }

        var regOpt = regRepo.findByRegistrationId(registrationId);
        if (regOpt.isEmpty()) {
            return "INPUT_DATA_ERROR";
        }

        Registration registration = regOpt.get();
        var offeringOpt = offeringRepo.findByOfferingId(registration.getOfferingId());

        if (offeringOpt.isEmpty()) {
            return "INPUT_DATA_ERROR";
        }

        CourseOffering offering = offeringOpt.get();

        if (offering.isAllotted()) {
            return registrationId + " CANCELLED_REJECTED";
        }

        registration.setCancelled(true);
        registration.setStatus("CANCELLED");
        regRepo.save(registration);

        return registrationId + " CANCELLED_ACCEPTED";
    }

    @Transactional
    public List<String> allot(String offeringId) {
        if (isBlank(offeringId)) {
            return List.of("INPUT_DATA_ERROR");
        }

        var offeringOpt = offeringRepo.findByOfferingId(offeringId);
        if (offeringOpt.isEmpty()) {
            return List.of("INPUT_DATA_ERROR");
        }

        CourseOffering offering = offeringOpt.get();

        List<Registration> allRegistrations = regRepo.findByOfferingId(offeringId);
        long activeCount = allRegistrations.stream()
                .filter(r -> !r.isCancelled())
                .count();

        boolean meetsMinimum = activeCount >= offering.getMinEmployees();

        // Mark offering as allotted
        offering.setAllotted(true);
        offeringRepo.save(offering);

        // Update registration statuses
        for (Registration r : allRegistrations) {
            if (r.isCancelled()) {
                r.setStatus("CANCELLED");
            } else {
                r.setStatus(meetsMinimum ? "CONFIRMED" : "COURSE_CANCELED");
            }
            regRepo.save(r);
        }

        // Generate output lines sorted by registration ID
        return allRegistrations.stream()
                .sorted(Comparator.comparing(Registration::getRegistrationId))
                .map(r -> String.join(" ",
                        r.getRegistrationId(),
                        r.getEmployeeName(),
                        r.getEmail(),
                        offering.getOfferingId(),
                        offering.getCourseName(),
                        offering.getInstructor(),
                        offering.getDate().format(fmt),
                        r.getStatus()))
                .toList();
    }

    public List<CourseOffering> getAllOfferings() {
        return offeringRepo.findAll();
    }

    // Helper to reduce duplication
    private boolean isBlank(String... values) {
        return Arrays.stream(values).anyMatch(s -> s == null || s.isBlank());
    }
}