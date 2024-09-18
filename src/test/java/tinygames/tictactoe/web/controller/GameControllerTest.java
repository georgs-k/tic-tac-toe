package tinygames.tictactoe.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import tinygames.tictactoe.business.service.GameService;
import tinygames.tictactoe.model.GameData;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(GameService.class)
public class GameControllerTest {

    public static String URL ="/api/v1/";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void wrongNumberOfValuesTest() throws Exception {
        GameData gameData = createGameData(0, 0, 0, 0, 0, 0, 0, 0);
        gameData.setBoard(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidDataTest() throws Exception {
        GameData gameData = createGameData(0, 0, 0, 0, -2, 0, 0, 0, 0);
        gameData.setBoard(null);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void playerWinsWithHorizontalStreakTest() throws Exception {
        GameData gameData = createGameData(0, -1, 0, 1, 1, 1, 0, -1, 0);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("player wins"))
                .andExpect(status().isOk());
    }

    @Test
    void playerWinsWithVerticalStreakTest() throws Exception {
        GameData gameData = createGameData(0, 1, 0, -1, 1, -1, 0, 1, 0);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("player wins"))
                .andExpect(status().isOk());
    }

    @Test
    void aDrawTest() throws Exception {
        GameData gameData = createGameData(1, -1, -1, -1, 1, 1, 1, 1, -1);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("a draw"))
                .andExpect(status().isOk());
    }

    @Test
    void apiWinsWithDiagonalStreakTest() throws Exception {
        GameData gameData = createGameData(-1, 1, 0, 1, 0, 0, 0, 1, -1);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("API wins"))
                .andExpect(jsonPath("$.board[4]").value("-1"))
                .andExpect(status().isOk());
    }

    @Test
    void apiPreventsPlayersVictoryWithDiagonalStreakTest() throws Exception {
        GameData gameData = createGameData(-1, 1, 1, 0, 0, -1, 1, 0, 0);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("API makes a move"))
                .andExpect(jsonPath("$.board[4]").value("-1"))
                .andExpect(status().isOk());
    }

    @Test
    void apiTakesTheMiddleCellTest() throws Exception {
        GameData gameData = createGameData(0, 0, 1, 0, 0, 0, 0, 0, 0);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("API makes a move"))
                .andExpect(jsonPath("$.board[4]").value("-1"))
                .andExpect(status().isOk());
    }

    @Test
    void apiTakesANonMiddleCellTest() throws Exception {
        GameData gameData = createGameData(0, 0, 0, 0, 1, 0, 0, 0, 0);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(gameData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("API makes a move"))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("-1"));
    }

    private GameData createGameData(int... board) {
        GameData gameData = new GameData();
        gameData.setBoard(board);
        gameData.setStatus("Any text");
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
