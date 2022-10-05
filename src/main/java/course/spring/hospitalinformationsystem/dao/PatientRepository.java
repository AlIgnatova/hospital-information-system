package course.spring.hospitalinformationsystem.dao;



import course.spring.hospitalinformationsystem.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEGN(String EGN);
}
