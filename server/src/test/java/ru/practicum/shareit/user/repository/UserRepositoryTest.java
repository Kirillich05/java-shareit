package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repoUnderTest;

    @AfterEach
    void tearDown() {
        repoUnderTest.deleteAll();
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
