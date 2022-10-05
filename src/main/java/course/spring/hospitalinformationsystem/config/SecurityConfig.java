package course.spring.hospitalinformationsystem.config;

import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;
import course.spring.hospitalinformationsystem.service.UserService;
import course.spring.hospitalinformationsystem.web.jwt.JwtAuthenticationEntryPoint;
import course.spring.hospitalinformationsystem.web.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import static course.spring.hospitalinformationsystem.entity.enums.Role.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    JwtRequestFilter jwtRequestFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .mvcMatchers(POST, "/api/auth/login").permitAll()
                .mvcMatchers(GET, "/api/users", "api/users/**").authenticated()
                .mvcMatchers("/api/users", "api/users/**").hasRole(ADMINISTRATOR.name())
                .mvcMatchers(GET, "/api/hospitalstays", "/api/hospitalstays/**").hasAnyRole(DOCTOR.name(), NURSE.name(), ADMINISTRATOR.name())
                .mvcMatchers(POST, "/api/hospitalstays", "/api/hospitalstays/**").hasAnyRole(DOCTOR.name(), NURSE.name(), ADMINISTRATOR.name())
                .mvcMatchers(PUT, "/api/hospitalstays", "/api/hospitalstays/**").hasAnyRole(DOCTOR.name(), NURSE.name(), ADMINISTRATOR.name())
                .mvcMatchers(DELETE,"/api/hospitalstays", "/api/hospitalstays/**").hasRole(ADMINISTRATOR.name())
                .mvcMatchers(GET, "/api/wards", "/api/wards/**").hasAnyRole(DOCTOR.name(), NURSE.name(), ADMINISTRATOR.name())
                .mvcMatchers("/api/wards", "/api/wards/**").hasRole(ADMINISTRATOR.name())
                .mvcMatchers(GET, "/api/patients", "/api/patients/**").hasAnyRole(DOCTOR.name(), NURSE.name(), ADMINISTRATOR.name())
                .mvcMatchers(POST, "/api/patients", "/api/patients/**").hasAnyRole(DOCTOR.name(), NURSE.name(), ADMINISTRATOR.name())
                .mvcMatchers(PUT, "/api/patients", "/api/patients/**").hasAnyRole(DOCTOR.name(), NURSE.name(), ADMINISTRATOR.name())
                .mvcMatchers(DELETE,"/api/patients", "/api/patients/**").hasRole(ADMINISTRATOR.name())
                .mvcMatchers(POST, "/api/tests", "/api/tests/**").hasAnyRole(DOCTOR.name(), NURSE.name(), ADMINISTRATOR.name())
                .mvcMatchers(GET, "/api/tests", "/api/tests/**").hasAnyRole(DOCTOR.name(), NURSE.name(), ADMINISTRATOR.name())
                .mvcMatchers(PUT, "/api/tests", "/api/tests/**").hasAnyRole(DOCTOR.name(), ADMINISTRATOR.name())
                .mvcMatchers(DELETE,"/api/tests", "/api/tests/**").hasRole(ADMINISTRATOR.name())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, UserDetailsService userDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder())
                .and()
                .build();
    }

    @Bean
    public HttpFirewall getHttpFirewall() {
        StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
        strictHttpFirewall.setAllowSemicolon(true);
        return strictHttpFirewall;
    }


    @Bean
    UserDetailsService userDetailsService(UserService userService) {
        return (String username) -> {
            try {
                return userService.getUserByUsername(username);
            } catch (NonExistingEntityException ex) {
                throw new UsernameNotFoundException(ex.getMessage());
            }
        };
    }
}