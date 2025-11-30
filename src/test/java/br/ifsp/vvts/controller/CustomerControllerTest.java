package br.ifsp.vvts.controller;

import br.ifsp.vvts.domain.dto.CreateCarRequest;
import br.ifsp.vvts.domain.dto.CreateCustomerRequest;
import br.ifsp.vvts.domain.dto.UpdateCarRequest;
import br.ifsp.vvts.domain.dto.UpdateCustomerRequest;
import br.ifsp.vvts.infra.persistence.repository.CarRepository;
import br.ifsp.vvts.infra.persistence.repository.CustomerRepository;
import br.ifsp.vvts.security.user.User;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CustomerControllerTest extends BaseApiIntegrationTest {

    String token;
    User user;

    @Autowired
    CustomerRepository repository;

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
    @DisplayName("Should register a customer and return 201 with customer object as payload")
    void shouldRegisterCustomerAndReturn201WithCustomerObjectAsPayload() {

        var request = new CreateCustomerRequest("Aislan","51430203609");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when().post("/api/v1/customers")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(201)
                .body("name", equalTo("Aislan"))
                .body("cpf", equalTo("514.302.036-09"));
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should update a car and return 200 with updated values")
    void shouldUpdateCarAndReturn200WithCarObjectAsPayload() {

        var request = new CreateCustomerRequest("Aislan","51430203609");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .post("/api/v1/customers");

        var updateRequest = new UpdateCustomerRequest("Aislan Pepi");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(updateRequest)
                .put("/api/v1/customers/" + request.cpf())
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("name", equalTo("Aislan Pepi"))
                .body("cpf", equalTo("514.302.036-09"));
    }
}
