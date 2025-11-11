package com.boardcamp.api.unit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.exceptions.ConflictException; 
import com.boardcamp.api.exceptions.NotFoundException; 
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.services.CustomerService;

@SpringBootTest
class CustomerUnitTests {

    @InjectMocks
    CustomerService customerService;

    @Mock
    CustomerRepository customerRepository;

    CustomerDTO customerDTO;
    CustomerModel customerModel;

    @BeforeEach
void setup() {
    customerDTO = new CustomerDTO("João Alfredo", "21998899222", "01234567890");
    
    customerModel = new CustomerModel(); 
    customerModel.setName(customerDTO.getName());
    customerModel.setPhone(customerDTO.getPhone());
    customerModel.setCpf(customerDTO.getCpf());
    customerModel.setId(1L);
}

    @Test
    void givenValidCustomer_whenCreatingCustomer_thenReturnsCustomer() {
        doReturn(Optional.empty()).when(customerRepository).findByCpf(customerDTO.getCpf());
        doReturn(customerModel).when(customerRepository).save(any());

        CustomerModel result = customerService.createCustomer(customerDTO);

        assertNotNull(result);
        assertEquals(customerDTO.getName(), result.getName());
        verify(customerRepository, times(1)).findByCpf(anyString());
        verify(customerRepository, times(1)).save(any(CustomerModel.class));
    }

    @Test
    void givenExistingCpf_whenCreatingCustomer_thenThrowsConflictException() {
        doReturn(Optional.of(customerModel)).when(customerRepository).findByCpf(customerDTO.getCpf());

        assertThrows(ConflictException.class, () -> {
            customerService.createCustomer(customerDTO);
        });
        
        verify(customerRepository, times(1)).findByCpf(anyString());
        verify(customerRepository, times(0)).save(any(CustomerModel.class));
    }

    @Test
    void givenNonExistingId_whenFindingCustomerById_thenThrowsNotFoundException() {
        doReturn(Optional.empty()).when(customerRepository).findById(1L);

        assertThrows(NotFoundException.class, () -> {
            customerService.getCustomerById(1L);
        });
        
        verify(customerRepository, times(1)).findById(anyLong());
    }

    @Test
    void whenGettingAllCustomers_thenReturnsCustomerList() {
        doReturn(List.of(customerModel))
            .when(customerRepository).findAll();

        List<CustomerModel> result = customerService.getAllCustomers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findAll();
    }
}