package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {

    @NotBlank
    private String name;

    private String image;

    @NotNull
    @Positive
    private Integer stockTotal;

    @NotNull
    @Positive
    private Integer pricePerDay;
}
