package br.ifsp.vvts.security.auth;

import br.ifsp.vvts.security.user.JpaUserRepository;
import br.ifsp.vvts.security.user.Role;
import br.ifsp.vvts.security.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Register should return 201 when user is created")
    void registerShouldReturn201WhenUserIsCreated() throws Exception {

        RegisterUserRequest request = new RegisterUserRequest(
                "Vladimir",
                "Putinho",
                "vladimirputinho@gmail.com",
                "password"
        );

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/register")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Should return 409 when email already exists")
    void ShouldReturn409WhenEmailAlreadyExists() throws Exception {

        userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name("Olivio")
                .lastname("Palito")
                .email("oliviopalito@gmail.com")
                .password((passwordEncoder.encode("pass")))
                .role(Role.USER)
                .build()
        );

        RegisterUserRequest request = new RegisterUserRequest(
                "Olivio",
                "Palito",
                "oliviopalito@gmail.com",
                "pass"
        );

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/register")
                .then()
                .statusCode(409);
    }

    @Test
    @DisplayName("Authenticate should return 200 when credentials are valid")
    void authenticate_ShouldReturn200_WhenCredentialsAreValid() throws Exception {

        userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .lastname("User")
                .email("test@gmail.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .build()
        );

        AuthRequest request = new AuthRequest(
                "test@gmail.com",
                "password"
        );

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/authenticate")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    void ShouldReturn401WhenAuthenticationFails() throws Exception {

        AuthRequest request = new AuthRequest(
                "hamurabi@gmail.com",
                "wrongpass"
        );

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/v1/authenticate")
                .then()
                .statusCode(401);
    }
}
