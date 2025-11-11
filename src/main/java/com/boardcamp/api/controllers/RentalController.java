package com.boardcamp.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.services.RentalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public ResponseEntity<List<RentalModel>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @PostMapping
    public ResponseEntity<RentalModel> createRental(@RequestBody @Valid RentalDTO body) {
        RentalModel newRental = rentalService.createRental(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRental);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<RentalModel> returnRental(@PathVariable Long id) {
        RentalModel finishedRental = rentalService.returnRental(id);

        return ResponseEntity.ok(finishedRental);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}