package course.spring.hospitalinformationsystem.web;


import course.spring.hospitalinformationsystem.dto.*;
import course.spring.hospitalinformationsystem.entity.HospitalStay;
import course.spring.hospitalinformationsystem.service.HospitalStayService;
import course.spring.hospitalinformationsystem.service.PatientService;
import course.spring.hospitalinformationsystem.service.TestService;
import course.spring.hospitalinformationsystem.service.WardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static course.spring.hospitalinformationsystem.dto.Converter.convertToHospitalStay;
import static course.spring.hospitalinformationsystem.dto.Converter.getDischargeStay;
import static course.spring.hospitalinformationsystem.dto.Converter.getHospitalStayDto;
import static course.spring.hospitalinformationsystem.dto.Converter.getUpdatedHospitalStay;
import static course.spring.hospitalinformationsystem.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/hospitalstays")
public class HospitalStayRestController {

    private HospitalStayService hospitalStayService;
    private PatientService patientService;
    private WardService wardService;
    private TestService testService;

    @Autowired
    public HospitalStayRestController(HospitalStayService hospitalStayService, PatientService patientService,
                                      WardService wardService, TestService testService) {
        this.hospitalStayService = hospitalStayService;
        this.patientService = patientService;
        this.wardService = wardService;
        this.testService = testService;
    }

    /**
     *
     * @return Collection of HospitalStayDto models of all HospitalStays in the repository
     */
    @GetMapping
    public Collection<HospitalStayDto> getAllHospitalStays() {
        Collection<HospitalStay> stays = hospitalStayService.getAllHospitalStays();

        return stays.stream().map(Converter::getHospitalStayDto).collect(Collectors.toList());
    }

    /**
     *
     * @param id ID of the HospitalStay
     * @return HospitalStayDto model of the HospitalStay with param id
     */
    @GetMapping("/{id:\\d+}")
    public HospitalStayDto getStayById(@PathVariable("id") Long id){
        HospitalStay stay = hospitalStayService.getHospitalStayById(id);
        return getHospitalStayDto(stay);
    }

    /**
     *
     * @param stayId ID of the HospitalStay, whose tests will be collected
     * @return Collection of TestDto models of all tests with stayId corresponding to the param stayId
     */
    @GetMapping("/{stayId}/tests")
    public Collection<TestDto> getAllTestsForHospitalStay(@PathVariable("stayId") Long stayId){
        Collection<TestDto> tests = testService.getAllTestsForHospitalStay(stayId).stream().map(Converter::getTestDto).collect(Collectors.toSet());
        return tests;
    }

