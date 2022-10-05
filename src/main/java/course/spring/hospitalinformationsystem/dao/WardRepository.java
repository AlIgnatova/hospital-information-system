package course.spring.hospitalinformationsystem.dao;


import course.spring.hospitalinformationsystem.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WardRepository extends JpaRepository<Ward, Long> {

}
