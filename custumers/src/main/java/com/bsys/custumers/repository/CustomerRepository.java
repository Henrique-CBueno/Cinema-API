package com.bsys.custumers.repository;

import com.bsys.custumers.domain.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findByKeycloakUserId(String keycloakUserId);
}
