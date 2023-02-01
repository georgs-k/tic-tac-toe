package tinygames.tictactoe.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tinygames.tictactoe.business.service.GameService;
import tinygames.tictactoe.model.GameData;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    public static String URL ="/api/v1";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService service;

    @Test
    void respondTestPositive() throws Exception {
        GameData gameData = createGameData();
        when(service.generateResponse(gameData)).thenReturn(gameData);
        ResultActions mvcResults = mockMvc
                .perform(MockMvcRequestBuilders
                        .post(URL + "/")
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(gameData.getStatus()))
                .andExpect(status().isOk());
        verify(service, times(1)).generateResponse(gameData);
    }

    @Test
    void respondTestNegative() throws Exception {
        GameData gameData = createGameData();
        gameData.setBoard(null);
        when(service.generateResponse(gameData)).thenReturn(gameData);
        ResultActions mvcResults = mockMvc
                .perform(MockMvcRequestBuilders
                        .post(URL + "/")
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, times(0)).generateResponse(gameData);
    }

    private GameData createGameData() {
        GameData gameData = new GameData();
        int[] board = {1, 0, 0, 0, -1, 0, 0, 0, 0};
        gameData.setBoard(board);
        gameData.setStatus("API makes a move");
        return gameData;
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
