package com.boardcamp.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc 
class GameIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    private final GameDTO gameDTO = new GameDTO(
        "Banco Imobiliário", 
        "http://image.com/banco.jpg", 
        3, 
        1500
    );

    @AfterEach
    void cleanUp() {
        gameRepository.deleteAll();
    }
    
    @Test
    void givenValidGame_whenCreatingGame_thenReturns201AndSavesToDB() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(gameDTO);

        mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isCreated()) 
            .andExpect(jsonPath("$.name").value("Banco Imobiliário"))
            .andExpect(jsonPath("$.id").isNumber());

        assertEquals(1, gameRepository.count());
    }
    
    @Test
    void givenExistingGameName_whenCreatingGame_thenReturns409Conflict() throws Exception {
       
        gameRepository.save(new GameModel(gameDTO)); 
        String jsonBody = objectMapper.writeValueAsString(gameDTO);

        mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isConflict()) 
            .andExpect(jsonPath("$").value("There is already a game with this name."));
       
        assertEquals(1, gameRepository.count());
    }

    @Test
    void givenInvalidStock_whenCreatingGame_thenReturns400BadRequest() throws Exception {
    
        GameDTO invalidGameDTO = new GameDTO(
            "Jogo Inválido", 
            "http://image.com/invalido.jpg", 
            0, // Valor inválido
            1500
        );
        String jsonBody = objectMapper.writeValueAsString(invalidGameDTO);

        mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isBadRequest()); 
        
        assertEquals(0, gameRepository.count());
    }
    
    @Test
    void whenGettingAllGames_thenReturns200AndGameList() throws Exception {
        gameRepository.save(new GameModel(gameDTO));

        mockMvc.perform(get("/games"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Banco Imobiliário"));
    }
}