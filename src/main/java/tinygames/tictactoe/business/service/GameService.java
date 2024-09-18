package tinygames.tictactoe.business.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import tinygames.tictactoe.model.GameData;

import java.security.SecureRandom;

@Log4j2
@Service
public class GameService {

    private static final int TWO_PROGRAMS_CELLS = -2;
    private static final int PROGRAMS_CELL = -1;
    private static final int EMPTY_CELL = 0;
    private static final int PLAYERS_CELL = 1;
    private static final int TWO_PLAYERS_CELLS = 2;
    private static final int THREE_PLAYERS_CELLS = 3;

    private final SecureRandom random = new SecureRandom();

    private final int[] indicesOfCellStreak = new int[3];
    private final int[] indicesOfPreferredCornerCells = {0, 2, 6, 8, 1, 3, 5, 7};
    private final int[] indicesOfPreferredSideCells = {1, 3, 5, 7, 0, 2, 6, 8};
    private int[] board;

    public GameData processGameData(GameData gameData) {
        board = gameData.getBoard();
        validateInput();
        shuffleIndicesOfPreferredCells();
        if (isFoundStreakWith(THREE_PLAYERS_CELLS)) {
            gameData.setStatus("player wins");
            log.info("Player wins");
            return gameData;
        }
        if (noMoreFreeCells()) {
            gameData.setStatus("a draw");
            log.info("No more free cells - a draw");
            return gameData;
        }
        if (isFoundStreakWith(TWO_PROGRAMS_CELLS)) {
            board[findIndexOfEmptyCell(indicesOfCellStreak)] = PROGRAMS_CELL;
            gameData.setStatus("API wins");
            log.info("API wins");
            return gameData;
        }
        gameData.setStatus("API makes a move");
        if (isFoundStreakWith(TWO_PLAYERS_CELLS)) {
            board[findIndexOfEmptyCell(indicesOfCellStreak)] = PROGRAMS_CELL;
            log.info("API prevents player's immediate victory");
            return gameData;
        }
        if (board[4] == EMPTY_CELL) {
            board[4] = PROGRAMS_CELL;
            log.info("API takes the middle cell");
            return gameData;
        }
        if (board[4] == PLAYERS_CELL) {
            board[findIndexOfEmptyCell(indicesOfPreferredCornerCells)] = PROGRAMS_CELL;
            log.info("API takes a corner cell if available, otherwise a side cell");
            return gameData;
        }
        int sumOfCornerCellValues = board[0] + board[2] + board[6] + board[8];
        int sumOfSideCellValues = board[1] + board[3] + board[5] + board[7];
        if (Math.abs(sumOfCornerCellValues) > Math.abs(sumOfSideCellValues)) {
            board[findIndexOfEmptyCell(indicesOfPreferredSideCells)] = PROGRAMS_CELL;
            log.info("API takes a side cell if available, otherwise a corner cell");
            return gameData;
        }
        board[findIndexOfEmptyCell(indicesOfPreferredCornerCells)] = PROGRAMS_CELL;
        log.info("API takes a corner cell if available, otherwise a side cell");
        return gameData;
    }

    private void validateInput() {
        if (board.length != 9) {
            log.error("Exception {} is thrown. Wrong number of values (must be 9)", HttpStatus.BAD_REQUEST);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Wrong number of values (must be 9)");
        }
        for (int i = 0; i < 9; i++) if (board[i] < -1 || board[i] > 1) {
            log.error("Exception {} is thrown. Invalid data (acceptable values: -1, 0, or 1)", HttpStatus.BAD_REQUEST);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid data (acceptable values: -1, 0, or 1)");
        }
    }

    private void shuffleIndicesOfPreferredCells() {
        int swapIndex, temp;
        for (int i = 3; i > 1; i--) {
            swapIndex = random.nextInt(i);
            temp = indicesOfPreferredCornerCells[i];
            indicesOfPreferredCornerCells[i] = indicesOfPreferredCornerCells[swapIndex];
            indicesOfPreferredCornerCells[swapIndex] = temp;
            swapIndex = random.nextInt(i);
            temp = indicesOfPreferredSideCells[i];
            indicesOfPreferredSideCells[i] = indicesOfPreferredSideCells[swapIndex];
            indicesOfPreferredSideCells[swapIndex] = temp;
        }
        for (int i = 0; i < 4; i++) {
            indicesOfPreferredCornerCells[i + 4] = indicesOfPreferredSideCells[i];
            indicesOfPreferredSideCells[i + 4] = indicesOfPreferredCornerCells[i];
        }
    }

    private boolean isFoundStreakWith(int sumOfCellValues) {
        for (int cellIndex = 0; cellIndex < 9; cellIndex += 3)
            if (sumOfCellValues == board[cellIndex] + board[cellIndex + 1] + board[cellIndex + 2]) {
                for (int i = 0; i < 3; i++) indicesOfCellStreak[i] = cellIndex + i;
                return true;
            }
        for (int cellIndex = 0; cellIndex < 3; cellIndex++)
            if (sumOfCellValues == board[cellIndex] + board[cellIndex + 3] + board[cellIndex + 6]) {
                for (int i = 0; i < 3; i++) indicesOfCellStreak[i] = cellIndex + i * 3;
                return true;
            }
        if (sumOfCellValues == board[0] + board[4] + board[8]) {
            for (int i = 0; i < 3; i++) indicesOfCellStreak[i] = i * 4;
            return true;
        }
        if (sumOfCellValues == board[2] + board[4] + board[6]) {
            for (int i = 0; i < 3; i++) indicesOfCellStreak[i] = i * 2 + 2;
            return true;
        }
        return false;
    }

    private boolean noMoreFreeCells() {
        boolean noMoreFreeCells = true;
        for (int i = 0; i < 9; i++)
            if (board[i] == EMPTY_CELL) {
                noMoreFreeCells = false;
                break;
            }
        return noMoreFreeCells;
    }

    private int findIndexOfEmptyCell(int[] indices) {
        int i;
        for (i = 0; i < indices.length; i++) if (board[indices[i]] == EMPTY_CELL) break;
        return indices[i];
    }
}
