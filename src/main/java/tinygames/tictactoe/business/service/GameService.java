package tinygames.tictactoe.business.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tinygames.tictactoe.model.GameData;

import java.security.SecureRandom;

@Log4j2
@Service
public class GameService {

    private final int THREE_PROGRAMS_CELLS = -3;
    private final int TWO_PROGRAMS_CELLS = -2;
    private final int PROGRAMS_CELL = -1;
    private final int EMPTY_CELL = 0;
    private final int PLAYERS_CELL = 1;
    private final int TWO_PLAYERS_CELLS = 2;
    private final int THREE_PLAYERS_CELLS = 3;
    private final int[] cellLine = new int[3];
    private final int[] preferredCells1 = {0, 2, 6, 8, 1, 3, 5, 7};
    private final int[] preferredCells2 = {1, 3, 5, 7, 0, 2, 6, 8};
    private final SecureRandom random = new SecureRandom();

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

    private boolean findCellLineWith(int choice, int[] board) {
        for (int cellNumber = 0; cellNumber < 9; cellNumber += 3)
            if (choice == board[cellNumber]
                    + board[cellNumber + 1]
                    + board[cellNumber + 2]) {
                for (int i = 0; i < 3; i++) cellLine[i] = cellNumber + i;
                return true;
            }
        for (int cellNumber = 0; cellNumber < 3; cellNumber++)
            if (choice == board[cellNumber]
                    + board[cellNumber + 3]
                    + board[cellNumber + 6]) {
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

    private void makeMove(int[] board) {
        int i = -1;
        if (findCellLineWith(TWO_PROGRAMS_CELLS, board)) {
            while (board[cellLine[++i]] != EMPTY_CELL);
            board[cellLine[i]] = PROGRAMS_CELL;
            return;
        }
        if (findCellLineWith(TWO_PLAYERS_CELLS, board)) {
            while (board[cellLine[++i]] != EMPTY_CELL);
            board[cellLine[i]] = PROGRAMS_CELL;
            return;
        }
        switch (board[4]) {
            case EMPTY_CELL:
                board[4] = PROGRAMS_CELL;
                return;
            case PLAYERS_CELL:
                while (board[preferredCells1[++i]] != EMPTY_CELL);
                board[preferredCells1[i]] = PROGRAMS_CELL;
                return;
            case PROGRAMS_CELL:
                int sumOfCornerCells = board[0] + board[2] + board[6] + board[8];
                int sumOfMidSideCells = board[1] + board[3] + board[5] + board[7];
                if (sumOfCornerCells + sumOfMidSideCells > 0) {
                    while (board[preferredCells2[++i]] != EMPTY_CELL);
                    board[preferredCells2[i]] = PROGRAMS_CELL;
                    return;
                }
                while (board[preferredCells1[++i]] != EMPTY_CELL);
                board[preferredCells1[i]] = PROGRAMS_CELL;
            }
    }

    public GameData generateResponse(GameData gameData) {
        shufflePreferredCells();
        if (findCellLineWith(THREE_PLAYERS_CELLS, gameData.getBoard())) {
            gameData.setStatus("player wins");
            return gameData;
        }
        boolean existsEmptyCell = false;
        for (int i = 0; i < 9; i++) if (gameData.getBoard()[i] == EMPTY_CELL) {
            existsEmptyCell = true;
            break;
        }
        if (!existsEmptyCell) {
            gameData.setStatus("a draw");
            return gameData;
        }
        makeMove(gameData.getBoard());
        if (findCellLineWith(THREE_PROGRAMS_CELLS, gameData.getBoard())) {
            gameData.setStatus("API wins");
            return gameData;
        }
        gameData.setStatus("API makes a move");
        return gameData;
    }
}
