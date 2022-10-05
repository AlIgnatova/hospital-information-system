package course.spring.hospitalinformationsystem.init;

import course.spring.hospitalinformationsystem.entity.User;
import course.spring.hospitalinformationsystem.entity.enums.Role;
import course.spring.hospitalinformationsystem.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!test")
public class DataInitializer implements ApplicationRunner {
    private static final User DEFAULT_ADMIN = new User("Default", "Default", "parolataNaAdmin#123",
             Role.ADMINISTRATOR, "admin.default@gmail.com" );

    private UserService userService;

    @Autowired
    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override

    public void run(ApplicationArguments args) throws Exception {
        if(userService.count() == 0) {
            userService.addUser(DEFAULT_ADMIN);
            log.info("Sample user created: {}", DEFAULT_ADMIN);
        }
    }
}
