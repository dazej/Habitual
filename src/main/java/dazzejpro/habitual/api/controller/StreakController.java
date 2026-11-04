package dazzejpro.habitual.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dazzejpro.habitual.api.dtos.StreakDtos.StreakResponse;
import dazzejpro.habitual.api.service.StreakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Streaks", description = "Habit streak tracking")
public class StreakController {
    private final StreakService streakService;

    public StreakController(StreakService streakService) {
        this.streakService = streakService;
    }

    // As a user, I want the number of consecutive days I complete a habit to be tracked and displayed.
    // GET method for /habits/{habitId}/streak
    @Operation(
            summary = "Get habit streak",
            description ="Returns the current streak data for a habit"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Streak retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Habit not found")
    })
    @GetMapping("/habits/{habitId}/streak")
    public ResponseEntity<StreakResponse> getStreaks(
            @Parameter( description = "ID of the habit", example = "1")
            @PathVariable int habitId
    ){
        StreakResponse response = streakService.streak(habitId);
        return ResponseEntity.ok(response);


    }


}
