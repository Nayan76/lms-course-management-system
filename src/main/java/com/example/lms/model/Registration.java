package com.example.lms.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "offeringId"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String registrationId; // REG-COURSE-<employeeName>-<courseName>
    private String email;
    private String employeeName;
    private String offeringId; // textual offering id
    private String status; // ACCEPTED, COURSE_FULL_ERROR (not stored), CANCELLED, CONFIRMED, COURSE_CANCELED
    private boolean cancelled = false;

    // adding manual constructor
    public Registration(String registrationId, String email, String employeeName, String offeringId, String status) {
        this.registrationId = registrationId;
        this.email = email;
        this.employeeName = employeeName;
        this.offeringId = offeringId;
        this.status = status;
    }

}
