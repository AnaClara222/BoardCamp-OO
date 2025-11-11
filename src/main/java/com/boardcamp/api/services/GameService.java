package com.boardcamp.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.exceptions.GameConflictException;
import com.boardcamp.api.exceptions.NotFoundException;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameModel> getAllGames() {
        return gameRepository.findAll();
    }

    public GameModel createGame(GameDTO gameDTO) {
        Optional<GameModel> existingGame = gameRepository.findByName(gameDTO.getName());
        if (existingGame.isPresent()) {
            throw new GameConflictException("There is already a game with this name.");
        }

        GameModel game = new GameModel(gameDTO);
        return gameRepository.save(game);
    }

    public GameModel getGameById(Long id) {
        return gameRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Game not found"));
    }
}
