package com.boardcamp.api.services;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    public List<CustomerModel> getAllCustomers() {
        return customerRepository.findAll();
    }

    public CustomerModel getCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    public CustomerModel createCustomer(CustomerDTO customerDTO) {
        if (customerDTO.getName() == null || customerDTO.getName().isBlank() ||
            customerDTO.getCpf() == null || customerDTO.getCpf().length() != 11 ||
            customerDTO.getPhone() == null || (customerDTO.getPhone().length() != 10 && customerDTO.getPhone().length() != 11)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos");
        }

        if (customerRepository.findByCpf(customerDTO.getCpf()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }

        CustomerModel customer = new CustomerModel();
        customer.setName(customerDTO.getName());
        customer.setCpf(customerDTO.getCpf());
        customer.setPhone(customerDTO.getPhone());

        return customerRepository.save(customer);
    }
}
