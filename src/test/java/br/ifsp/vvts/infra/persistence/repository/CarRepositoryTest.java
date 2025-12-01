package br.ifsp.vvts.infra.persistence.repository;

import br.ifsp.vvts.domain.model.car.Car;
import br.ifsp.vvts.domain.model.car.LicensePlate;
import br.ifsp.vvts.infra.persistence.entity.car.CarEntity;
import br.ifsp.vvts.infra.persistence.entity.car.LicensePlateEmbeddable;
import br.ifsp.vvts.infra.persistence.entity.customer.CustomerEntity;
import br.ifsp.vvts.infra.persistence.mapper.CarMapper;
import br.ifsp.vvts.security.user.JpaUserRepository;
import br.ifsp.vvts.security.user.Role;
import br.ifsp.vvts.security.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CarRepositoryTest {

    @Autowired
    private CarRepository repository;
    @Autowired
    private CarMapper carMapper;

    @AfterEach
    void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return the car with provided license plate when it is present")
    public void shouldReturnCarWithTheProvidedLicensePlate() {
        LicensePlate licensePlate = LicensePlate.of("ABC1234");

        var car = new Car(
                licensePlate,
                "Chevrolet",
                "Chevette",
                300
        );
        var carEntity = carMapper.toEntity(car);

        repository.save(carEntity);
        var queryResult = repository.findByLicensePlate(licensePlate.value());

        assertThat(queryResult.get()).isEqualTo(carEntity);
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return an empty optional if the car does not exists on database")
    public void shouldReturnEmptyIfThereIsNoCarWithTheProvidedLicensePlate() {
        LicensePlate licensePlate = LicensePlate.of("ABC1234");

        var car = new Car(
                licensePlate,
                "Chevrolet",
                "Chevette",
                300
        );

        var carEntity = carMapper.toEntity(car);

        repository.save(carEntity);
        var queryResult = repository.findByLicensePlate("BRL1000");

        assertTrue(queryResult.isEmpty());
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should not allow creating two cars with the same license plate")
    public void shouldNotAllowDuplicatedLicensePlate() {
        var plate = LicensePlate.of("ABC1234");

        var car1 = new Car(
                plate,
                "Chevrolet",
                "Chevette",
                300
        );
        repository.save(carMapper.toEntity(car1));

        var car2 = new Car(
                plate,
                "Toyota",
                "Corolla",
                250
        );
        var entity2 = carMapper.toEntity(car2);

        assertThrows(JpaSystemException.class, () -> {
            repository.saveAndFlush(entity2);
        });
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should not allow to persist a customer with a null cpf")
    public void shouldNotAllowNullCpf() {
        var carEntity = new CarEntity(
                null,
                null,
                "Honda",
                "Accord",
                250);

        assertThrows(JpaSystemException.class, () -> {
            repository.save(carEntity);
        });
    }
}