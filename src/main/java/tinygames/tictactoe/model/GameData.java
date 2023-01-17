package tinygames.tictactoe.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@ApiModel(value = "Model of game data ")
@Component
@Data
public class GameData {

    @ApiModelProperty(
            notes = "Game board array of nine cells (3 x 3): 1 = Player's cell, -1 = API's cell, 0 = Empty cell",
            example = "[0, 0, 0, 0, 1, 0, 0, 0, 0]")
    @NotEmpty(message = "Provide game board data (left-right, top-bottom): 1 = Player's cell, -1 = API's cell, 0 = Empty cell")
    private int[] board;

    @ApiModelProperty(notes = "Game status after API's response")
    private String status;
}
