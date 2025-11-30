package br.ifsp.vvts.infra.persistence.repository;

import br.ifsp.vvts.domain.model.car.Car;
import br.ifsp.vvts.domain.model.car.LicensePlate;
import br.ifsp.vvts.domain.model.customer.CPF;
import br.ifsp.vvts.domain.model.customer.Customer;
import br.ifsp.vvts.infra.persistence.entity.car.CarEntity;
import br.ifsp.vvts.infra.persistence.entity.car.LicensePlateEmbeddable;
import br.ifsp.vvts.infra.persistence.mapper.CarMapper;
import br.ifsp.vvts.infra.persistence.mapper.CustomerMapper;
import br.ifsp.vvts.security.user.JpaUserRepository;
import br.ifsp.vvts.security.user.Role;
import br.ifsp.vvts.security.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("Should return the customer of the provided CPF when it is present")
    public void shouldReturnCustomerWithTheProvidedCpf() {
        var cpf = CPF.of("12345678909");

        var customer = new Customer("Gustavo Gomes", cpf);
        var customerEntity = customerMapper.toEntity(customer);

        repository.save(customerEntity);
        var queryResult = repository.findByCpfNumber(cpf.unformat());

        assertThat(queryResult.get()).isEqualTo(customerEntity);
    }
}