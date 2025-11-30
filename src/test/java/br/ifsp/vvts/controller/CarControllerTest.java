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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class CarControllerTest extends BaseApiIntegrationTest {

    String token;
    User user;

    @Autowired
    CarRepository carRepository;

    @AfterEach
    void tearDown() {
        carRepository.deleteAll();
    }

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

    @Test
    @DisplayName("Should delete car and return 204 with no content")
    void shouldDeleteCarAndReturn204WithNoContent(){
        var createRequest = new CreateCarRequest("ABC1234","Toyota","Corolla",40000.0);
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(createRequest).post("/api/v1/cars");

        var licensePlate = createRequest.licensePlate();

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(licensePlate)
                .delete("/api/v1/cars/" + licensePlate)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);
    }

    @Test
    @DisplayName("Should return 200 and list of cars in database")
    void shouldReturn200AndAListOfCarsInDatabase() {

        var corolla = new CreateCarRequest("ABC1111","Toyota","Corolla",40000.0);
        var honda = new CreateCarRequest("ABC4321","Honda","Civic",45000.0);
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(corolla).post("/api/v1/cars");
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(honda).post("/api/v1/cars");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .get("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("[0].licensePlate", equalTo("ABC1111"))
                .body("[0].brand", equalTo("Toyota"))
                .body("[0].model", equalTo("Corolla"))
                .body("[0].basePrice", equalTo(40000.0F))

                .body("[1].licensePlate", equalTo("ABC4321"))
                .body("[1].brand", equalTo("Honda"))
                .body("[1].model", equalTo("Civic"))
                .body("[1].basePrice", equalTo(45000.0F));
    }

    @Test
    @DisplayName("Should return 200 and a car using license plate in database")
    void shouldReturn200AndACarUsingLicensePlateInDatabase() {

        var corolla = new CreateCarRequest("ABC1111","Toyota","Corolla",40000.0);
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(corolla).post("/api/v1/cars");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .get("/api/v1/cars/" + corolla.licensePlate())
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("licensePlate", equalTo("ABC1111"))
                .body("brand", equalTo("Toyota"))
                .body("model", equalTo("Corolla"))
                .body("basePrice", equalTo(40000.0F));
    }
}
