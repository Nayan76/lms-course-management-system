package com.example.lms.repo;

import com.example.lms.model.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {

    Optional<CourseOffering> findByCourseNameAndInstructor(String courseName, String instructor);

    // Spring Data will automatically generate this query using the offering_id
    // column
    Optional<CourseOffering> findByOfferingId(String offeringId);
}