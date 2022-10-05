package course.spring.hospitalinformationsystem.dao;


import course.spring.hospitalinformationsystem.entity.HospitalStay;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HospitalStayRepository extends JpaRepository<HospitalStay, Long> {
}
