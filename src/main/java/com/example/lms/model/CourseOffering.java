package com.example.lms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"courseName", "instructor"}))
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseOffering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String courseName;
    private String instructor;
    private LocalDate date;
    private int minEmployees;
    private int maxEmployees;
    private boolean allotted = false; // becomes true after ALLOT

    public String offeringId(){
        return String.format("OFFERING-%s-%s", courseName, instructor);
    }
}
