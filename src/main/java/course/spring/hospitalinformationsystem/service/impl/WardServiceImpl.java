package course.spring.hospitalinformationsystem.service.impl;

import course.spring.hospitalinformationsystem.dao.WardRepository;
import course.spring.hospitalinformationsystem.entity.Ward;

import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import course.spring.hospitalinformationsystem.exception.MethodNotAllowedException;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;
import course.spring.hospitalinformationsystem.service.UserService;
import course.spring.hospitalinformationsystem.service.WardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.util.Collection;

import static course.spring.hospitalinformationsystem.service.serviceUtils.UserManager.getLoggedUser;

@Service
@Transactional
@Slf4j
public class WardServiceImpl implements WardService {
    private WardRepository wardRepo;

    @Autowired
    public WardServiceImpl(WardRepository wardRepo) {
        this.wardRepo = wardRepo;
    }

    /**
     *
     * @return collection of all Wards in the repository
     */
    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"DOCTOR", "ADMINISTRATOR"})
    public Collection<Ward> getAllWards() {
        return wardRepo.findAll();
    }

    /**
     *
     * @param id of the Ward that will be returned
     * @return Ward with param id
     * @throws NonExistingEntityException if Ward with param ID is not present in the repository
     */
    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"DOCTOR", "ADMINISTRATOR", "NURSE"})
    public Ward getWardById(Long id) throws NonExistingEntityException {
        Ward ward = wardRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("Ward with id='%d' does not exist.", id)));
        if("NURSE".equals(getLoggedUser().getRole().toString())){
            if(!ward.getStaff().contains(getLoggedUser())){
                throw new MethodNotAllowedException("This information cannot be viewed.");
            }
        }
        return ward;
    }

    /**
     *
     * @param type String representation of the type of the Ward
     * @return Ward with param type
     * @throws NonExistingEntityException if Ward with param type is not present in the repository
     */
    @Override
    @Transactional(readOnly = true)
    public Ward getWardByType(String type) throws NonExistingEntityException {

        Ward ward = wardRepo.findAll().stream()
                .filter(w -> w.getWardType().toString().equals(type)).findAny().orElse(null);
        if (ward ==null){
            throw new NonExistingEntityException(String.format
                    ("Ward with type '%s' does not exist.", type ));
        }
        return ward;
    }

    /**
     *
     * @param ward that will be added to the repository
     * @return the created Ward entity
     * @throws InvalidEntityDataException in case of invalid input data
     */
    @Override
    @RolesAllowed("ADMINISTRATOR")
    public Ward addWard(Ward ward) throws InvalidEntityDataException {
        ward.setId(null);
        ward.setBedAvailability(ward.getBedCapacity());
        ward.setCreated(LocalDateTime.now());
        ward.setModified(LocalDateTime.now());
        return wardRepo.save(ward);
    }

    /**
     *
     * @param ward that will be updated
     * @return the updated Ward
     * @throws NonExistingEntityException in case the input Ward's ID does not match any existing Ward in the repository
     * @throws InvalidEntityDataException in case of invalid input data
     */
    @Override
    @RolesAllowed("ADMINISTRATOR")
    public Ward updateWard(Ward ward) throws NonExistingEntityException, InvalidEntityDataException {
        Ward oldWard = getWardById(ward.getId());
        ward.setCreated(oldWard.getCreated());
        ward.setModified(LocalDateTime.now());
        return wardRepo.save(ward);
    }

    /**
     *
     * @param id ID of the Ward that will be deleted
     * @return the Ward that was deleted
     * @throws NonExistingEntityException if Ward with param id is not present in the repository
     */
    @Override
    @RolesAllowed("ADMINISTRATOR")
    public Ward deleteWardById(Long id) throws NonExistingEntityException {
        var oldWard = getWardById(id);
        if (!oldWard.getPatients().isEmpty()){
            throw new MethodNotAllowedException(String.format
                    ("Ward '%s' cannot be deleted - there are currently hospitalized patients in this ward.", oldWard.getWardType()));
        }
        wardRepo.deleteById(id);
        return oldWard;
    }

    /**
     *
     * @return count of all Wards in the repository
     */
    @Override
    public long count() {
        return wardRepo.count();
    }

}
