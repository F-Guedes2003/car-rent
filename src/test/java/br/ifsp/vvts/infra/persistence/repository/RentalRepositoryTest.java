package br.ifsp.vvts.infra.persistence.repository;

import br.ifsp.vvts.domain.model.customer.CPF;
import br.ifsp.vvts.domain.model.customer.Customer;
import br.ifsp.vvts.domain.model.car.Car;
import br.ifsp.vvts.domain.model.car.LicensePlate;
import br.ifsp.vvts.domain.model.rental.RentalStatus;
import br.ifsp.vvts.infra.persistence.entity.car.CarEntity;
import br.ifsp.vvts.infra.persistence.entity.car.LicensePlateEmbeddable;
import br.ifsp.vvts.infra.persistence.entity.customer.CustomerEntity;
import br.ifsp.vvts.infra.persistence.entity.rental.RentalEntity;
import br.ifsp.vvts.infra.persistence.mapper.CarMapper;
import br.ifsp.vvts.infra.persistence.mapper.CustomerMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CarMapper carMapper;

    @AfterEach
    void cleanUp() {
        rentalRepository.deleteAll();
        customerRepository.deleteAll();
        carRepository.deleteAll();
    }

    private CustomerEntity createCustomer() {
        Customer domain = new Customer("John Doe", CPF.of("12345678909"));
        return customerRepository.save(customerMapper.toEntity(domain));
    }

    private CarEntity createCar(String plate) {
        Car domain = new Car(LicensePlate.of(plate), "Model X", "Tesla", 200);
        return carRepository.save(carMapper.toEntity(domain));
    }

    private RentalEntity createRental(CarEntity car,
                                      CustomerEntity customer,
                                      LocalDate start,
                                      LocalDate end,
                                      RentalStatus status) {

        RentalEntity rental = new RentalEntity(
                null,
                customer,
                car,
                start,
                end,
                new BigDecimal("100.00"),
                status
        );

        return rentalRepository.save(rental);
    }

    @Test
    @DisplayName("licence plate existent, active and between the overlap should return true")
    void EveryConditionsAreTrue() {

        var customer = createCustomer();
        var car = createCar("ABC1234");

        createRental(car, customer,
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 20),
                RentalStatus.ACTIVE);

        boolean result = rentalRepository
                .existsByCarLicensePlateAndPeriodOverlaps(
                        new LicensePlateEmbeddable("ABC1234"),
                        LocalDate.of(2024, 1, 12),
                        LocalDate.of(2024, 1, 18)
                );

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("different license plate should return false(the rest of the conditions is right)")
    void LicensePlateIsNotPresent() {

        var customer = createCustomer();
        var car = createCar("ABC1234");

        createRental(car, customer,
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 20),
                RentalStatus.ACTIVE);

        boolean result = rentalRepository
                .existsByCarLicensePlateAndPeriodOverlaps(
                        new LicensePlateEmbeddable("ZZZ9999"), // diferente
                        LocalDate.of(2024, 1, 12),
                        LocalDate.of(2024, 1, 18)
                );

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Status is inactive should return false(everything else is true)")
    void inactiveStatusShouldReturnFalse() {

        var customer = createCustomer();
        var car = createCar("ABC1234");

        createRental(car, customer,
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 20),
                RentalStatus.FINISHED
        );

        boolean result = rentalRepository
                .existsByCarLicensePlateAndPeriodOverlaps(
                        new LicensePlateEmbeddable("ABC1234"),
                        LocalDate.of(2024, 1, 12),
                        LocalDate.of(2024, 1, 18)
                );

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("No rentals on the provided overlap should return false")
    void NoRentalsOnOverlapShouldReturnFalse() {

        var customer = createCustomer();
        var car = createCar("ABC1234");

        createRental(car, customer,
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 20),
                RentalStatus.ACTIVE);

        boolean result = rentalRepository
                .existsByCarLicensePlateAndPeriodOverlaps(
                        new LicensePlateEmbeddable("ABC1234"),
                        LocalDate.of(2024, 1, 21), // começa depois
                        LocalDate.of(2024, 1, 25)
                );

        assertThat(result).isFalse();
    }
}
