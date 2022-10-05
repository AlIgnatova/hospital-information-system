package course.spring.hospitalinformationsystem.dto;

import course.spring.hospitalinformationsystem.entity.*;
import course.spring.hospitalinformationsystem.entity.enums.DischargeDisposition;
import course.spring.hospitalinformationsystem.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Converter {
    private UserService userService;
    private TestService testService;
    private WardService wardService;
    private HospitalStayService hospitalStayService;
    private PatientService patientService;

    public Converter(UserService userService, TestService testService, WardService wardService,
                     HospitalStayService hospitalStayService, PatientService patientService) {
        this.userService = userService;
        this.testService = testService;
        this.wardService = wardService;
        this.hospitalStayService = hospitalStayService;
        this.patientService = patientService;
    }

    public static String getDateAndTime(LocalDateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd, HH:mm");
        return dateTime.format(formatter);
    }
    public static Test convertFromCompletedTest(CompleteTestDto completeTestDto, UserService userService, TestService testService) {
        Test test = testService.getTestById(completeTestDto.getId());
        test.setResult(completeTestDto.getResult());
        return test;
    }
    public static HospitalStay getDischargeStay(DischargeHospitalStayDto dischargeHospitalStayDto, HospitalStayService hospitalStayService){
        HospitalStay stay = hospitalStayService.getHospitalStayById(dischargeHospitalStayDto.getId());
        stay.setDiagnosisOnDischarge(dischargeHospitalStayDto.getDiagnosisOnDischarge());
        stay.setDischargeDisposition(dischargeHospitalStayDto.getDischargeDisposition());
        return stay;
    }
    public static HospitalStayDto getHospitalStayDto(HospitalStay stay) {
        List<String> medList = stay.getMedicationsList().stream().toList();
        List<String> testList = getTests(stay.getTestsList().stream().toList());
        HospitalStayDto created = new HospitalStayDto();
        created.setId(stay.getId());
        created.setPatient(String.format("%s %s %s, EGN: %s", stay.getPatient().getFirstName(),
                stay.getPatient().getMiddleName(), stay.getPatient().getLastName(), stay.getPatient().getEGN()));
        created.setWard(stay.getWard().getWardType().toString());
        created.setDiagnosisOnAdmission(stay.getDiagnosisOnAdmission().toString());
        created.setAdmissionDateAndTime(getDateAndTime(stay.getAdmissionDateAndTime()));
        created.setDecursusList(getDecursus(new ArrayList<>(stay.getDecursusList())));
        created.setMedicationsList(medList);
        created.setTestsList(testList);
        created.setDiagnosisOnDischarge("");
        created.setDischargeDisposition(DischargeDisposition.NOT_DISCHARGED.getDischargeDisposition());
        if(!stay.isStayActive()) {
            created.setDiagnosisOnDischarge(stay.getDiagnosisOnDischarge().toString());
            created.setDischargeDateAndTime(getDateAndTime(stay.getDischargeDateAndTime()));
            created.setDischargeDisposition(stay.getDischargeDisposition().getDischargeDisposition());
        }
        return created;
    }
    private static List<String> getDecursus(List<Decursus> decursusList) {
        List<String> dec = new ArrayList<>();
        decursusList.stream().forEach(decursus -> {
            dec.add(String.format(decursus.getText() + " (MD %s %s, %s)", decursus.getAddedBy().getFirstName(),
                    decursus.getAddedBy().getLastName(), getDateAndTime(decursus.getCreated())));
        });
        return dec;
    }
    public static List<String> getTests(List<Test> testsList) {
        List<String> tests = new ArrayList<>();
        for (int i = 0; i < testsList.size(); i++) {
            String current =String.format("Test id='%d', type='%s'; result: '%s'.", testsList.get(i).getId(),
                    testsList.get(i).getTestType().toString(), testsList.get(i).getResult());
            tests.add(current);
        }
        return tests;
    }
    public static HospitalStay convertToHospitalStay(InputHospitalStayDto inputHospitalStayDto, PatientService patientService,
                                                     WardService wardService){
        return new HospitalStay(patientService.getPatientByEGN(inputHospitalStayDto.getPatientEGN()),
                wardService.getWardById(inputHospitalStayDto.getWardId()), inputHospitalStayDto.getDiagnosisOnAdmission());
    }
    public static Test convertToNewTest(InputTestDto inputTestDto, PatientService patientService, HospitalStayService hospitalStayService,
                                        TestService testService, UserService userService){
        return new Test(patientService.getPatientById(hospitalStayService.getHospitalStayById(inputTestDto.getHospitalStayId()).getPatient().getId()), hospitalStayService.getHospitalStayById(inputTestDto.getHospitalStayId()),
                inputTestDto.getTestType(), inputTestDto.getWhatToBeTested());
    }
    public static Ward convertToWard(InputWardDto inputWardDto, UserService userService){
        return new Ward(inputWardDto.getWardType(),
                inputWardDto.getBedCapacity(), getUsersStaff(inputWardDto.getStaff(), userService));
    }

    public static User convertToUser(InputUserDto inputUserDto){
        return new User(inputUserDto.getFirstName(), inputUserDto.getLastName(), inputUserDto.getPassword(),
                inputUserDto.getRole(),inputUserDto.getEmailAddress());
    }
    private static Set<User> getUsersStaff(List<String> staff, UserService userService) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < staff.size(); i++) {
            users.add(userService.getUserByUsername(staff.get(i)));
        }
        return new HashSet<>(users);
    }
    public static PatientDto getPatientDto(Patient patient) {
        return new PatientDto(patient.getId(), patient.getFirstName(), patient.getMiddleName(), patient.getLastName(), patient.getEGN());
    }
    public static TestDto getTestDto(Test test) {
        var testDto = new TestDto();
        testDto.setId(test.getId());
        testDto.setPatient(String.format("%s %s %s", test.getPatient().getFirstName(),
                test.getPatient().getMiddleName(), test.getPatient().getLastName()));
        testDto.setHospitalStayId(test.getHospitalStay().getId());
        testDto.setTestType(test.getTestType().toString());
        testDto.setWhatToBeTested(test.getWhatToBeTested());
        if (test.isCompleted()) {
            testDto.setPerformedBy(String.format("MD '%s %s'.", test.getPerformedBy().getFirstName(), test.getPerformedBy().getLastName()));
            testDto.setResult(test.getResult());
        }
        return testDto;
    }
    public static HospitalStay getUpdatedHospitalStay(UpdateHospitalStayDto updatedStayDto, HospitalStayService hospitalStayService,
                                                      PatientService patientService, WardService wardService){
        HospitalStay tempStay = hospitalStayService.getHospitalStayById(updatedStayDto.getId());
        tempStay.setPatient(patientService.getPatientByEGN(updatedStayDto.getPatientEGN()));
        tempStay.setWard(wardService.getWardById(updatedStayDto.getWardId()));
        tempStay.setDiagnosisOnAdmission(updatedStayDto.getDiagnosisOnAdmission());
        if(!tempStay.isStayActive()){
            tempStay.setDiagnosisOnDischarge(updatedStayDto.getDiagnosisOnDischarge());
            tempStay.setDischargeDisposition(updatedStayDto.getDischargeDisposition());
        }
        return tempStay;
    }
    public static Test convertToTest(UpdateTestDto updateTestDto, PatientService patientService, HospitalStayService hospitalStayService,
                                     TestService testService, UserService userService){
        Test tempTest = testService.getTestById(updateTestDto.getId());

        tempTest.setPatient(patientService.getPatientById(updateTestDto.getPatientId()));
        tempTest.setHospitalStay(hospitalStayService.getHospitalStayById(updateTestDto.getHospitalStayId()));
        tempTest.setTestType(updateTestDto.getTestType());
        tempTest.setWhatToBeTested(updateTestDto.getWhatToBeTested());
        if(tempTest.isCompleted()){
            tempTest.setPerformedBy(userService.getUserByUsername(updateTestDto.getPerformedBy()));
            tempTest.setResult(updateTestDto.getResult());
        }
        return tempTest;
    }
    public static User convertFromUpdatedUser(UpdateUserDto updateUserDto, UserService userService) {
        User user = userService.getUserById(updateUserDto.getId());
        BeanUtils.copyProperties(updateUserDto, user);

        return user;
    }
    public static Ward convertFromUpdatedWard(UpdateWardDto updateWardDto, UserService userService, WardService wardService) {

        Ward ward = wardService.getWardById(updateWardDto.getId());
        int newAvailability = (ward.getBedAvailability()+ (updateWardDto.getBedCapacity() - ward.getBedCapacity()));
        ward.setWardType(updateWardDto.getWardType());
        ward.setBedCapacity(updateWardDto.getBedCapacity());
        ward.setBedAvailability(newAvailability);
        ward.setStaff(getUsersStaff(updateWardDto.getStaff(), userService));
        return ward;
    }
    public static UserDto getUserDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setRole(user.getRole().toString());
        return userDto;
    }
    public static WardDto getWardDto(Ward ward) {
        return new WardDto(ward.getId(), ward.getWardType().toString(), ward.getBedAvailability(), getStaff(ward), getPatients(ward));
    }
    private static List<String> getPatients(Ward ward) {
        List<String> patients = new ArrayList<>();
        for (int i = 0; i < ward.getPatients().size() ; i++) {
            String patientData = ward.getPatients().get(i).getFirstName() + " " +
                    ward.getPatients().get(i).getLastName() + " " + ward.getPatients().get(i).getEGN();
            patients.add(patientData);
        }
        return patients;
    }
    private static List<String> getStaff(Ward ward) {
        List<String> usernames = new ArrayList<>();
        List<User> sortedStaff = ward.getStaff().stream().sorted(Comparator.comparing(st -> st.getRole().toString())).collect(Collectors.toList());
        for (int i = 0; i < sortedStaff.size() ; i++) {
            String info = String.format("%s %s %s", sortedStaff.get(i).getRole().toString(),
                    sortedStaff.get(i).getFirstName(), sortedStaff.get(i).getLastName());
            usernames.add(info);
        }
        return usernames;
    }
}
