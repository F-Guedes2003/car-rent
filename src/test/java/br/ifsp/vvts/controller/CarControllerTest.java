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
    CreateCarRequest car;

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
        car = createCar("ABC1235","Toyota","Corolla",40000.0);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should register a car and return 201 with car object as payload")
    void shouldRegisterCarAndReturn201WithCarObjectAsPayload() {

        var newCar = new CreateCarRequest("ABC4321","Honda","Civic",45000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(newCar)
                .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(201)
                .body("licensePlate", equalTo(newCar.licensePlate()))
                .body("brand", equalTo(newCar.brand()))
                .body("model", equalTo(newCar.model()))
                .body("basePrice", equalTo(45000.0F));
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should update a car and return 200 with updated values")
    void shouldUpdateCarAndReturn200WithCarObjectAsPayload() {

        var updateRequest = new UpdateCarRequest("Toyota","Corolla",45000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(updateRequest)
        .when().put("/api/v1/cars/" + car.licensePlate())
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("licensePlate", equalTo(car.licensePlate()))
                .body("brand", equalTo(car.brand()))
                .body("model", equalTo(car.model()))
                .body("basePrice", equalTo(45000.0F));
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should delete car and return 204 with no content")
    void shouldDeleteCarAndReturn204WithNoContent(){

        var licensePlate = car.licensePlate();

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(licensePlate)
        .when().delete("/api/v1/cars/" + licensePlate)
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 200 and list of cars in database")
    void shouldReturn200AndAListOfCarsInDatabase() {

        var honda = createCar("ABC4321","Honda","Civic",45000.0);
        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token).body(honda).post("/api/v1/cars");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
        .when().get("/api/v1/cars")
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("[0].licensePlate", equalTo(car.licensePlate()))
                .body("[0].brand", equalTo(car.brand()))
                .body("[0].model", equalTo(car.model()))
                .body("[0].basePrice", equalTo(40000.0F))

                .body("[1].licensePlate", equalTo(honda.licensePlate()))
                .body("[1].brand", equalTo(honda.brand()))
                .body("[1].model", equalTo(honda.model()))
                .body("[1].basePrice", equalTo(45000.0F));
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 200 and a car using license plate in database")
    void shouldReturn200AndACarUsingLicensePlateInDatabase() {

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
        .when().get("/api/v1/cars/" + car.licensePlate())
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("licensePlate", equalTo(car.licensePlate()))
                .body("brand", equalTo(car.brand()))
                .body("model", equalTo(car.model()))
                .body("basePrice", equalTo(40000.0F));
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if the user tries to create a car and is not authenticated")
    void shouldReturn401IfUserTriesToCreateCarAndIsNotAuthenticated() {


        given()
                .contentType("application/json")
                .port(port)
                .body(car)
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

        var licensePlate = car.licensePlate();

        given()
                .contentType("application/json")
                .port(port)
                .body(licensePlate)
        .when().delete("/api/v1/cars/" + licensePlate)
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if the user tries to update a car and is not authenticated")
    void shouldReturn401IfUserTriesToUpdateCarAndIsNotAuthenticated() {

        var updateRequest = new UpdateCarRequest("Toyota","Corolla",45000.0);

        given()
                .contentType("application/json")
                .port(port).body(updateRequest)
        .when().put("/api/v1/cars/" + car.licensePlate())
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if the user tries to list all car and is not authenticated")
    void shouldReturn401IfUserTriesToListAllCarsAndIsNotAuthenticated() {

        createCar("ABC1111","Toyota","Corolla",40000.0);
        createCar("ABC4321","Honda","Civic",45000.0);

        given()
                .contentType("application/json")
                .port(port)
        .when().get("/api/v1/cars")
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 401 if the user tries to get a car from database and is not authenticated")
    void shouldReturn401IfUserTriesToGetCarAndIsNotAuthenticated() {

        given()
                .contentType("application/json")
                .port(port)
        .when().get("/api/v1/cars/" + car.licensePlate())
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 409 if user tries to register a car that already is registered")
    void shouldReturn409IfUserTriesToRegisterCarThatAlreadyIsRegistered(){
        var dupRequest = createCar("ABC1235","Toyota","Corolla",40000.0);

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
        .when().put("/api/v1/cars/ABC1234")
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 404 if user tries to delete a inexistent car")
    void shouldReturn404IfUserTriesToDeleteInexistentCar(){

        var inexistentLicense = "ABC1111";

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
        .when().delete("/api/v1/cars/" + inexistentLicense)
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 400 if license plate is invalid")
    void shouldReturn400IfLicensePlateIsInvalid() {

        var invalidCar = createCar("ABC123521","Toyota","Corolla",45000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(invalidCar)
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
        var inexistentLicense = "ABC1111";

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
        .when().get("/api/v1/cars/" + inexistentLicense)
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

        var negativeCar = createCar("ABC1235","Toyota","Corolla",basePrice);


        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(negativeCar)
        .when().post("/api/v1/cars")
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 400 Bad Request if brand is null")
    void shouldReturn400BadRequestIfBrandIsNull() {

        var nullCar = createCar("ABC1235",null,"Coroalla",45000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(nullCar)
        .when().post("/api/v1/cars")
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400);
    }

    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @Test
    @DisplayName("Should return 400 Bad Request if model is null")
    void shouldReturn400BadRequestIfModelIsNull() {

        var nullCar = createCar("ABC1235","Toyota",null,45000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(nullCar)
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

        var emptyBrand = createCar("ABC1235","","Corolla",45000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(emptyBrand)
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

        var emptyModel = createCar("ABC1235","Toyota","",45000.0);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(emptyModel)
        .when().post("/api/v1/cars")
        .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400);
    }

    private CreateCarRequest createCar(String licensePlate, String brand, String model, double basePrice){
        var car = new CreateCarRequest(licensePlate,brand,model,basePrice);
        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(car)
                .when().post("/api/v1/cars");
        return car;
    }
}
