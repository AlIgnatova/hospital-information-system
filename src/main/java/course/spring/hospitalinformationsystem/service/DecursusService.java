package course.spring.hospitalinformationsystem.service;

import course.spring.hospitalinformationsystem.entity.Decursus;

public interface DecursusService {
    Decursus getDecursusById(Long id);
    Decursus addDecursus(Decursus decursus);
    Decursus editDecursus(Long id, String text);
    Decursus deleteDecursus(Long id);
}
