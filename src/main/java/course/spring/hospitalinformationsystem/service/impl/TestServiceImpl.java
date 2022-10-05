package course.spring.hospitalinformationsystem.service.impl;

import course.spring.hospitalinformationsystem.dao.HospitalStayRepository;
import course.spring.hospitalinformationsystem.dao.PatientRepository;
import course.spring.hospitalinformationsystem.dao.TestRepository;
import course.spring.hospitalinformationsystem.entity.*;
import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import course.spring.hospitalinformationsystem.exception.MethodNotAllowedException;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;
import course.spring.hospitalinformationsystem.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static course.spring.hospitalinformationsystem.service.serviceUtils.UserManager.getLoggedUser;


@Service
@Transactional
@Slf4j
public class TestServiceImpl implements TestService {
    private HospitalStayService hospitalStayService;
    private WardService wardService;
    private TestRepository testRepo;
    private HospitalStayRepository hospitalStayRepo;
    private PatientRepository patientRepo;

    @Autowired
    public TestServiceImpl(HospitalStayService hospitalStayService, WardService wardService, TestRepository testRepo,
                           HospitalStayRepository hospitalStayRepo, PatientRepository patientRepo) {
        this.hospitalStayService = hospitalStayService;
        this.wardService = wardService;
        this.testRepo = testRepo;
        this.hospitalStayRepo = hospitalStayRepo;
        this.patientRepo = patientRepo;
    }

    /**
     * @return Collection of all Tests in the repository
     */
    @Override
    @RolesAllowed({"ADMINISTRATOR", "DOCTOR"})
    public Collection<Test> getAllTests() {
        return testRepo.findAll();
    }


    /**
     * @param patientId ID of the Patient, whose Tests will be collected
     * @return Collection of all Tests with patientId corresponding to the param patientId
     * @throws NonExistingEntityException if there are no existing Tests with param patientID in the repository
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<Test> getAllTestsForPatient(Long patientId) throws NonExistingEntityException {
        if (testRepo.findAll().stream().noneMatch(test -> test.getPatient().getId().equals(patientId))) {
            throw new NonExistingEntityException(String.format
                    ("There are no tests available for patient with id='%d'.", patientId));
        }
        return testRepo.findAll().stream().filter(test -> test.getPatient().getId().equals(patientId)).collect(Collectors.toList());
    }

    /**
     * @param stayId ID of HospitalStay, whose Tests will be collected
     * @return Collection of all Tests with stayId corresponding to the param stayId
     * @throws NonExistingEntityException if there are no existing Tests with param stayId in the repository
     */
    @Override
    @Transactional(readOnly = true)
    @RolesAllowed({"NURSE", "DOCTOR"})
    public Collection<Test> getAllTestsForHospitalStay(Long stayId) throws NonExistingEntityException {
        HospitalStay stay = hospitalStayService.getHospitalStayById(stayId);
        boolean areExistingTestsForStay = testRepo.findAll().stream().anyMatch(test -> test.getHospitalStay().getId().equals(stayId));
        if (!areExistingTestsForStay) {
            throw new NonExistingEntityException(String.format
                    ("There are no tests available for Hospital Stay with id='%d'.", stayId));
        }
        List<Test> tests = testRepo.findAll().stream().filter(test -> test.getHospitalStay().getId().equals(stayId)).collect(Collectors.toList());

        User currentUser = getLoggedUser();
        boolean currentUserIsNurse = "NURSE".equals(currentUser.getRole().toString());
        if (currentUserIsNurse) {
            boolean userIsStaffOfSameWard = stay.getWard().getStaff().contains(currentUser);
            if (!userIsStaffOfSameWard) {
                throw new MethodNotAllowedException("This information cannot be viewed.");
            }
            if (!stay.isStayActive()) {
                throw new MethodNotAllowedException("Patient is already discharged.");
            }
        }
        return tests;
    }

    /**
     * @return Collection of all Tests in the repository, that have property "isCompleted" with value "false"
     */
    @Override
    @Transactional(readOnly = true)
    @RolesAllowed("DOCTOR")
    public Collection<Test> getAllUncompletedTests() {
        String wardType = "";
        boolean userWorksInImaging = wardService.getWardByType("IMAGING").getStaff().contains(getLoggedUser());
        boolean userWorksInLaboratory = wardService.getWardByType("LABORATORY").getStaff().contains(getLoggedUser());
        if (userWorksInImaging) {
            wardType = "IMAGING";
        } else if (userWorksInLaboratory) {
            wardType = "LABORATORY";
        } else {
            throw new MethodNotAllowedException("This information is not available.");
        }
        List<Test> notCompletedTests = new ArrayList<>();
        List<Test> alltests = new ArrayList<>(getAllTests());
        if (wardType.equals("IMAGING")) {
            for (int i = 0; i < alltests.size(); i++) {
                if (!alltests.get(i).isCompleted() && alltests.get(i).getTestType().toString().equals("IMAGING")) {
                    notCompletedTests.add(alltests.get(i));
                }
            }
        } else {
            for (int i = 0; i < alltests.size(); i++) {
                if (!alltests.get(i).isCompleted() && !alltests.get(i).getTestType().toString().equals("IMAGING")) {
                    notCompletedTests.add(alltests.get(i));
                }
            }
        }
        return notCompletedTests;
    }

