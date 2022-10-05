package course.spring.hospitalinformationsystem.service.impl;

import course.spring.hospitalinformationsystem.dao.DecursusRepository;
import course.spring.hospitalinformationsystem.entity.Decursus;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;
import course.spring.hospitalinformationsystem.service.DecursusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@Slf4j
public class DecursusServiceImpl implements DecursusService {

    private DecursusRepository decursusRepo;

    @Autowired
    public DecursusServiceImpl(DecursusRepository decursusRepo) {
        this.decursusRepo = decursusRepo;
    }

    /**
     *
     * @param id ID of the Decursus that will be returned
     * @return Decursus with param id
     */
    @Override
    public Decursus getDecursusById(Long id) {
        return decursusRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("Decursus with id='%d' does not exist.", id)));
    }

    /**
     *
     * @param dec Decursus that will be added to the repository
     * @return the created Decursus entity
     */
    @Override
    public Decursus addDecursus(Decursus dec) {
        dec.setId(null);
        dec.setCreated(LocalDateTime.now());
        dec.setModified(LocalDateTime.now());

        return decursusRepo.save(dec);
    }

    /**
     *
     * @param id ID of the Decursus that will be updated
     * @param text the new String value for Decursus's property "text"
     * @return the updated Decursus
     */
    @Override
    public Decursus editDecursus(Long id, String text) {
        var oldDecursus = getDecursusById(id);
        oldDecursus.setText(text);
        oldDecursus.setModified(LocalDateTime.now());
        return decursusRepo.save(oldDecursus);
    }

    /**
     *
     * @param id ID of the Decursus that will be deleted
     * @return the Deleted decursus
     */
    @Override
    public Decursus deleteDecursus(Long id) {
        var oldDecursus = getDecursusById(id);
        decursusRepo.deleteById(id);
        return oldDecursus;
    }
}
