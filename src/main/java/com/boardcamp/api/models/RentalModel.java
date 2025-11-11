package com.boardcamp.api.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rentals")
public class RentalModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerModel customer;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameModel game;

    private LocalDate rentDate;
    private LocalDate returnDate;
    private int daysRented;
    
    
    private Long originalPrice; 
    private Long delayFee;     

    public RentalModel() {}

    public RentalModel(CustomerModel customer, GameModel game, LocalDate rentDate, int daysRented, Long originalPrice) {
        this.customer = customer;
        this.game = game;
        this.rentDate = rentDate;
        this.daysRented = daysRented;
        this.originalPrice = originalPrice; 
    }

}