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

    private final int[] cellLine = new int[3];
    private final int[] preferredCells1 = {0, 2, 6, 8, 1, 3, 5, 7};
    private final int[] preferredCells2 = {1, 3, 5, 7, 0, 2, 6, 8};
    private final SecureRandom random = new SecureRandom();
    private int[] board;

    public GameData generateResponse(GameData gameData) {

        final int TWO_PROGRAMS_CELLS = -2;
        final int PROGRAMS_CELL = -1;
        final int EMPTY_CELL = 0;
        final int PLAYERS_CELL = 1;
        final int TWO_PLAYERS_CELLS = 2;
        final int THREE_PLAYERS_CELLS = 3;

        board = gameData.getBoard();
        validateInput();
        shufflePreferredCells();
        if (findCellLineWith(THREE_PLAYERS_CELLS)) {
            gameData.setStatus("player wins");
            log.info("Player wins");
            return gameData;
        }
        boolean existsEmptyCell = false;
        for (int i = 0; i < 9; i++) if (board[i] == EMPTY_CELL) {
            existsEmptyCell = true;
            break;
        }
        if (!existsEmptyCell) {
            gameData.setStatus("a draw");
            log.info("No more free cells - a draw");
            return gameData;
        }
        int i = -1;
        if (findCellLineWith(TWO_PROGRAMS_CELLS)) {
            while (board[cellLine[++i]] != EMPTY_CELL);
            board[cellLine[i]] = PROGRAMS_CELL;
            gameData.setStatus("API wins");
            log.info("API wins");
            return gameData;
        }
        gameData.setStatus("API makes a move");
        if (findCellLineWith(TWO_PLAYERS_CELLS)) {
            while (board[cellLine[++i]] != EMPTY_CELL);
            board[cellLine[i]] = PROGRAMS_CELL;
            log.info("API prevents player's immediate victory");
            return gameData;
        }
        if (board[4] == EMPTY_CELL) {
            board[4] = PROGRAMS_CELL;
            log.info("API takes the middle cell");
            return gameData;
        }
        if (board[4] == PLAYERS_CELL) {
            while (board[preferredCells1[++i]] != EMPTY_CELL);
            board[preferredCells1[i]] = PROGRAMS_CELL;
            log.info("API takes a corner cell if available, otherwise a side cell");
            return gameData;
        }
        int sumOfCornerCells = board[0] + board[2] + board[6] + board[8];
        int sumOfSideCells = board[1] + board[3] + board[5] + board[7];
        if (Math.abs(sumOfCornerCells) > Math.abs(sumOfSideCells)) {
            while (board[preferredCells2[++i]] != EMPTY_CELL);
            board[preferredCells2[i]] = PROGRAMS_CELL;
            log.info("API takes a side cell if available, otherwise a corner cell");
            return gameData;
        }
        while (board[preferredCells1[++i]] != EMPTY_CELL);
        board[preferredCells1[i]] = PROGRAMS_CELL;
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

    private void shufflePreferredCells() {
        int swapIndex, temp;
        for (int i = 3; i > 1; i--) {
            swapIndex = random.nextInt(i);
            temp = preferredCells1[i];
            preferredCells1[i] = preferredCells1[swapIndex];
            preferredCells1[swapIndex] = temp;
            swapIndex = random.nextInt(i);
            temp = preferredCells2[i];
            preferredCells2[i] = preferredCells2[swapIndex];
            preferredCells2[swapIndex] = temp;
        }
        for (int i = 0; i < 4; i++) {
            preferredCells1[i + 4] = preferredCells2[i];
            preferredCells2[i + 4] = preferredCells1[i];
        }
    }

    private boolean findCellLineWith(int choice) {
        for (int cellNumber = 0; cellNumber < 9; cellNumber += 3)
            if (choice == board[cellNumber] + board[cellNumber + 1] + board[cellNumber + 2]) {
                for (int i = 0; i < 3; i++) cellLine[i] = cellNumber + i;
                return true;
            }
        for (int cellNumber = 0; cellNumber < 3; cellNumber++)
            if (choice == board[cellNumber] + board[cellNumber + 3] + board[cellNumber + 6]) {
                for (int i = 0; i < 3; i++) cellLine[i] = cellNumber + i * 3;
                return true;
            }
        if (choice == board[0] + board[4] + board[8]) {
            for (int i = 0; i < 3; i++) cellLine[i] = i * 4;
            return true;
        }
        if (choice == board[2] + board[4] + board[6]) {
            for (int i = 0; i < 3; i++) cellLine[i] = i * 2 + 2;
            return true;
        }
        return false;
    }
}
