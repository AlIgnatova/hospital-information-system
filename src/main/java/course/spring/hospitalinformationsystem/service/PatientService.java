package course.spring.hospitalinformationsystem.service;


import course.spring.hospitalinformationsystem.entity.HospitalStay;
import course.spring.hospitalinformationsystem.entity.Patient;
import course.spring.hospitalinformationsystem.entity.Test;
import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;

import java.util.Collection;

public interface PatientService {
    Collection<Patient> getAllPatients();
    Patient getPatientById(Long id) throws NonExistingEntityException;
    Patient getPatientByEGN(String EGN) throws NonExistingEntityException;
    Collection<Test> getAllTestsForPatientById(Long id) throws NonExistingEntityException;
    Collection<HospitalStay> getAllHospitalStaysForPatientById(Long id) throws NonExistingEntityException;
    Patient addPatient(Patient patient) throws InvalidEntityDataException;
    Patient updatePatient(Patient patient) throws NonExistingEntityException, InvalidEntityDataException;
    Patient deletePatientById(Long id) throws NonExistingEntityException;
    long count();
}
