package br.ifsp.vvts.controller;

import br.ifsp.vvts.domain.dto.CreateCarRequest;
import br.ifsp.vvts.domain.dto.UpdateCarRequest;
import br.ifsp.vvts.infra.persistence.repository.CarRepository;
import br.ifsp.vvts.security.user.User;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Tag("ApiTest")
    @Tag("IntegrationTest")
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

    @Tag("ApiTest")
    @Tag("IntegrationTest")
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
                .put("/api/v1/cars/" + createRequest.licensePlate())
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("licensePlate", equalTo("ABC1234"))
                .body("brand", equalTo("Toyota"))
                .body("model", equalTo("Corolla"))
                .body("basePrice", equalTo(45000.0F));
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
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

    @Tag("ApiTest")
    @Tag("IntegrationTest")
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

    @Tag("ApiTest")
    @Tag("IntegrationTest")
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

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if the user tries to create a car and is not authenticated")
    void shouldReturn401IfUserTriesToCreateCarAndIsNotAuthenticated() {

        var request = new CreateCarRequest(
                "ABC1235","Toyota","Corolla",40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .body(request)
                .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if the user tries to delete a car and is not authenticated")
    void shouldReturn401IfUserTriesToDeleteCarAndIsNotAuthenticated(){
        var createRequest = new CreateCarRequest("ABC1234","Toyota","Corolla",40000.0);
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(createRequest).post("/api/v1/cars");

        var licensePlate = createRequest.licensePlate();

        given()
                .contentType("application/json")
                .port(port)
                .body(licensePlate)
                .delete("/api/v1/cars/" + licensePlate)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if the user tries to update a car and is not authenticated")
    void shouldReturn401IfUserTriesToUpdateCarAndIsNotAuthenticated() {

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
                .port(port).body(updateRequest)
                .put("/api/v1/cars/" + createRequest.licensePlate())
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if the user tries to list all car and is not authenticated")
    void shouldReturn401IfUserTriesToListAllCarsAndIsNotAuthenticated() {

        var corolla = new CreateCarRequest("ABC1111","Toyota","Corolla",40000.0);
        var honda = new CreateCarRequest("ABC4321","Honda","Civic",45000.0);
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(corolla).post("/api/v1/cars");
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(honda).post("/api/v1/cars");

        given()
                .contentType("application/json")
                .port(port)
                .get("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if the user tries to get a car from database and is not authenticated")
    void shouldReturn401IfUserTriesToGetCarAndIsNotAuthenticated() {

        var corolla = new CreateCarRequest("ABC1111","Toyota","Corolla",40000.0);
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(corolla).post("/api/v1/cars");

        given()
                .contentType("application/json")
                .port(port)
                .get("/api/v1/cars/" + corolla.licensePlate())
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 409 if user tries to register a car that already is registered")
    void shouldReturn409IfUserTriesToRegisterCarThatAlreadyIsRegistered(){

        var request = new CreateCarRequest(
                "ABC1235","Toyota","Corolla",40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .post("/api/v1/cars");

        var dupRequest = new CreateCarRequest(
                "ABC1235","Toyota","Corolla",40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(dupRequest)
                .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(409);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 404 if user tries to update a inexistent car")
    void shouldReturn404IfUserTriesToUpdateInexistentCar() {

        var updateRequest = new UpdateCarRequest("Toyota","Corolla",45000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(updateRequest)
                .put("/api/v1/cars/ABC1234")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 404 if user tries to delete a inexistent car")
    void shouldReturn404IfUserTriesToDeleteInexistentCar(){
        var licensePlate = "ABC1234";

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(licensePlate)
                .delete("/api/v1/cars/" + licensePlate)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 400 if license plate is invalid")
    void shouldReturn400IfLicensePlateIsInvalid() {

        var request = new CreateCarRequest(
                "ABC1235132","Toyota","Corolla",40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 404 if the user tries to get a inexistent car")
    void shouldReturn404IfUserTriesToGetInexistentCar() {

        var corolla = new CreateCarRequest("ABC1111","Toyota","Corolla",40000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
        .when().get("/api/v1/cars/" + corolla.licensePlate())
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @ParameterizedTest
    @ValueSource(doubles = {-4000.0,0})
    @DisplayName("Should return 400 Bad Request if Base Price is Negative")
    void shouldReturn400BadRequestIfPriceIsNegativeOrZero(double basePrice) {

        var request = new CreateCarRequest(
                "ABC1235","Toyota","Corolla",basePrice);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 400 Bad Request if brand is null")
    void shouldReturn400BadRequestIfBrandIsNull
            () {

        var request = new CreateCarRequest(
                "ABC1235",null,"Corolla",4000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 400 Bad Request if model is null")
    void shouldReturn400BadRequestIfModelIsNull
            () {

        var request = new CreateCarRequest(
                "ABC1235","Toyota",null,4000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 400 Bad Request if brand is empty")
    void shouldReturn400BadRequestIfBrandIsEmpty() {

        var request = new CreateCarRequest(
                "ABC1235","","Corolla",4000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 400 Bad Request if model is null")
    void shouldReturn400BadRequestIfModelIsEmpty() {

        var request = new CreateCarRequest(
                "ABC1235","Toyota","",4000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400);
    }
}
