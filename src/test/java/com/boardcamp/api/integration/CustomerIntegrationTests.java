package com.boardcamp.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc 
class CustomerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDTO validCustomerDTO;

    @BeforeEach
    void setup() {
        validCustomerDTO = new CustomerDTO("João Alfredo", "21998899222", "01234567890");
    }
    
    @AfterEach
    void cleanUp() {
        customerRepository.deleteAll();
    }

    private CustomerModel createCustomerModel(CustomerDTO dto) {
        CustomerModel model = new CustomerModel();
        model.setName(dto.getName());
        model.setPhone(dto.getPhone());
        model.setCpf(dto.getCpf());
        return model;
    }

    @Test
    void givenValidCustomer_whenCreatingCustomer_thenReturns201CreatedAndSavesToDB() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(validCustomerDTO);

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isCreated()) 
            .andExpect(jsonPath("$.cpf").value(validCustomerDTO.getCpf()))
            .andExpect(jsonPath("$.id").isNumber());

        assertEquals(1, customerRepository.count());
    }

    @Test
    void givenExistingCpf_whenCreatingCustomer_thenReturns409Conflict() throws Exception {
    
        CustomerModel existingCustomer = createCustomerModel(validCustomerDTO);
        customerRepository.save(existingCustomer); 
        
        String jsonBody = objectMapper.writeValueAsString(validCustomerDTO);

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isConflict()); 
        
        assertEquals(1, customerRepository.count());
    }

    @Test
    void givenInvalidCpf_whenCreatingCustomer_thenReturns400BadRequest() throws Exception {
        CustomerDTO invalidDTO = new CustomerDTO("Nome", "2199889922", "0123456789"); 
        String jsonBody = objectMapper.writeValueAsString(invalidDTO);

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest()); 
        
        assertEquals(0, customerRepository.count());
    }
    
    @Test
    void givenExistingId_whenFindingCustomerById_thenReturns200AndCustomer() throws Exception {
        CustomerModel customerToSave = createCustomerModel(validCustomerDTO);
        CustomerModel savedCustomer = customerRepository.save(customerToSave);
        Long customerId = savedCustomer.getId();

        mockMvc.perform(get("/customers/{id}", customerId))
            .andExpect(status().isOk()) 
            .andExpect(jsonPath("$.cpf").value(validCustomerDTO.getCpf()));
    }

    @Test
    void givenNonExistingId_whenFindingCustomerById_thenReturns404NotFound() throws Exception {
        Long nonExistingId = 99L;

        mockMvc.perform(get("/customers/{id}", nonExistingId))
            .andExpect(status().isNotFound()); 
    }
}