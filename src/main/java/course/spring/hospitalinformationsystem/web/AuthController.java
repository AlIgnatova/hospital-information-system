package course.spring.hospitalinformationsystem.web;

import course.spring.hospitalinformationsystem.dto.Converter;
import course.spring.hospitalinformationsystem.dto.Credentials;
import course.spring.hospitalinformationsystem.dto.LoginResponse;
import course.spring.hospitalinformationsystem.entity.User;
import course.spring.hospitalinformationsystem.service.UserService;
import course.spring.hospitalinformationsystem.web.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static course.spring.hospitalinformationsystem.utils.ErrorHandlingUtils.handleValidationErrors;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    @Autowired
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("login")
    public LoginResponse login(@Valid @RequestBody Credentials credentials, Errors errors) {
        handleValidationErrors(errors);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                credentials.getUsername(), credentials.getPassword()
        ));
        final User user = userService.getUserByUsername(credentials.getUsername());
        final String token = jwtUtils.generateToken(user);
        return new LoginResponse(token, Converter.getUserDto(user));
    }

}












