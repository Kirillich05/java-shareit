package ru.practicum.shareit.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class UserRepositoryTest {

    private Validator validator;

    @Autowired
    private UserRepository repoUnderTest;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void tearDown() {
        repoUnderTest.deleteAll();
    }

    @Test
    void testUserInvalidEmail() {
        User user = new User();
        user.setName("John");
        user.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testFindUserWhenEmailExists() {
        String email = "john@gmailss.com";
        User user1 = new User();
        user1.setName("John");
        user1.setEmail(email);

        repoUnderTest.save(user1);

        User expected = repoUnderTest.findUserByEmail(email);

        assertThat(expected).isEqualTo(user1);
    }

    @Test
    void testCheckWhenEmailDoesNotExists() {
        String email = "john@gmailss.com";

        boolean expected = repoUnderTest.selectExistsEmail(email);

        assertThat(expected).isFalse();
    }

    @Test
    void testCreateUser() {
        User user1 = new User();
        user1.setName("John");
        user1.setEmail("john@gmailss.com");

        repoUnderTest.save(user1);

        User expected = repoUnderTest.findUserByEmail("john@gmailss.com");
        assertThat(expected).isEqualTo(user1);
    }
}