    /**
     * @param testId ID of the Test that will be returned
     * @return Test with param id
     * @throws NonExistingEntityException if Test with param testId is not present in the repository
     */
    @Override
    @Transactional(readOnly = true)
    public Test getTestById(Long testId) throws NonExistingEntityException {
        Test test = testRepo.findById(testId).orElseThrow(() -> new NonExistingEntityException(String.format
                ("Test with id='%d' does not exist.", testId)));
        String error = "This information is not available.";
        boolean currentUserIsNurse = "NURSE".equals(getLoggedUser().getRole().toString());
        if (currentUserIsNurse) {
            if (!test.getHospitalStay().isStayActive()) {
                throw new MethodNotAllowedException(error);
            }
            boolean userIsStaffOfSameWard = test.getHospitalStay().getWard().getStaff().contains(getLoggedUser());
            if (!userIsStaffOfSameWard) {
                throw new MethodNotAllowedException(error);
            }
        }
        return test;
    }

    /**
     * @param test that will be added to the repository
     * @return the created Test entity
     * @throws InvalidEntityDataException in case of invalid input data
     */
    @Override
    public Test addTest(Test test) throws InvalidEntityDataException {
        isPatientDischarged(test);
        test.setId(null);
        test.setCreated(LocalDateTime.now());
        test.setModified(LocalDateTime.now());
        return testRepo.save(test);
    }

    /**
     * @param addedTest Test that will be created
     * @return the created Test
     * @throws InvalidEntityDataException in case of invalid input data
     */
    @Override
    public Test createTest(Test addedTest) throws InvalidEntityDataException {
        Test test = addTest(addedTest);
        HospitalStay stay = test.getHospitalStay();
        List<Test> tests = new ArrayList<>(stay.getTestsList());
        tests.add(test);
        stay.setTestsList(new HashSet<>(tests));
        stay.setModified(LocalDateTime.now());
        hospitalStayRepo.save(stay);

        Patient patient = test.getPatient();
        List<Test> patientTests = new ArrayList<>(patient.getTestsCollection());
        patientTests.add(test);
        patient.setTestsCollection(patientTests);
        patient.setModified(LocalDateTime.now());
        patientRepo.save(patient);
        return test;
    }

    /**
     * @param test that will be updated
     * @return the updated Test
     * @throws NonExistingEntityException in case the input Test's ID does not match any existing Test in the repository
     * @throws InvalidEntityDataException in case of invalid input data
     */
    @Override
    public Test updateTest(Test test) throws NonExistingEntityException, InvalidEntityDataException {
        Test oldTest = getTestById(test.getId());
        boolean whatToBeTestedIsSame = oldTest.getWhatToBeTested().equals(test.getWhatToBeTested());
        if (oldTest.isCompleted() && !whatToBeTestedIsSame) {
            throw new MethodNotAllowedException(String.format
                    ("Test with ID'%d'is already completed and required test parameters cannot be changed.", test.getId()));
        }
        test.setCreated(oldTest.getCreated());
        test.setModified(LocalDateTime.now());

        return testRepo.save(test);
    }

    /**
     * @param test that will be completed - will be added values to fields "performedBy" and "result" and property
     *             "isCompleted" will be set to value "true"
     *             * @return the completed Test
     * @throws NonExistingEntityException in case the input Test's ID does not match any existing Test in the repository
     */
    @Override
    public Test completeTest(Test test) throws NonExistingEntityException {
        String testType = test.getTestType().toString();
        boolean userIsStaffOfSameTestDepartment = wardService.getWardByType(testType).getStaff().contains(getLoggedUser());
        if (!userIsStaffOfSameTestDepartment) {
            throw new MethodNotAllowedException("This option is not available.");
        }
        if (!testRepo.findById(test.getId()).isPresent()) {
            throw new NonExistingEntityException(String.format
                    ("Test with id='%s' does not exist.", test.getId()));
        }
        if (test.isCompleted()) {
            throw new MethodNotAllowedException(String.format
                    ("Test with ID='%d' is already completed. You can edit it if changes are required.", test.getId()));
        }
        test.setCompleted(true);
        test.setPerformedBy(getLoggedUser());
        hospitalStayRepo.save(test.getHospitalStay());
        return updateTest(test);
    }

    /**
     * @param id of the Test that will be deleted
     * @return the Test that was deleted
     * @throws NonExistingEntityException if Test with param id is not present in the repository
     */
    @Override
    public Test deleteTestById(Long id) throws NonExistingEntityException {
        Test oldTest = getTestById(id);
        isPatientDischarged(oldTest);
        List<Test> stayTests = new ArrayList<>(oldTest.getHospitalStay().getTestsList());
        stayTests.remove(oldTest);
        oldTest.getHospitalStay().setModified(LocalDateTime.now());
        hospitalStayRepo.save(oldTest.getHospitalStay());

        List<Test> patientTests = new ArrayList<>(oldTest.getPatient().getTestsCollection());
        patientTests.remove(oldTest);
        oldTest.getPatient().setModified(LocalDateTime.now());
        patientRepo.save(oldTest.getPatient());
        testRepo.deleteById(id);
        return oldTest;
    }

    /**
     * @return count of all Tests in the repository
     */
    @Override
    public long count() {
        return testRepo.count();
    }

    private static void isPatientDischarged(Test oldTest) {
        HospitalStay stay = oldTest.getHospitalStay();
        if (!stay.isStayActive()) {
            throw new InvalidEntityDataException(String.format
                    ("Patient for Hospital stay with id='%d' is already discharged.", stay.getId()));
        }
    }



}
