package course.spring.hospitalinformationsystem.web;


import course.spring.hospitalinformationsystem.dto.Converter;
import course.spring.hospitalinformationsystem.dto.HospitalStayDto;
import course.spring.hospitalinformationsystem.dto.PatientDto;
import course.spring.hospitalinformationsystem.dto.TestDto;
import course.spring.hospitalinformationsystem.entity.Patient;
import course.spring.hospitalinformationsystem.entity.Test;
import course.spring.hospitalinformationsystem.service.HospitalStayService;
import course.spring.hospitalinformationsystem.service.PatientService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

import static course.spring.hospitalinformationsystem.dto.Converter.getPatientDto;
import static course.spring.hospitalinformationsystem.dto.Converter.getTestDto;
import static course.spring.hospitalinformationsystem.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/patients")
public class PatientRestController {


    private PatientService patientService;
    private HospitalStayService hospitalStayService;

    public PatientRestController(PatientService patientService, HospitalStayService hospitalStayService) {
        this.patientService = patientService;
        this.hospitalStayService = hospitalStayService;
    }

    /**
     *
     * @return Collection of PatientDto models of all Patients in the repository
     */
    @GetMapping
    public Collection<PatientDto> getAllPatients(){
        Collection<Patient> patients = patientService.getAllPatients();
        return patients.stream().map(Converter::getPatientDto).collect(Collectors.toList());
    }

    /**
     *
     * @param id ID of the Patient
     * @return PatientDto model of the Patient with param id
     */
    @GetMapping("/{id:\\d+}")
    public PatientDto getPatientById(@PathVariable("id") Long id){
        Patient patient = patientService.getPatientById(id);
        return getPatientDto(patient);
    }

    /**
     *
     * @param id of the Patient, whose tests will be collected
     * @return Collection of TestDto models of all tests with patientId corresponding to the param id
     */
    @GetMapping("/{patientId}/tests")
    public Collection<TestDto> getAllTestsForPatient(@PathVariable("patientId") Long id){
        Collection<Test> tests = patientService.getAllTestsForPatientById(id);
        return tests.stream().map(t -> getTestDto(t)).collect(Collectors.toList());
    }

    /**
     *
     * @param id of the Patient, whose hospitalStays we will collect
     * @return Collection of HospitalStayDto models of all hospitalStays with patientId corresponding to the param id
     */
    @GetMapping("/{patientId}/hospitalstays")
    public Collection<HospitalStayDto> getAllHospitalStaysForPatient(@PathVariable("patientId") Long id){
        Collection<HospitalStayDto> stays = hospitalStayService.getAllHospitalStays().stream()
                .filter(st -> st.getPatient().getId().equals(id))
                .map(Converter::getHospitalStayDto).collect(Collectors.toList());

        return stays;
    }

    /**
     *
     * @param patient input Patient that has to be created
     * @param errors that may appear due to invalid input data
     * @return PatientDto model of the created Patient
     */
    @PostMapping
    public ResponseEntity<PatientDto> createNewPatient(@Valid @RequestBody Patient patient, Errors errors){
        handleValidationErrors(errors);
        Patient created = patientService.addPatient(patient);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest().pathSegment("{id}")
                .buildAndExpand(created.getId()).toUri()).body(getPatientDto(created));
    }

    /**
     *
     * @param id of the Patient that will be updated
     * @param patient input Patient with applied changes
     * @param errors that may appear due to invalid input data
     * @return PatientDto model of the updated Patient
     */
    @PutMapping("/{id}")
    public PatientDto updatePatient(@PathVariable("id") Long id, @Valid @RequestBody Patient patient, Errors errors){
        handleValidationErrors(errors);
        Patient updated = patientService.updatePatient(patient);
        return getPatientDto(updated);
    }

    /**
     *
     * @param id of the Patient that will be deleted
     * @return PatientDto model of the Patient that was deleted
     */
    @DeleteMapping("/{id}")
    public PatientDto deleteById(@PathVariable("id") Long id) {
        Patient deleted = patientService.deletePatientById(id);
        return getPatientDto(deleted);
    }

    /**
     *
     * @return count of all Patients in the repository
     */
    @GetMapping(value = "/count", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getCountOfPatients(){
        return Long.toString(patientService.count());
    }


}
