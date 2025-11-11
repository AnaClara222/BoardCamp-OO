package com.boardcamp.api.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.exceptions.BadRequestException;
import com.boardcamp.api.exceptions.NotFoundException;
import com.boardcamp.api.exceptions.UnprocessableEntityException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;
import com.boardcamp.api.services.RentalService;

@SpringBootTest
class RentalUnitTests {

    @InjectMocks
    RentalService rentalService;

    @Mock
    RentalRepository rentalRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    GameRepository gameRepository;

    RentalDTO validRentalDTO;
    CustomerModel customer;
    GameModel game;
    RentalModel rental;

    @BeforeEach
    void setup() {
        
        validRentalDTO = new RentalDTO(1L, 1L, 5); 
        
        customer = new CustomerModel();
        customer.setId(1L);
        customer.setName("Test Customer");

        game = new GameModel();
        game.setId(1L);
        game.setPricePerDay(1000); 
        game.setStockTotal(3);

        rental = new RentalModel();
        rental.setId(1L);
        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setRentDate(LocalDate.now());
        rental.setDaysRented(5);
        rental.setOriginalPrice(5000L);
        rental.setReturnDate(null);
        rental.setDelayFee(0L);
    }

    @Test
    void givenValidRental_whenCreatingRental_thenReturnsRental() {
        doReturn(Optional.of(customer)).when(customerRepository).findById(anyLong());
        doReturn(Optional.of(game)).when(gameRepository).findById(anyLong());
        doReturn(0L).when(rentalRepository).countByGameIdAndReturnDateIsNull(anyLong());
        doReturn(rental).when(rentalRepository).save(any(RentalModel.class));

        RentalModel result = rentalService.createRental(validRentalDTO);

        assertNotNull(result);
        assertEquals(5000, result.getOriginalPrice());
        assertEquals(customer.getName(), result.getCustomer().getName());

        verify(customerRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).findById(1L);
        verify(rentalRepository, times(1)).save(any(RentalModel.class));
    }

    @Test
    void givenNonExistingCustomer_whenCreatingRental_thenThrowsNotFoundException() {
        doReturn(Optional.empty()).when(customerRepository).findById(anyLong());
        
        assertThrows(NotFoundException.class, () -> {
            rentalService.createRental(validRentalDTO);
        });

        verify(customerRepository, times(1)).findById(anyLong());
        verify(gameRepository, times(0)).findById(anyLong());
        verify(rentalRepository, times(0)).save(any());
    }

    @Test
    void givenNonExistingGame_whenCreatingRental_thenThrowsNotFoundException() {
        doReturn(Optional.of(customer)).when(customerRepository).findById(anyLong());
        doReturn(Optional.empty()).when(gameRepository).findById(anyLong());
        
        assertThrows(NotFoundException.class, () -> {
            rentalService.createRental(validRentalDTO);
        });

        verify(customerRepository, times(1)).findById(anyLong());
        verify(gameRepository, times(1)).findById(anyLong());
        verify(rentalRepository, times(0)).save(any());
    }

    @Test
    void givenDaysRentedIsZero_whenCreatingRental_thenThrowsBadRequestException() {
        RentalDTO invalidDTO = new RentalDTO(1L, 1L, 0); 
        
        assertThrows(BadRequestException.class, () -> {
            rentalService.createRental(invalidDTO);
        });
    }

    @Test
    void givenGameOutOfStock_whenCreatingRental_thenThrowsUnprocessableEntityException() {
        doReturn(Optional.of(customer)).when(customerRepository).findById(anyLong());
        doReturn(Optional.of(game)).when(gameRepository).findById(anyLong());
        
        doReturn(3L).when(rentalRepository).countByGameIdAndReturnDateIsNull(anyLong()); 
        
        assertThrows(UnprocessableEntityException.class, () -> {
            rentalService.createRental(validRentalDTO);
        });

        verify(rentalRepository, times(0)).save(any());
    }
    
    
    @Test
    void whenGettingAllRentals_thenReturnsRentalList() {
        doReturn(List.of(rental)).when(rentalRepository).findAll();

        List<RentalModel> result = rentalService.getAllRentals();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(rentalRepository, times(1)).findAll();
    }
}