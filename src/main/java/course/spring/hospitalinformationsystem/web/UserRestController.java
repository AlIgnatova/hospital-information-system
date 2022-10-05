package course.spring.hospitalinformationsystem.web;


import course.spring.hospitalinformationsystem.dto.Converter;
import course.spring.hospitalinformationsystem.dto.UpdateUserDto;
import course.spring.hospitalinformationsystem.dto.UserDto;
import course.spring.hospitalinformationsystem.entity.User;
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

import static course.spring.hospitalinformationsystem.dto.Converter.convertFromUpdatedUser;
import static course.spring.hospitalinformationsystem.dto.Converter.getUserDto;
import static course.spring.hospitalinformationsystem.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    /**
     *
     * @return Collection of UserDto models of all Users in the repository
     */
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        Collection<User> users = userService.getAllUsers();
        return users.stream().map(Converter::getUserDto).collect(Collectors.toList());
    }

    /**
     *
     * @param id - ID of the User
     * @return UserDto model of the User with param id
     */
    @GetMapping("/{id:\\d+}")
    public UserDto getUserById(@PathVariable("id") Long id) {
        User returned = userService.getUserById(id);
        return getUserDto(returned);
    }

    /**
     *
     * @param user input User that has to be created
     * @param errors that may appear due to invalid input data
     * @return UserDto model of the created User
     */
    @PostMapping
    public ResponseEntity<UserDto> createNewUser(@Valid @RequestBody User user, Errors errors) {
        handleValidationErrors(errors);
        User created = userService.addUser(user);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest().pathSegment("{id}")
                .buildAndExpand(created.getId()).toUri()).body(getUserDto(created));
    }

    /**
     *
     * @param id of the User that will be updated
     * @param updateUserDto input DTO model of the User with applied changes
     * @param errors that may appear due to invalid input data
     * @return UserDto model of the updated User
     */
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserDto updateUserDto, Errors errors) {
        handleValidationErrors(errors);
        User user = convertFromUpdatedUser(updateUserDto, userService);
        User updated = userService.updateUser(user);
        return getUserDto(updated);
    }

    /**
     *
     * @param id of the User that will be deleted
     * @return UserDto model of the User that was deleted
     */
    @DeleteMapping("/{id}")
    public UserDto deleteUserById(@PathVariable("id") Long id) {
        User deleted = userService.deleteUserById(id);
        return getUserDto(deleted);
    }

    /**
     *
     * @return count of all Users in the repository
     */
    @GetMapping(value = "/count", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getCountOfUsers() {
        return Long.toString(userService.count());
    }


}
