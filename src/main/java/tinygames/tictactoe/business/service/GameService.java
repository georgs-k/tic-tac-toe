package tinygames.tictactoe.business.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tinygames.tictactoe.model.GameData;

import java.security.SecureRandom;

@Log4j2
@Service
public class GameService {

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

    public GameData generateResponse(GameData gameData) {

        final int TWO_PROGRAMS_CELLS = -2;
        final int PROGRAMS_CELL = -1;
        final int EMPTY_CELL = 0;
        final int PLAYERS_CELL = 1;
        final int TWO_PLAYERS_CELLS = 2;
        final int THREE_PLAYERS_CELLS = 3;

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
        int i = -1;
        if (findCellLineWith(TWO_PROGRAMS_CELLS, gameData.getBoard())) {
            while (gameData.getBoard()[cellLine[++i]] != EMPTY_CELL);
            gameData.getBoard()[cellLine[i]] = PROGRAMS_CELL;
            gameData.setStatus("API wins");
            return gameData;
        }
        gameData.setStatus("API makes a move");
        if (findCellLineWith(TWO_PLAYERS_CELLS, gameData.getBoard())) {
            while (gameData.getBoard()[cellLine[++i]] != EMPTY_CELL);
            gameData.getBoard()[cellLine[i]] = PROGRAMS_CELL;
            return gameData;
        }
        if (gameData.getBoard()[4] == EMPTY_CELL) {
            gameData.getBoard()[4] = PROGRAMS_CELL;
            return gameData;
        }
        if (gameData.getBoard()[4] == PLAYERS_CELL) {
            while (gameData.getBoard()[preferredCells1[++i]] != EMPTY_CELL);
            gameData.getBoard()[preferredCells1[i]] = PROGRAMS_CELL;
            return gameData;
        }
        int sumOfCornerCells = gameData.getBoard()[0] + gameData.getBoard()[2] + gameData.getBoard()[6] + gameData.getBoard()[8];
        int sumOfMidSideCells = gameData.getBoard()[1] + gameData.getBoard()[3] + gameData.getBoard()[5] + gameData.getBoard()[7];
        if (Math.abs(sumOfCornerCells) > Math.abs(sumOfMidSideCells)) {
            while (gameData.getBoard()[preferredCells2[++i]] != EMPTY_CELL);
            gameData.getBoard()[preferredCells2[i]] = PROGRAMS_CELL;
            return gameData;
        }
        while (gameData.getBoard()[preferredCells1[++i]] != EMPTY_CELL);
        gameData.getBoard()[preferredCells1[i]] = PROGRAMS_CELL;
        return gameData;
    }
}
