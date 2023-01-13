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

    @ApiModelProperty(notes = "Game board 2d data array (3 x 3 cells): 1 = Player's cell, -1 = API's cell, 0 = Empty cell")
    @NotEmpty(message = "Provide game board data: 1 = Player's cell, -1 = API's cell, 0 = Empty cell")
    private int[] board;

    private String status;
}