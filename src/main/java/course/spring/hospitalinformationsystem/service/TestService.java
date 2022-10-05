package course.spring.hospitalinformationsystem.service;


import course.spring.hospitalinformationsystem.entity.Test;
import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;

import java.util.Collection;

public interface TestService {
    Collection<Test> getAllTests();
    Collection<Test> getAllTestsForPatient(Long patientId) throws NonExistingEntityException;
    Collection<Test> getAllTestsForHospitalStay(Long stayId) throws NonExistingEntityException;
    Collection<Test> getAllUncompletedTests();
    Test getTestById(Long testId) throws NonExistingEntityException;
    Test addTest(Test test) throws InvalidEntityDataException;
    Test createTest(Test test) throws InvalidEntityDataException;
    Test updateTest(Test test) throws NonExistingEntityException, InvalidEntityDataException;
    Test completeTest(Test test) throws NonExistingEntityException;
    Test deleteTestById(Long id) throws NonExistingEntityException;
    long count();
}
