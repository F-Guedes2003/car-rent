package br.ifsp.vvts.controller;

import br.ifsp.vvts.EntityBuilder;
import br.ifsp.vvts.domain.model.car.Car;
import br.ifsp.vvts.domain.model.car.LicensePlate;
import br.ifsp.vvts.domain.useCases.ManageCarUseCase;
import br.ifsp.vvts.infra.persistence.mapper.CarMapper;
import br.ifsp.vvts.infra.persistence.repository.CarRepository;
import br.ifsp.vvts.security.auth.AuthenticationInfoService;
import br.ifsp.vvts.security.user.User;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class CarControllerTest extends BaseApiIntegrationTest {

    @Test @DisplayName("Should register a car and return 201 with car object as payload")
    void shouldRegisterCarAndReturn201WithCarObjectAsPayload() {
        final User user = register("123password");
        final String token = authenticate(user.getEmail(), "123password");
        final Car car = new Car(LicensePlate.of("ABC1234"), "Toyota", "Corolla", 40000.0);

        given().contentType("application/json").port(port).header("Authorization", "Bearer " + token)
                .body(car)
        .when().post("/api/v1/cars")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(201)
                .body("licensePlate", equalTo("ABC1234"))
                .body("brand", equalTo("Toyota"))
                .body("model", equalTo("Corolla"))
                .body("basePrice", equalTo(40000.0F));
    }
}