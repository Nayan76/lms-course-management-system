package com.example.lms.service;


import com.example.lms.model.CourseOffering;
import com.example.lms.model.Registration;
import com.example.lms.repo.CourseOfferingRepository;
import com.example.lms.repo.RegistrationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class LmsService {
    private final CourseOfferingRepository offeringRepo;
    private final RegistrationRepository regRepo;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("ddMMyyyy");
    public LmsService(CourseOfferingRepository o, RegistrationRepository r) {this.offeringRepo = o;this.regRepo = r;}

    public String addCourseOffering(String course, String instructor, String dateStr, String minStr, String maxStr){
        //validate
        if (course.isBlank() || instructor.isBlank() || dateStr.isBlank() || minStr.isBlank() || maxStr.isBlank()) throw new IllegalArgumentException();
        LocalDate date;
        int min, max;
        try{date = LocalDate.parse(dateStr, fmt); min = Integer.parseInt(minStr); max = Integer.parseInt(maxStr);}
        catch(Exception e) {throw new IllegalArgumentException();}
        if(min > max || min < 0 || max <= 0) throw new IllegalArgumentException();

        // Prevent Duplicate offerings with same course+instructor+date
        if (offeringRepo.findByCourseNameAndInstructor(course, instructor).isPresent()) {
            return String.format("OFFERING-%s-%s", course, instructor);
        }
        // Unique course & instructor assumption means combination is unique
        // create and persist
        var offering = CourseOffering.builder()
                        .courseName(course)
                .instructor(instructor)
                .date(date)
                        .minEmployees(min)
                .maxEmployees(max)
                .offeringId("OFFERING-" + course + "-" + instructor)
                        .build();
        offeringRepo.save(offering);
        return offering.getOfferingId();
    }

    @Transactional
    public List<String> register(String email, String offeringId){
        if(email.isBlank() || offeringId.isBlank()) return  List.of("INPUT_DATA_ERROR");
        // find offering
        var opt = offeringRepo.findByOfferingId(offeringId);
        if (opt.isEmpty()) return  List.of("INPUT_DATA_ERROR");

        CourseOffering off = opt.get();
        // check unique email+offering
        if (regRepo.existsByEmailAndOfferingId(email, offeringId)) return List.of("INPUT_DATA_ERROR");
        // check if offering is already allotted
        if(off.isAllotted()) return List.of("COURSE_FULL_ERROR"); // treat as closed

        // count current accepted (not cancelled)
        var regs = regRepo.findByOfferingIdAndCancelledFalse(offeringId);
        if(regs.size() >= off.getMaxEmployees()){
            return List.of("COURSE_FULL_ERROR");
        } else {
            // create register id
            String empName = email.split("@")[0];
            String regId = String.format("REG-COURSE-%s-%s", empName, off.getCourseName());
            Registration r = new Registration(regId, email, empName, offeringId, "ACCEPTED");
            regRepo.save(r);
            return List.of(regId+" ACCEPTED");
        }
    }

    @Transactional
    public String cancel(String registrationId){
        if (registrationId.isBlank()) return "INPUT_DATA_ERROR";
        var opt = regRepo.findByRegistrationId(registrationId);
        if (opt.isEmpty()) return "INPUT_DATA_ERROR";
        Registration r = opt.get();
        // find offering to see if allotted
        var offeringOpt = offeringRepo.findByOfferingId(r.getOfferingId());
        if (offeringOpt.isEmpty()) return "INPUT_DATA_ERROR";
        CourseOffering off = offeringOpt.get();
        if(off.isAllotted()){
            return registrationId + " CANCELED_REJECTED";
        } else  {
            r.setCancelled(true);
            r.setStatus("CANCELED");
            regRepo.save(r);
            return registrationId + " CANCELED_ACCEPTED";
        }

    }

    @Transactional
    public List<String> allot(String offeringId) {
        if (offeringId == null || offeringId.isBlank())
            return List.of("INPUT_DATA_ERROR");

        // Use correct repo method
        var opt = offeringRepo.findByOfferingId(offeringId);
        if (opt.isEmpty())
            return List.of("INPUT_DATA_ERROR");

        CourseOffering off = opt.get();

        // fetch all (including cancelled)
        List<Registration> allRegs = regRepo.findByOfferingId(offeringId);
        List<Registration> active = allRegs.stream()
                .filter(r -> !r.isCancelled())
                .toList();

        boolean meetsMin = active.size() >= off.getMinEmployees();

        // mark offering allotted
        off.setAllotted(true);
        offeringRepo.save(off);

        // update statuses
        for (Registration r : allRegs) {
            if (r.isCancelled()) {
                r.setStatus("CANCELLED");
            } else {
                if (meetsMin) r.setStatus("CONFIRMED");
                else r.setStatus("COURSE_CANCELED");
            }
            regRepo.save(r);
        }

        // build output list
        return allRegs.stream()
                .sorted(Comparator.comparing(Registration::getRegistrationId))
                .map(r -> String.join(" ",
                        r.getRegistrationId(),
                        r.getEmployeeName(),
                        r.getEmail(),
                        off.getOfferingId(),
                        off.getCourseName(),
                        off.getInstructor(),
                        off.getDate().format(fmt),
                        r.getStatus()))
                .toList();

    }

    public List<CourseOffering> getAllOfferings() {
        return offeringRepo.findAll();
    }

}
