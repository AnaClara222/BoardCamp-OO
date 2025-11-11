package com.boardcamp.api.controllers;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.services.CustomerService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerModel>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerModel> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerModel> createCustomer(@RequestBody CustomerDTO customerDTO) {
        CustomerModel newCustomer = customerService.createCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
    }
}
