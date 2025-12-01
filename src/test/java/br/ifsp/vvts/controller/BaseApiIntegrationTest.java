package br.ifsp.vvts.controller;

import br.ifsp.vvts.EntityBuilder;
import br.ifsp.vvts.security.auth.AuthRequest;
import br.ifsp.vvts.security.auth.AuthResponse;
import br.ifsp.vvts.security.user.JpaUserRepository;
import br.ifsp.vvts.security.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static io.restassured.RestAssured.baseURI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseApiIntegrationTest {
    @LocalServerPort protected int port = 8080;
    @Autowired private JpaUserRepository repository;

    @BeforeEach public void generalSetup(){
        baseURI = "http://localhost:8080";
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    protected User register(String password) {
        final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = EntityBuilder.createRandomUser(password);
        user.setPassword(encoder.encode(password));
        repository.save(user);
        return user;
    }

    protected String  authenticate(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        AuthRequest authRequest = new AuthRequest(username, password);
        final AuthResponse response = restTemplate.postForObject(baseURI + "/api/v1/authenticate", authRequest, AuthResponse.class);
        return Objects.requireNonNull(response).token();
    }
}
