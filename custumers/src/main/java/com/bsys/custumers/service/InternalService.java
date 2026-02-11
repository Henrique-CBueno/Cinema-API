package com.bsys.custumers.service;

import com.bsys.custumers.domain.dto.req.UpdateCustomerIdDTO;
import com.bsys.custumers.domain.entity.Customer;
import com.bsys.custumers.infra.constants.ExceptionsConstants;
import com.bsys.custumers.infra.exceptions.NotFoundException;
import com.bsys.custumers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternalService {

    private final MongoTemplate mongoTemplate;
    private final CustomerRepository customerRepository;

    public Customer findById(String customerId) {
        return customerRepository.findByKeycloakUserId(customerId).orElseThrow(
                () -> new NotFoundException(
                        String.format(
                                ExceptionsConstants.CUSTOMER_NOT_FOUND,
                                customerId
                        )
                )
        );
    }

    public void updateCustomerId(UpdateCustomerIdDTO dto, String userId) {

        if (dto.customerId() == null) throw new NotFoundException(String.format(
                ExceptionsConstants.CUSTOMER_NOT_FOUND
        ));

        Query query = new Query(Criteria.where("keycloakUserId").is(userId));

        Update update = new Update().set("customerId", dto.customerId());

        mongoTemplate.updateFirst(query, update, Customer.class);
    }
}
