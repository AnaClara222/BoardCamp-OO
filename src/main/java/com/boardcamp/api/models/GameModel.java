package com.boardcamp.api.models;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;

import com.boardcamp.api.dtos.GameDTO;

@Data
@Entity
@Table(name = "games")
public class GameModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String image;

    @Column(nullable = false)
    private Integer stockTotal;

    @Column(nullable = false)
    private Integer pricePerDay;

    public GameModel() {
    }

    public GameModel(GameDTO gameDTO) {
        this.name = gameDTO.getName();
        this.image = gameDTO.getImage();
        this.stockTotal = gameDTO.getStockTotal();
        this.pricePerDay = gameDTO.getPricePerDay();
    }
}
