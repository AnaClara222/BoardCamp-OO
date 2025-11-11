package com.boardcamp.api.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.exceptions.GameConflictException;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.services.GameService;

@SpringBootTest
class GameUnitTests {

    @InjectMocks
    GameService gameService;

    @Mock
    GameRepository gameRepository;

    GameDTO gameDTO;

    @BeforeEach
    void setup() {
        gameDTO = new GameDTO(
            "Banco Imobiliário", 
            "http://image.com/banco.jpg", 
            3, 
            1500
        );
    }

    @Test
    void givenValidGame_whenCreatingGame_thenReturnsGame() {
        
        doReturn(Optional.empty()).when(gameRepository).findByName(gameDTO.getName());
        doReturn(new GameModel(gameDTO)).when(gameRepository).save(any());

        GameModel result = gameService.createGame(gameDTO);

        assertNotNull(result);
        assertEquals(gameDTO.getName(), result.getName());
        
        verify(gameRepository, times(1)).findByName(anyString());
        verify(gameRepository, times(1)).save(any(GameModel.class));
    }

    @Test
    void givenExistingGameName_whenCreatingGame_thenThrowsGameConflictException() {
        GameModel existingGame = new GameModel(gameDTO);
        
        doReturn(Optional.of(existingGame)).when(gameRepository).findByName(gameDTO.getName());

        assertThrows(GameConflictException.class, () -> {
            gameService.createGame(gameDTO);
        });
        
        verify(gameRepository, times(1)).findByName(anyString());
        verify(gameRepository, times(0)).save(any(GameModel.class));
    }

    @Test
    void whenGettingAllGames_thenReturnsGameList() {
        doReturn(java.util.List.of(new GameModel(gameDTO)))
            .when(gameRepository).findAll();

            var result = gameService.getAllGames();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(gameRepository, times(1)).findAll();
    }
}