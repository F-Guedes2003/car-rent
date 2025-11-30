package br.ifsp.vvts.controller;

import br.ifsp.vvts.domain.dto.CreateCarRequest;
import br.ifsp.vvts.domain.dto.CreateCustomerRequest;
import br.ifsp.vvts.domain.dto.CreateRentalRequest;
import br.ifsp.vvts.domain.dto.UpdateCustomerRequest;
import br.ifsp.vvts.infra.persistence.repository.RentalRepository;
import br.ifsp.vvts.security.user.User;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;

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

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should delete a rental and return 204 with no content")
    void shouldDeleteRentalAndReturn204WithNoContent() {

        var car = new CreateCarRequest("ABC1234", "Toyota", "Corolla", 40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(car)
                .post("/api/v1/cars");

        var customer = new CreateCustomerRequest("Aislan", "51430203609");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(customer)
                .post("/api/v1/customers");

        var rental = new CreateRentalRequest(car.licensePlate(),customer.cpf(), LocalDate.of(2025,1,1),LocalDate.of(2025,1,2),true);

        long id = given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(rental)
        .when().post("/api/v1/rentals")
        .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
        .when().delete("/api/v1/rentals/" + id)
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should get all rentals and return 200")
    void shouldGetAllRentalsAndReturn200() {

        var car = new CreateCarRequest("ABC1234", "Toyota", "Corolla", 40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(car)
                .post("/api/v1/cars");

        var customer = new CreateCustomerRequest("Aislan", "51430203609");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(customer)
                .post("/api/v1/customers");

        var rental = new CreateRentalRequest(car.licensePlate(),customer.cpf(), LocalDate.of(2025,1,1),LocalDate.of(2025,1,2),true);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(rental)
        .when().post("/api/v1/rentals");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
        .when().get("/api/v1/rentals")
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should get one rental with id and return 200")
    void shouldGetOneRentalWithIdAndReturn200() {

        var car = new CreateCarRequest("ABC1234", "Toyota", "Corolla", 40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(car)
                .post("/api/v1/cars");

        var customer = new CreateCustomerRequest("Aislan", "51430203609");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(customer)
                .post("/api/v1/customers");

        var rental = new CreateRentalRequest(car.licensePlate(),customer.cpf(), LocalDate.of(2025,1,1),LocalDate.of(2025,1,2),true);

        long id = given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(rental)
                .when().post("/api/v1/rentals")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when().get("/api/v1/rentals/" + id)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if unauthorized user tries to register rental")
    void shouldReturn401IfUnauthorizedUserTriesToRegisterRental() {

        var car = new CreateCarRequest("ABC1234","Toyota","Corolla",40000.0);

        given()
                .contentType("application/json")
                .port(port)
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
                .statusCode(401);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if unauthorized tries to delete rental")
    void shouldReturn401IfUnauthorizedUserTriesToDeleteRental() {

        var car = new CreateCarRequest("ABC1234", "Toyota", "Corolla", 40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(car)
                .post("/api/v1/cars");

        var customer = new CreateCustomerRequest("Aislan", "51430203609");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(customer)
                .post("/api/v1/customers");

        var rental = new CreateRentalRequest(car.licensePlate(),customer.cpf(), LocalDate.of(2025,1,1),LocalDate.of(2025,1,2),true);

        long id = given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(rental)
                .when().post("/api/v1/rentals")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        given()
                .contentType("application/json")
                .port(port)
                .when().delete("/api/v1/rentals/" + id)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }
}