package dazzejpro.habitual.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dazzejpro.habitual.api.dtos.CompletionDtos.CompletionRequest;
import dazzejpro.habitual.api.dtos.CompletionDtos.CompletionResponse;
import dazzejpro.habitual.api.service.CompletionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@Tag(name= "Completions", description = "Record and view habit completions")
public class CompletionController {
    private final CompletionService completionService;

    public CompletionController(CompletionService completionService) {
        this.completionService = completionService;
    }

    // As a user, I want the number of consecutive days I complete a habit to be tracked and displayed.
    // POST method for /habits/{habitId}/completions
    @Operation(
            summary = "Record a habit completion.",
            description ="Records the date of the habit completion."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Habit completion recorded successfully."),
            @ApiResponse(responseCode = "400", description = "Completion date is required."),
            @ApiResponse(responseCode = "404", description = "Habit not found."),
            @ApiResponse(responseCode = "409", description = "Habit is paused or completion date duplicate.")
    })
    @PostMapping("/habits/{habitId}/completions")
    public ResponseEntity<CompletionResponse> getCompletions(
            @Parameter( description = "ID of the habit", example = "1")
            @PathVariable int habitId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Date of completion", required = true)
            @RequestBody CompletionRequest request
    ){
        CompletionResponse response = completionService.completion(habitId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);


    }
    @Operation(
            summary = "Get completion history",
            description = "Returns list of completions for a habit"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habit completion list retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "habit not found.")
    })
    @GetMapping("/habits/{habitId}/completions")
    public ResponseEntity<List<CompletionResponse>> getCompletionHistory(
            @Parameter( description = "ID of the habit", example = "1")
            @PathVariable int habitId
    ) {
        List<CompletionResponse> response =
                completionService.getCompletionHistory(habitId);

        return ResponseEntity.ok(response);
    }

}
