package course.spring.hospitalinformationsystem.dao;

import course.spring.hospitalinformationsystem.entity.Decursus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecursusRepository extends JpaRepository<Decursus, Long> {
}
