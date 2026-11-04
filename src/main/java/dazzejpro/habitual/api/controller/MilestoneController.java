package dazzejpro.habitual.api.controller;

import dazzejpro.habitual.api.dtos.MilestoneDtos.MilestoneShareResponse;
import dazzejpro.habitual.api.service.MilestoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MilestoneController {

    private final MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    // As a user, I want to share a streak milestone I have reached.
    @Operation(
            summary = "Share a streak milestone",
            description = "Builds a shareable message for a habit whose current streak is exactly 10, 50 or 100 days. "
                    + "The habit must already have a streak, which is calculated and saved when "
                    + "GET /habits/{habitId}/streak is called for it. Any other streak length is rejected."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Milestone reached; shareable message returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MilestoneShareResponse.class))),
            @ApiResponse(responseCode = "400", description = "The habit's streak is not a shareable milestone",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Shareable milestone has not been reached\"}"))),
            @ApiResponse(responseCode = "404", description = "No habit exists with the given id, or it has no streak yet",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Habit not found",
                                            value = "{\"message\": \"Habit not found\"}"),
                                    @ExampleObject(name = "Streak not found",
                                            value = "{\"message\": \"Streak not found\"}")
                            }))
    })
    @PostMapping("/habits/{habitId}/milestone/share")
    public ResponseEntity<MilestoneShareResponse> shareMilestone(
            @Parameter(description = "ID of the habit whose streak milestone should be shared",
                    required = true, example = "1")
            @PathVariable int habitId) {

        MilestoneShareResponse response =
                milestoneService.shareMilestone(habitId);
        return ResponseEntity.ok(response);
    }
}
