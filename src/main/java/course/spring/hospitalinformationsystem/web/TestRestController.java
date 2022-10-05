package course.spring.hospitalinformationsystem.web;


import course.spring.hospitalinformationsystem.dto.*;
import course.spring.hospitalinformationsystem.entity.Test;
import course.spring.hospitalinformationsystem.service.HospitalStayService;
import course.spring.hospitalinformationsystem.service.PatientService;
import course.spring.hospitalinformationsystem.service.TestService;
import course.spring.hospitalinformationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

import static course.spring.hospitalinformationsystem.dto.Converter.*;
import static course.spring.hospitalinformationsystem.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/tests")
public class TestRestController {

    private TestService testService;
    private UserService userService;
    private PatientService patientService;
    private HospitalStayService hospitalStayService;

    public TestRestController(TestService testService, UserService userService, PatientService patientService,
                              HospitalStayService hospitalStayService) {
        this.testService = testService;
        this.userService = userService;
        this.patientService = patientService;
        this.hospitalStayService = hospitalStayService;
    }

    /**
     *
     * @return Collection of TestDto models of all Tests in the repository
     */
    @GetMapping
    public Collection<TestDto> getAllTests() {
        Collection<Test> tests = testService.getAllTests();
        return tests.stream()
                .map(Converter::getTestDto)
                .collect(Collectors.toList());
    }

    /**
     *
     * @return Collection of TestDto models of all Tests in the repository, that have property "isCompleted" with value "false"
     */
    @GetMapping("/notcompleted")
    public Collection<TestDto> getAllUncompletedTests(){
        return testService.getAllUncompletedTests().stream()
                .map(Converter::getTestDto)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param id - ID of the Test
     * @return TestDto model of the Test with param id
     */
    @GetMapping("/{id:\\d+}")
    public TestDto getTestById(@PathVariable("id") Long id) {
        return getTestDto(testService.getTestById(id));
    }

    /**
     *
     * @param inputTestDto input InputTestDto model of the Test that has to be created
     * @param errors that may appear due to invalid input data
     * @return TestDto model of the created Test
     */
    @PostMapping
    public ResponseEntity<TestDto> createNewTest(@Valid @RequestBody InputTestDto inputTestDto, Errors errors) {
        handleValidationErrors(errors);
        Test test = convertToNewTest(inputTestDto, patientService, hospitalStayService, testService, userService);
        Test created = testService.createTest(test);
        TestDto testDto = getTestDto(created);

        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest().pathSegment("{id}")
                .buildAndExpand(created.getId()).toUri()).body(testDto);
    }

    /**
     *
     * @param id of the Test that will be updated
     * @param updateTestDto input DTO model of the Test with applied changes
     * @param errors that may appear due to invalid input data
     * @return TestDto model of the updated Test
     */
    @PutMapping("/{id}")
    public TestDto editTestById(@PathVariable("id") Long id, @Valid @RequestBody UpdateTestDto updateTestDto, Errors errors){
        handleValidationErrors(errors);
        Test edited = testService.updateTest(convertToTest(updateTestDto, patientService, hospitalStayService, testService, userService
        ));
        return getTestDto(edited);
    }

    /**
     *
     * @param id - ID of the Test that will be completed
     * @param completeTestDto - input DTO model with changes that will be applied to the Test
     * @param errors that may appear due to invalid input data
     * @return TestDto model of the completed Test
     */
    @PutMapping("/{id}/complete")
    public TestDto completeTest(@PathVariable("id") Long id, @Valid @RequestBody CompleteTestDto completeTestDto, Errors errors) {
        handleValidationErrors(errors);
        Test completed = testService.completeTest(convertFromCompletedTest(completeTestDto, userService, testService));
        return getTestDto(completed);

    }

    /**
     *
     * @param id of the Test that will be deleted
     * @return TestDto model of the Test that was deleted
     */
    @DeleteMapping("/{id}")
    public TestDto deleteTestById(@PathVariable("id") Long id) {
        Test deleted = testService.deleteTestById(id);
        return getTestDto(deleted);
    }

    /**
     *
     * @return count of all Tests in the repository
     */
    @GetMapping(value = "/count", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getCountOfTests() {
        return Long.toString(testService.count());
    }
}
