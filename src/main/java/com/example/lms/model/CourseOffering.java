package com.example.lms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "course_offering")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(nullable = false)
    private String instructor;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "min_employees", nullable = false)
    private int minEmployees;

    @Column(name = "max_employees", nullable = false)
    private int maxEmployees;

    @Column(name = "allotted")
    private boolean allotted = false;

    @Column(name = "offering_id", unique = true, nullable = false)
    private String offeringId;
}
