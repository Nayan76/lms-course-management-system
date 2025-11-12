package com.example.lms.repo;

import com.example.lms.model.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {
    Optional<CourseOffering> findByCourseNameAndInstructor(String courseName, String instructor);
    //find by offering id:
    default Optional<CourseOffering> findByOfferingId(String offeringId){
        // offeringId = 0FFERING-<COURSE>-<INSTRUCTOR>
        if(!offeringId.startsWith("OFFERING-")) return Optional.empty();
        String body = offeringId.substring("OFFERING-".length());
        int idx = body.lastIndexOf("-");
        if(idx < 0) return Optional.empty();
        String course = body.substring(0, idx);
        String instructor = body.substring(idx + 1);
        return findByCourseNameAndInstructor(course, instructor);
    }
}
