package br.ifsp.vvts.controller;

import br.ifsp.vvts.domain.dto.CreateCarRequest;
import br.ifsp.vvts.domain.dto.CreateCustomerRequest;
import br.ifsp.vvts.domain.dto.UpdateCustomerRequest;
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
        .when().put("/api/v1/customers/" + request.cpf())
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("name", equalTo("Aislan Pepi"))
                .body("cpf", equalTo("514.302.036-09"));
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should delete customer and return 204 with no content")
    void shouldDeleteCustomerAndReturn204WithNoContent(){
        var request = new CreateCustomerRequest("Aislan","51430203609");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .post("/api/v1/customers");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
        .when().delete("/api/v1/customers/" + request.cpf())
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 200 and list of customers in database")
    void shouldReturn200AndAListOfCustomersInDatabase() {
        var request = new CreateCustomerRequest("Aislan","51430203609");
        var request2 = new CreateCustomerRequest("Fhelippe","76540166460");
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(request).post("/api/v1/customers");
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(request2).post("/api/v1/customers");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .get("/api/v1/customers")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("[0].name", equalTo("Aislan"))
                .body("[0].cpf", equalTo("514.302.036-09"))
                .body("[1].name", equalTo("Fhelippe"))
                .body("[1].cpf", equalTo("765.401.664-60"));
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 200 and a customer using cpf")
    void shouldReturn200AndACustomerUsingCpf() {

        var request = new CreateCustomerRequest("Aislan","51430203609");
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(request).post("/api/v1/customers");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .get("/api/v1/customers/" + request.cpf())
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("name", equalTo("Aislan"))
                .body("cpf", equalTo("514.302.036-09"));
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if the user tries to create a customer and is not authenticated")
    void shouldReturn401IfUserTriesToCreateCustomerAndIsNotAuthenticated() {

        var request = new CreateCustomerRequest("Aislan", "51430203609");

        given()
                .contentType("application/json")
                .port(port)
                .body(request)
                .when().post("/api/v1/customers")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }
}
