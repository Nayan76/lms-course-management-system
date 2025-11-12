package com.example.lms.repo;


import com.example.lms.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByRegistrationId(String registrationId);
    boolean existsByEmailAndOfferingId(String email, String offeringId);
    List<Registration> findByOfferingId(String offeringId);
    List<Registration> findByOfferingIdAndCancelledFalse(String offeringId);

}
