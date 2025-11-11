package com.boardcamp.api.services;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.exceptions.NotFoundException; 
import com.boardcamp.api.exceptions.ConflictException; 
import com.boardcamp.api.exceptions.BadRequestException; 

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerModel> getAllCustomers() {
        return customerRepository.findAll();
    }

    public CustomerModel getCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public CustomerModel createCustomer(CustomerDTO customerDTO) {
        if (customerDTO.getName() == null || customerDTO.getName().isBlank() ||
             customerDTO.getCpf() == null || customerDTO.getCpf().length() != 11 ||
             customerDTO.getPhone() == null || (customerDTO.getPhone().length() != 10 && customerDTO.getPhone().length() != 11)) {
             throw new BadRequestException("Invalid customer data (name, cpf, or phone)."); 
        }

        Optional<CustomerModel> existingCustomer = customerRepository.findByCpf(customerDTO.getCpf());
        if (existingCustomer.isPresent()) {
            throw new ConflictException("CPF already registered"); 
        }

        CustomerModel customer = new CustomerModel();
        customer.setName(customerDTO.getName());
        customer.setCpf(customerDTO.getCpf());
        customer.setPhone(customerDTO.getPhone());

        return customerRepository.save(customer);
    }
}