    /**
     *
     * @param inputHospitalStayDto model of the HospitalStay that has to be created
     * @param errors that may appear due to invalid input data
     * @return HospitalStayDto model of the created HospitalStay
     */
    @PostMapping
    public ResponseEntity<HospitalStayDto> createNewHospitalStay(@Valid @RequestBody InputHospitalStayDto inputHospitalStayDto, Errors errors){
        handleValidationErrors(errors);
        HospitalStay created = hospitalStayService.addHospitalStay(convertToHospitalStay(inputHospitalStayDto, patientService, wardService));
        HospitalStayDto stayDto = getHospitalStayDto(created);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest().pathSegment("{id}")
                .buildAndExpand(created.getId()).toUri()).body(stayDto);
    }

    /**
     *
     * @param id of the HospitalStay that will be updated
     * @param updateStayDto input DTO model of the HospitalStay with applied changes
     * @param errors that may appear due to invalid input data
     * @return HospitalStayDto model of the updated HospitalStay
     */
    @PutMapping("/{id}")
    public HospitalStayDto updateHospitalStay(@PathVariable("id") Long id, @Valid @RequestBody UpdateHospitalStayDto updateStayDto,
                                              Errors errors){
        handleValidationErrors(errors);
        HospitalStay stay = getUpdatedHospitalStay(updateStayDto, hospitalStayService, patientService, wardService);
        HospitalStay updated = hospitalStayService.updateHospitalStay(stay);
        return getHospitalStayDto(updated);

    }

    /**
     *
     * @param id ID of the HospitalStay where decursus will be added
     * @param decursus The value of the "text" property of the Decursus that will be added to the
     *                 Decursus collection in HospitalStay
     * @param errors that may appear due to invalid input data
     * @return HospitalStayDto model of the updated with the added Decursus HospitalStay
     */
    @PutMapping("/{id}/decursus")
    public HospitalStayDto addDecursus(@PathVariable("id")Long id, @Valid @RequestBody InputDecursusDto decursus, Errors errors){
        handleValidationErrors(errors);
      HospitalStay updated = hospitalStayService.addDecursus(id, decursus.getText());
      return getHospitalStayDto(updated);
    }

    /**
     *
     * @param id ID of the HospitalStay whose decursus collection will be edited
     * @param decId ID of the Decursus that will be modified
     * @param decursus the updated version of the Decursus with param decId
     * @return HospitalStayDto model of the updated with the edited Decursus HospitalStay
     */
    @PutMapping("/{id}/decursus/{decId}")
    public HospitalStayDto editDecursus(@PathVariable("id")Long id, @PathVariable("decId") Long decId,
                                        @Valid @RequestBody InputDecursusDto decursus){
        HospitalStay updated = hospitalStayService.editDecursus(id, decId, decursus.getText());
        return getHospitalStayDto(updated);
    }

    /**
     *
     * @param id ID of the HospitalStay in whose Decursus Collection an element will be deleted
     * @param decId ID of the Decursus that will be deleted
     * @return HospitalStayDto model of the updated with the deleted Decursus HospitalStay
     */
    @PutMapping("{id}/deletedecursus/{decId}")
    public HospitalStayDto deleteDecursus(@PathVariable("id")Long id, @PathVariable("decId")Long decId){
        HospitalStay updated = hospitalStayService.deleteDecursus(id, decId);
        return getHospitalStayDto(updated);
    }

    /**
     *
     * @param id ID of the HospitalStay whose treatment collection will be modified
     * @param medications - List of Strings with the elements that will be added to the treatment collection
     * @param errors that may appear due to invalid input data
     * @return HospitalStayDto model of the updated with added treatment HospitalStay
     */
    @PutMapping("/{id}/treatment")
    public HospitalStayDto addTreatment(@PathVariable("id")Long id, @Valid @RequestBody InputMedicationsDto medications, Errors errors){
       handleValidationErrors(errors);
        HospitalStay updated = hospitalStayService.addTreatment(id, medications.getMedicationList());
        return getHospitalStayDto(updated);
    }

    /**
     *
     * @param stayId ID of the HospitalStay, whose "isActive" property will be set to "false" value
     * @param dischargeHospitalStayDto input DTO model with changes that will be applied to the HospitalStay
     * @param errors that may appear due to invalid input data
     * @return HospitalStayDto model of the updated HospitalStay
     */
    @PutMapping("/{stayId}/discharge")
    public HospitalStayDto dischargePatient(@PathVariable("stayId") Long stayId,
                                            @Valid @RequestBody DischargeHospitalStayDto dischargeHospitalStayDto,
                                            Errors errors){
        handleValidationErrors(errors);
        dischargeHospitalStayDto.setId(stayId);
        HospitalStay temp = getDischargeStay(dischargeHospitalStayDto, hospitalStayService);
        HospitalStay discharged = hospitalStayService.dischargePatient(temp.getId(), temp);
        return getHospitalStayDto(discharged);
    }

    /**
     *
     * @param stayId ID of the HospitalStay, whose discharge summary will be provided
     * @return String value of the discharge summary
     */

    @GetMapping(value="/{stayId}/dischargesummary", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDischargeSummary(@PathVariable("stayId") Long stayId){
        return hospitalStayService.getDischargeSummary(stayId);
    }

    /**
     *
     * @param id of the HospitalStay that will be deleted
     * @return HospitalStayDto model of the HospitalStay that was deleted
     */
    @DeleteMapping("/{id}")
    public HospitalStayDto deleteStayById(@PathVariable("id") Long id){
        HospitalStay deleted = hospitalStayService.deleteHospitalStayById(id);
        return getHospitalStayDto(deleted);
    }

    /**
     *
     * @return count of all HospitalStays in the repository
     */
    @GetMapping(value ="/count", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getCountOfHospitalStays(){
        return Long.toString(hospitalStayService.count());
    }


}
