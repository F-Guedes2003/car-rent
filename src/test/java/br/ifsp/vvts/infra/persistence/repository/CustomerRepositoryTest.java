package br.ifsp.vvts.infra.persistence.repository;

import br.ifsp.vvts.domain.model.car.Car;
import br.ifsp.vvts.domain.model.car.LicensePlate;
import br.ifsp.vvts.domain.model.customer.CPF;
import br.ifsp.vvts.domain.model.customer.Customer;
import br.ifsp.vvts.infra.persistence.entity.car.CarEntity;
import br.ifsp.vvts.infra.persistence.entity.car.LicensePlateEmbeddable;
import br.ifsp.vvts.infra.persistence.entity.customer.CustomerEntity;
import br.ifsp.vvts.infra.persistence.mapper.CarMapper;
import br.ifsp.vvts.infra.persistence.mapper.CustomerMapper;
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
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;
    @Autowired
    private CustomerMapper customerMapper;

    @AfterEach
    void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return the customer of the provided CPF when it is present")
    public void shouldReturnCustomerWithTheProvidedCpf() {
        var cpf = CPF.of("12345678909");

        var customer = new Customer("Gustavo Gomes", cpf);
        var customerEntity = customerMapper.toEntity(customer);

        repository.save(customerEntity);
        var queryResult = repository.findByCpfNumber(cpf.unformat());

        assertThat(queryResult.get()).isEqualTo(customerEntity);
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return an empty optional of the provided CPF when it is present")
    public void shouldReturnAnEmptyOptional() {
        var cpf = CPF.of("12345678909");

        var customer = new Customer("Gustavo Gomes", cpf);
        var customerEntity = customerMapper.toEntity(customer);

        repository.save(customerEntity);
        var queryResult = repository.findByCpfNumber("12345678900");

        assertTrue(queryResult.isEmpty());
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should not allow creating users with the same cpf")
    public void shouldNotAllowDuplicatedCpf() {
        var cpf = CPF.of("12345678909");

        var customer1 = new Customer("Gustavo Gomes", cpf);
        var entity1 = customerMapper.toEntity(customer1);
        repository.save(entity1);

        var customer2 = new Customer("Marcos Silva", cpf);
        var entity2 = customerMapper.toEntity(customer2);

        assertThrows(JpaSystemException.class, () -> {
            repository.saveAndFlush(entity2);
        });
    }

    @Test
    @Tag("PersistenceTest")
    @Tag("IntegrationTest")
    @DisplayName("Should not allow to persist a customer with a null cpf")
    public void shouldNotAllowNullCpf() {
        var customerEntity = new CustomerEntity(null, "John Doe", null);

        assertThrows(JpaSystemException.class, () -> {
            repository.save(customerEntity);
        });
    }
}