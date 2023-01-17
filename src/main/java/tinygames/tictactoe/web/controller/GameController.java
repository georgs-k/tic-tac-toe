package tinygames.tictactoe.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tinygames.tictactoe.business.service.GameService;
import tinygames.tictactoe.model.GameData;

import javax.validation.Valid;

@Api(tags = "Game Controller")
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class GameController {

    private final GameService gameService;

    @PostMapping("/")
    @ApiOperation(value = "Responds to submitted game data",
            notes = "Provide an array with game board data (left-right, top-bottom): 1 = Player's cell, -1 = API's cell, 0 = Empty cell",
            response = GameData.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Wrong data or format"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<GameData> respond(@Valid @RequestBody GameData gameData, BindingResult bindingResult) {
        log.info("Respond to game data: {}", gameData);
        if (bindingResult.hasErrors()) {
            log.error("Wrong data or format! {}", bindingResult);
            return ResponseEntity.badRequest().build();
        }
        log.debug("API responds with game data: {}", gameData);
        return new ResponseEntity<>(gameService.generateResponse(gameData), HttpStatus.OK);
    }
}
