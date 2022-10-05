package course.spring.hospitalinformationsystem.service.impl;

import course.spring.hospitalinformationsystem.dao.PatientRepository;
import course.spring.hospitalinformationsystem.entity.*;
import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import course.spring.hospitalinformationsystem.exception.MethodNotAllowedException;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;
import course.spring.hospitalinformationsystem.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static course.spring.hospitalinformationsystem.service.serviceUtils.UserManager.getLoggedUser;

@Service
@Transactional
@Slf4j
public class PatientServiceImpl implements PatientService {

    private PatientRepository patientRepo;
    @Autowired
    public PatientServiceImpl(PatientRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    /**
     *
     * @return collection of all Patients in the repository
     */
    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"ADMINISTRATOR", "DOCTOR"})
    public Collection<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    /**
     *
     * @param id of the Patient that will be returned
     * @return Patient with param id
     * @throws NonExistingEntityException if Patient with param ID is not present in the repository
     */
    @Override
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) throws NonExistingEntityException {
        return patientRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("Patient with id='%d' does not exist.", id)));
    }

    /**
     *
     * @param EGN of the Patient that will be returned
     * @return Patient with param EGN
     * @throws NonExistingEntityException if Patient with param EGN is not present in the repository
     */
    @Override
    @Transactional(readOnly = true)
    public Patient getPatientByEGN(String EGN) throws NonExistingEntityException {
        return patientRepo.findByEGN(EGN).orElseThrow(() -> new NonExistingEntityException(
                String.format("Patient with EGN='%s' has never been registered in the hospital.", EGN)
        ));
    }

    /**
     *
     * @param id of the Patient whose tests will be returned
     * @return Collection of all tests with patientId corresponding to the param id
     * @throws NonExistingEntityException if Tests with param ID are not present in the repository
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<Test> getAllTestsForPatientById(Long id) throws NonExistingEntityException {
        Collection<Test> tests = new ArrayList<>();
        boolean currentUserIsNurse = "NURSE".equals(getLoggedUser().getRole().toString());
        if(currentUserIsNurse){
            Ward ward = getPatientById(id).getWard();
            if(ward == null){
                throw new MethodNotAllowedException(String.format
                        ("Patient with id='%d'is currently not hospitalized, no tests are available to be viewed.", id));
            }
            boolean userIsStaffOfSameWard = ward.getStaff().contains(getLoggedUser());
            if(!userIsStaffOfSameWard){
                throw new MethodNotAllowedException(String.format
                        ("Patient with id='%d' is not hospitalized in your ward.", id));
            }
            tests = getPatientById(id).getTestsCollection().stream().filter(t -> t.getHospitalStay().isStayActive()).collect(Collectors.toList());

        }else {
            Patient patient = getPatientById(id);
            tests = patient.getTestsCollection();
        }
        return tests;
    }

    /**
     *
     * @param id of the Patient whose HospitalStays will be returned
     * @return Collection of all HospitalStays with patientId corresponding to the param id
     * @throws NonExistingEntityException if HospitalStays with param ID are not present in the repository
     */
    @Override
    @Transactional(readOnly = true)
    @PostFilter("hasRole('DOCTOR') or hasRole('ADMINISTRATOR')")
    public Collection<HospitalStay> getAllHospitalStaysForPatientById(Long id) throws NonExistingEntityException {
        return getPatientById(id).getHospitalStaysCollection();
    }

    @Override
    public Patient addPatient(Patient patient) throws InvalidEntityDataException {
        patient.setId(null);
        if(patientRepo.findByEGN(patient.getEGN()).isPresent()){
            throw new InvalidEntityDataException(String.format
                    ("Patient with EGN='%s' already exists.", patient.getEGN()));
        }

        patient.setCreated(LocalDateTime.now());
        patient.setModified(LocalDateTime.now());

        return patientRepo.save(patient);
    }

    /**
     *
     * @param patient that will be updated
     * @return the updated patient
     * @throws NonExistingEntityException in case the input Patient's ID does not match any existing Patient in the repository
     * @throws InvalidEntityDataException in case of invalid input data
     */
    @Override
    @RolesAllowed({"ADMINISTRATOR", "DOCTOR"})
    public Patient updatePatient(Patient patient) throws NonExistingEntityException, InvalidEntityDataException {
        Patient oldPatient = getPatientById(patient.getId());
        if (!patient.getEGN().equals(oldPatient.getEGN()) && patientRepo.findByEGN(patient.getEGN()).isPresent()) {
            throw new InvalidEntityDataException(String.format
                    ("Patient with EGN ='%s' already exists.", patient.getEGN()));
        }
        patient.setCreated(oldPatient.getCreated());
        patient.setModified(LocalDateTime.now());
        Patient updated = patientRepo.save(patient);

        return updated;
    }

    /**
     *
     * @param id of the Patient that will be deleted
     * @return the Patient that was deleted
     * @throws NonExistingEntityException if Patient with param id is not present in the repository
     */
    @Override
    @PreFilter("hasRole('ADMINISTRATOR')")
    public Patient deletePatientById(Long id) throws NonExistingEntityException {
        Patient oldPatient = getPatientById(id);
        if(oldPatient.getWard()!=null){
            throw new InvalidEntityDataException(String.format
                    ("Patient is currently hospitalized in Ward '%s' and cannot be deleted.", oldPatient.getWard().getWardType()));
        }
        if(!oldPatient.getHospitalStaysCollection().isEmpty()){
            throw new InvalidEntityDataException("Patient has existing records in the system and cannot be deleted.");
        }
        patientRepo.deleteById(id);
        return oldPatient;
    }

    /**
     *
     * @return count of all Patients in the repository
     */
    @Override
    public long count() {
        return patientRepo.count();
    }
}
