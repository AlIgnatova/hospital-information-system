package course.spring.hospitalinformationsystem.web;

import course.spring.hospitalinformationsystem.dto.Converter;
import course.spring.hospitalinformationsystem.dto.InputWardDto;
import course.spring.hospitalinformationsystem.dto.UpdateWardDto;
import course.spring.hospitalinformationsystem.dto.WardDto;
import course.spring.hospitalinformationsystem.entity.Ward;
import course.spring.hospitalinformationsystem.service.UserService;
import course.spring.hospitalinformationsystem.service.WardService;
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
@RequestMapping("/api/wards")
public class WardRestController {

    private WardService wardService;
    private UserService userService;

    @Autowired
    public WardRestController(WardService wardService, UserService userService) {
        this.wardService = wardService;
        this.userService = userService;
    }

    /**
     *
     * @return Collection of WardDto models of all Wards in the repository
     */
    @GetMapping
    public Collection<WardDto> getAllWards() {
        Collection<Ward> wards = wardService.getAllWards();
        return wards.stream()
                .map(Converter::getWardDto)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param id - ID of the Ward
     * @return WardDto model of the Ward with param id
     */
    @GetMapping("/{id:\\d+}")
    public WardDto getWardById(@PathVariable("id") Long id) {
        Ward returned = wardService.getWardById(id);
        return getWardDto(returned);
    }

    /**
     *
     * @param inputWardDto input DTO model of the Ward that has to be created
     * @param errors that may appear due to invalid input data
     * @return WardDto model of the created Ward
     */

    @PostMapping
    public ResponseEntity<WardDto> createNewWard(@Valid @RequestBody InputWardDto inputWardDto, Errors errors) {
        handleValidationErrors(errors);
        Ward created = wardService.addWard(convertToWard(inputWardDto, userService));
        WardDto wardDto = getWardDto(created);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest().pathSegment("{id}")
                .buildAndExpand(created.getId()).toUri()).body(wardDto);
    }

    /**
     *
     * @param id of the Ward that will be updated
     * @param updateWardDto input DTO model of the ward with applied changes
     * @param errors that may appear due to invalid input data
     * @return WardDto model of the updated Ward
     */

    @PutMapping("/{id}")
    public WardDto updateWard(@PathVariable("id") Long id, @Valid @RequestBody UpdateWardDto updateWardDto, Errors errors) {
        handleValidationErrors(errors);
        Ward toBeUpdated = convertFromUpdatedWard(updateWardDto, userService, wardService);
        Ward updated = wardService.updateWard(toBeUpdated);
        return getWardDto(updated);
    }

    /**
     *
     * @param id of the Ward that will be deleted
     * @return WardDto model of the Ward that was deleted
     */
    @DeleteMapping("/{id}")
    public WardDto deleteWardById(@PathVariable("id") Long id) {
        Ward removed = wardService.deleteWardById(id);
        return getWardDto(removed);
    }

    /**
     *
     * @return count of all Wards in the repository
     */

    @GetMapping(value = "/count", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getCountOfWards() {
        return Long.toString(wardService.count());
    }


}
