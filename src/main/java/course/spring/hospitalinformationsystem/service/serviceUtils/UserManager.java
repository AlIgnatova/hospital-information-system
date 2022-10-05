package course.spring.hospitalinformationsystem.service.serviceUtils;

import course.spring.hospitalinformationsystem.entity.User;
import course.spring.hospitalinformationsystem.exception.UserNotLogged;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class UserManager {

    public static User getLoggedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return  (User) principal;
            }
        }
        throw new UserNotLogged("There is no logged user.");
    }
}
