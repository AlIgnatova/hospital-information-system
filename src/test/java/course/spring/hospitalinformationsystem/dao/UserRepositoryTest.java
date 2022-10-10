package course.spring.hospitalinformationsystem.dao;

import course.spring.hospitalinformationsystem.entity.User;
import course.spring.hospitalinformationsystem.entity.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource("/application-test.properties")
@Slf4j
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepo;

    @BeforeEach
    void setup() {
        userRepo.saveAll(DEFAULT_USERS);
    }

    @AfterEach
    void tearDown() {
        userRepo.deleteAll();
    }

    @Test
    void findAll() {
        List<User> actual = userRepo.findAll();
        assertThat(actual, hasSize(3));
        log.info("First user: {}", actual.get(0));
        assertThat(actual.get(0), samePropertyValuesAs(EXPECTED_USERS.get(0),
                "id", "created", "modified"));
    }

    @Test
    void findByEmailAddress() {
        Optional<User> actual = userRepo.findByEmailAddress(DEFAULT_USERS.get(2).getEmailAddress());
        assertTrue(actual.isPresent());
        assertThat(actual.get(), samePropertyValuesAs(EXPECTED_USERS.get(2),
                "id", "created", "modified"));
    }


    public static final List<User> DEFAULT_USERS = List.of(
            new User("Default", "Default", "parolataNaAdmin#123",
                    Role.ADMINISTRATOR, "admin.default@gmail.com"),
            new User("Default1", "Default1", "parolataNaAdmin#123",
                    Role.ADMINISTRATOR, "admin.default1@gmail.com"),
            new User("Default2", "Default2", "parolataNaAdmin#123",
                    Role.ADMINISTRATOR, "admin.default2@gmail.com")
    );

    public static final List<User> EXPECTED_USERS = List.of(
            new User("Default", "Default", "parolataNaAdmin#123",
                    Role.ADMINISTRATOR, "admin.default@gmail.com"),
            new User("Default1", "Default1", "parolataNaAdmin#123",
                    Role.ADMINISTRATOR, "admin.default1@gmail.com"),
            new User("Default2", "Default2", "parolataNaAdmin#123",
                    Role.ADMINISTRATOR, "admin.default2@gmail.com")
    );

}
