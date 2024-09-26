package tinygames.tictactoe.business.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import tinygames.tictactoe.model.GameData;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameServiceTest {

    private static final Integer PROGRAMS_CELL = -1;

    private final GameService gameService = new GameService();

    private final GameData gameData = new GameData();

    @Test
    void wrongNumberOfValuesTest() {
        int[] board = {0, 0, 0, 0, 0, 0, 0, 0};
        gameData.setBoard(board);
        assertThrows(HttpClientErrorException.class, () -> gameService.processGameData(gameData));
    }

    @Test
    void invalidDataTest() {
        int[] board = {0, 0, 0, 0, -2, 0, 0, 0, 0};
        gameData.setBoard(board);
        assertThrows(HttpClientErrorException.class, () -> gameService.processGameData(gameData));
    }

    @Test
    void playerWinsWithHorizontalStreakTest() {
        int[] board = {0, -1, 0, 1, 1, 1, 0, -1, 0};
        gameData.setBoard(board);
        assertEquals("player wins", gameService.processGameData(gameData).getStatus());
    }

    @Test
    void playerWinsWithVerticalStreakTest() {
        int[] board = {0, 1, 0, -1, 1, -1, 0, 1, 0};
        gameData.setBoard(board);
        assertEquals("player wins", gameService.processGameData(gameData).getStatus());
    }

    @Test
    void aDrawTest() {
        int[] board = {1, -1, -1, -1, 1, 1, 1, 1, -1};
        gameData.setBoard(board);
        assertEquals("a draw", gameService.processGameData(gameData).getStatus());
    }

    @Test
    void apiWinsWithDiagonalStreakTest() {
        int[] board = {-1, 1, 0, 1, 0, 0, 0, 1, -1};
        gameData.setBoard(board);
        assertEquals("API wins", gameService.processGameData(gameData).getStatus());
        assertEquals(PROGRAMS_CELL, board[4]);
    }

    @Test
    void apiPreventsPlayersVictoryWithDiagonalStreakTest() {
        int[] board = {-1, 1, 1, 0, 0, -1, 1, 0, 0};
        gameData.setBoard(board);
        assertEquals("API makes a move", gameService.processGameData(gameData).getStatus());
        assertEquals(PROGRAMS_CELL, board[4]);
    }

    @Test
    void apiTakesTheMiddleCellTest() {
        int[] board = {0, 0, 1, 0, 0, 0, 0, 0, 0};
        gameData.setBoard(board);
        assertEquals("API makes a move", gameService.processGameData(gameData).getStatus());
        assertEquals(PROGRAMS_CELL, board[4]);
    }

    @Test
    void apiTakesACornerCellTest() {
        int[] board = {0, 0, 0, 0, 1, 0, 0, 0, 0};
        gameData.setBoard(board);
        assertEquals("API makes a move", gameService.processGameData(gameData).getStatus());
        assertTrue(asList(board[0], board[2], board[6], board[8]).contains(PROGRAMS_CELL));
    }

    @Test
    void apiTakesASideCellTest() {
        int[] board = {0, 0, 1, 0, -1, 0, 1, 0, 0};
        gameData.setBoard(board);
        assertEquals("API makes a move", gameService.processGameData(gameData).getStatus());
        assertTrue(asList(board[1], board[3], board[5], board[7]).contains(PROGRAMS_CELL));
    }

    @Test
    void apiTakesACornerCellWhenApisCellInTheMiddleTest() {
        int[] board = {0, 0, 1, 1, -1, 0, 0, 0, 0};
        gameData.setBoard(board);
        assertEquals("API makes a move", gameService.processGameData(gameData).getStatus());
        assertTrue(asList(board[0], board[2], board[6], board[8]).contains(PROGRAMS_CELL));
    }
}
