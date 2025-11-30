package br.ifsp.vvts.domain.security;

import br.ifsp.vvts.security.user.JpaUserRepository;
import br.ifsp.vvts.security.user.Role;
import br.ifsp.vvts.security.user.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository repository;

    @AfterEach
    void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should save and query user by email")
    void ShouldSaveAndQueryUser() {
        User user = new User(
                UUID.randomUUID(),
                "Teste",
                "User",
                "test@gmail.com",
                "Test123@3",
                Role.USER);

        user.setId(UUID.randomUUID());
        user.setName("Teste");
        user.setEmail("teste@teste.com");
        user.setPassword("123456");

        repository.save(user);

        Optional<User> found = repository.findByEmail("teste@teste.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Teste");
    }

    @Test
    @DisplayName("Should return an empty optional if there is no user with the provided email")
    void shouldReturnEmptyOptional() {
        User user = new User(
                UUID.randomUUID(),
                "Teste",
                "User",
                "test@gmail.com",
                "Test123@3",
                Role.USER);

        Optional<User> result = repository.findByEmail("anotheremail@gmail.com");
        assertTrue(result.isEmpty());
    }
}
