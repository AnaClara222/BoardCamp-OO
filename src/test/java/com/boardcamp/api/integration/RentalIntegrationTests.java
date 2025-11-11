package com.boardcamp.api.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;

@SpringBootTest
@AutoConfigureMockMvc
class RentalIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RentalRepository rentalRepository;

    private CustomerModel customer;
    private GameModel game;

    @BeforeEach
    void setup() {

        rentalRepository.deleteAll();
        customerRepository.deleteAll();
        gameRepository.deleteAll();

        customer = new CustomerModel();
        customer.setName("Integration Customer");
        customer.setCpf("12345678900");
        customer.setPhone("99999999999");
        customer = customerRepository.save(customer);

        game = new GameModel();
        game.setName("Integration Game");
        game.setImage("http://test.com/image.jpg");
        game.setStockTotal(1);

        game.setPricePerDay(1000);
        game = gameRepository.save(game);
    }

    @AfterEach
    void tearDown() {
        rentalRepository.deleteAll();
        customerRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    void givenValidRentalData_whenPostingRental_thenReturnCreatedAndRentalDetails() throws Exception {
        Long customerId = customer.getId();
        Long gameId = game.getId();
        int daysRented = 5;

        String body = String.format("{\"customerId\": %d, \"gameId\": %d, \"daysRented\": %d}",
                customerId, gameId, daysRented);

        mockMvc.perform(post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customer.id").value(customerId))
                .andExpect(jsonPath("$.game.id").value(gameId))
                .andExpect(jsonPath("$.daysRented").value(daysRented))
                .andExpect(jsonPath("$.originalPrice").value(5000));

        assert rentalRepository.count() == 1;
    }

    @Test
    void givenNonExistingCustomerId_whenPostingRental_thenReturnNotFound() throws Exception {
        Long nonExistentCustomerId = 999L;

        String body = String.format("{\"customerId\": %d, \"gameId\": %d, \"daysRented\": 5}",
                nonExistentCustomerId, game.getId());

        mockMvc.perform(post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
        assert rentalRepository.count() == 0;
    }

    @Test
    void givenGameOutOfStock_whenPostingRental_thenReturnUnprocessableEntity() throws Exception {

        String firstRentalBody = String.format("{\"customerId\": %d, \"gameId\": %d, \"daysRented\": 1}",
                customer.getId(), game.getId());

        mockMvc.perform(post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstRentalBody))
                .andExpect(status().isCreated());

        String secondRentalBody = String.format("{\"customerId\": %d, \"gameId\": %d, \"daysRented\": 1}",
                customer.getId(), game.getId());

        mockMvc.perform(post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(secondRentalBody))
                .andExpect(status().isUnprocessableEntity()); 

        assert rentalRepository.count() == 1;
    }

    @Test
    void givenDaysRentedIsZero_whenPostingRental_thenReturnBadRequest() throws Exception {
        String body = String.format("{\"customerId\": %d, \"gameId\": %d, \"daysRented\": 0}",
                customer.getId(), game.getId());

        mockMvc.perform(post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest()); 

        
        assert rentalRepository.count() == 0;
    }
}