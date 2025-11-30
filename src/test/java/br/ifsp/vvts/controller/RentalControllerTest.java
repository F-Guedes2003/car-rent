package br.ifsp.vvts.controller;

import br.ifsp.vvts.domain.dto.CreateCarRequest;
import br.ifsp.vvts.domain.dto.CreateCustomerRequest;
import br.ifsp.vvts.domain.dto.CreateRentalRequest;
import br.ifsp.vvts.infra.persistence.repository.RentalRepository;
import br.ifsp.vvts.security.user.User;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class RentalControllerTest extends BaseApiIntegrationTest {

    String token;
    User user;

    @Autowired
    RentalRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @BeforeEach
    void setup() {
        user = register("123password");
        token = authenticate(user.getEmail(), "123password");
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should register a rental and return 201 with rental object as payload")
    void shouldRegisterRentalAndReturn201WithRentalObjectAsPayload() {

        var car = new CreateCarRequest("ABC1234","Toyota","Corolla",40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(car)
                .post("/api/v1/cars");

        var customer = new CreateCustomerRequest("Aislan","51430203609");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(customer)
                .post("/api/v1/customers");

        var request = new CreateRentalRequest(car.licensePlate(),customer.cpf(), LocalDate.of(2025,1,1),LocalDate.of(2025,1,2),true);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when().post("/api/v1/rentals")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(201);
    }


}