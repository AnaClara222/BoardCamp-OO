package com.boardcamp.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.services.GameService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<Object> getGames() {
        return ResponseEntity.status(HttpStatus.OK).body(gameService.getAllGames());
    }

    @PostMapping
    public ResponseEntity<Object> createGame(@RequestBody @Valid GameDTO gameDTO) {
        var game = gameService.createGame(gameDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }
}
