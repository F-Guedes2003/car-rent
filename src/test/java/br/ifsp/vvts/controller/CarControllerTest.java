package br.ifsp.vvts.controller;

import br.ifsp.vvts.domain.dto.CreateCarRequest;
import br.ifsp.vvts.domain.dto.UpdateCarRequest;
import br.ifsp.vvts.domain.model.car.Car;
import br.ifsp.vvts.domain.model.car.LicensePlate;
import br.ifsp.vvts.domain.useCases.ManageCarUseCase;
import br.ifsp.vvts.infra.persistence.entity.car.CarEntity;
import br.ifsp.vvts.infra.persistence.entity.car.LicensePlateEmbeddable;
import br.ifsp.vvts.infra.persistence.repository.CarRepository;
import br.ifsp.vvts.security.auth.AuthenticationInfoService;
import br.ifsp.vvts.security.user.User;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CarControllerTest extends BaseApiIntegrationTest {

    String token;
    User user;

    @BeforeEach
    void setup() {
        user = register("123password");
        token = authenticate(user.getEmail(), "123password");
    }

    @Test
    @DisplayName("Should register a car and return 201 with car object as payload")
    void shouldRegisterCarAndReturn201WithCarObjectAsPayload() {

        var request = new CreateCarRequest(
                "ABC1235","Toyota","Corolla",40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(201)
                .body("licensePlate", equalTo("ABC1235"))
                .body("brand", equalTo("Toyota"))
                .body("model", equalTo("Corolla"))
                .body("basePrice", equalTo(40000.0F));
    }

    @Test
    @DisplayName("Should update a car and return 200 with updated values")
    void shouldUpdateCarAndReturn200WithCarObjectAsPayload() {

        var createRequest = new CreateCarRequest("ABC1234","Toyota","Corolla",40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(createRequest)
                .post("/api/v1/cars");

        var updateRequest = new UpdateCarRequest("Toyota","Corolla",45000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(updateRequest)
                .put("/api/v1/cars/ABC1234")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("licensePlate", equalTo("ABC1234"))
                .body("brand", equalTo("Toyota"))
                .body("model", equalTo("Corolla"))
                .body("basePrice", equalTo(45000.0F));
    }
}
