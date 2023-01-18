package tinygames.tictactoe.business.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import tinygames.tictactoe.model.GameData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GameServiceTest {

    private final GameService gameService = new GameService();

    private final GameData gameData = new GameData();

    @Test
    public void gameServiceTest() {

        int[] wrongNumberOfValuesBoard = {0, 0, 0, 0, 0, 0, 0, 0};
        gameData.setBoard(wrongNumberOfValuesBoard);
        assertThrows(HttpClientErrorException.class, () -> gameService.generateResponse(gameData));

        int[] invalidDataBoard = {0, 0, 0, 0, -2, 0, 0, 0, 0};
        gameData.setBoard(invalidDataBoard);
        assertThrows(HttpClientErrorException.class, () -> gameService.generateResponse(gameData));

        int[] playerWinsHorizontallyBoard = {0, -1, 0, 1, 1, 1, 0, -1, 0};
        gameData.setBoard(playerWinsHorizontallyBoard);
        assertEquals("player wins", gameService.generateResponse(gameData).getStatus());

        int[] playerWinsVerticallyBoard = {0, 1, 0, -1, 1, -1, 0, 1, 0};
        gameData.setBoard(playerWinsVerticallyBoard);
        assertEquals("player wins", gameService.generateResponse(gameData).getStatus());

        int[] aDrawBoard = {1, -1, -1, -1, 1, 1, 1, 1, -1};
        gameData.setBoard(aDrawBoard);
        assertEquals("a draw", gameService.generateResponse(gameData).getStatus());

        int[] apiWinsDiagonallyBoard = {-1, 1, 0, 1, 0, 0, 0, 1, -1};
        gameData.setBoard(apiWinsDiagonallyBoard);
        assertEquals("API wins", gameService.generateResponse(gameData).getStatus());
        assertEquals(-1, apiWinsDiagonallyBoard[4]);

        int[] apiPreventsPlayersVictoryDiagonallyBoard = {-1, 1, 1, 0, 0, -1, 1, 0, 0};
        gameData.setBoard(apiPreventsPlayersVictoryDiagonallyBoard);
        assertEquals("API makes a move", gameService.generateResponse(gameData).getStatus());
        assertEquals(-1, apiPreventsPlayersVictoryDiagonallyBoard[4]);

        int[] apiTakesMiddleCellBoard = {0, 0, 1, 0, 0, 0, 0, 0, 0};
        gameData.setBoard(apiTakesMiddleCellBoard);
        assertEquals("API makes a move", gameService.generateResponse(gameData).getStatus());
        assertEquals(-1, apiTakesMiddleCellBoard[4]);

        int[] apiTakesCornerCellBoard = {0, 0, 0, 0, 1, 0, 0, 0, 0};
        gameData.setBoard(apiTakesCornerCellBoard);
        assertEquals("API makes a move", gameService.generateResponse(gameData).getStatus());
        assertTrue(apiTakesCornerCellBoard[0] == -1
                || apiTakesCornerCellBoard[2] == -1
                || apiTakesCornerCellBoard[6] == -1
                || apiTakesCornerCellBoard[8] == -1);

        int[] apiTakesSideCellBoard = {0, 0, 1, 0, -1, 0, 1, 0, 0};
        gameData.setBoard(apiTakesSideCellBoard);
        assertEquals("API makes a move", gameService.generateResponse(gameData).getStatus());
        assertTrue(apiTakesSideCellBoard[1] == -1
                || apiTakesSideCellBoard[3] == -1
                || apiTakesSideCellBoard[5] == -1
                || apiTakesSideCellBoard[7] == -1);

        int[] apiTakesCornerCellWhenApisCellInTheMiddle = {0, 0, 1, 1, -1, 0, 0, 0, 0};
        gameData.setBoard(apiTakesCornerCellWhenApisCellInTheMiddle);
        assertEquals("API makes a move", gameService.generateResponse(gameData).getStatus());
        assertTrue(apiTakesCornerCellWhenApisCellInTheMiddle[0] == -1
                || apiTakesCornerCellWhenApisCellInTheMiddle[2] == -1
                || apiTakesCornerCellWhenApisCellInTheMiddle[6] == -1
                || apiTakesCornerCellWhenApisCellInTheMiddle[8] == -1);
    }
}
