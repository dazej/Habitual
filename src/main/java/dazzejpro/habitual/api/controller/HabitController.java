package dazzejpro.habitual.api.controller;

import dazzejpro.habitual.api.dtos.HabitDtos;
import dazzejpro.habitual.api.dtos.HabitDtos.CreateHabitRequest;
import dazzejpro.habitual.api.dtos.HabitDtos.CreateHabitResponse;
import dazzejpro.habitual.api.dtos.HabitDtos.UpdateHabitRequest;
import dazzejpro.habitual.api.dtos.HabitDtos.UpdateHabitResponse;
import dazzejpro.habitual.api.service.HabitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dazzejpro.habitual.api.dtos.AssignCategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

// handles the routes for creating and editing habits

@RestController
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    // As a user, I want to create a habit so I can complete it.
    //POST method and URI for habit
    @Operation(
            summary = "Create a habit",
            description = "Creates a new habit for an existing user. The habit name is required; frequency and "
                    + "goal are optional and can be set now or changed later with the edit endpoint."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Habit created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateHabitResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request: missing body, or a blank or missing habitName",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Missing habitName",
                                            value = "{\"message\": \"Invalid/missing field\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "No user exists with the given userId",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"User not found\"}")))
    })
    @PostMapping("/users/{userId}/habit")
    public ResponseEntity<CreateHabitResponse> createHabit(
            @Parameter(description = "ID of the user who will own the habit", required = true, example = "1")
            @PathVariable int userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the habit to create",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateHabitRequest.class),
                            examples = {
                                    @ExampleObject(name = "Name only", value =
                                            "{\"habitName\":\"Drink 8 glasses of water\"}"),
                                    @ExampleObject(name = "With frequency and goal", value =
                                            "{\"habitName\":\"Drink 8 glasses of water\",\"frequency\":\"daily\",\"goal\":8}")
                            }))
            @RequestBody CreateHabitRequest request) {

        CreateHabitResponse response = habitService.createHabit(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // As a user, I want to delete a habit so that I can remove habits I no longer want to track.
    @Operation(
            summary = "Delete a habit",
            description = "Permanently removes the habit with the given id so it is no longer tracked. "
                    + "This cannot be undone. On success the response has status 204 and no body."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Habit deleted successfully; no response body",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "The habit id in the URL is not a whole number",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"id must be a whole number\"}"))),
            @ApiResponse(responseCode = "404", description = "No habit exists with the given id",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Habit not found\"}"))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"An unexpected error occurred. Please try again later.\"}")))
    })
    @DeleteMapping("/habits/{id}")
    public ResponseEntity<Void> deleteHabit(
            @Parameter(description = "Unique ID of the habit to delete", required = true, example = "1",
                    schema = @Schema(type = "integer", format = "int32", minimum = "1"))
            @PathVariable Integer id) {
        habitService.deleteHabit(id);
        return ResponseEntity.noContent().build(); // 204
    }


    // As a user, I want to edit an existing habit so I can change its name, frequency, or goal when my needs change.
    @Operation(
            summary = "Edit an existing habit",
            description = "Partially updates a habit's name, frequency, and/or goal. Only the fields "
                    + "included in the request body are changed; omitted fields keep their current "
                    + "values. At least one field is required."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habit updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateHabitResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request: empty body, blank habitName or frequency, "
                            + "or a goal that is not a positive number",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value =
                                    "{\"message\": \"At least one field (habitName, frequency, goal) is required\"}"))),
            @ApiResponse(responseCode = "404", description = "No habit exists with the given habitId",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Habit not found\"}")))
    })
    @PatchMapping("/habits/{habitId}")
    public ResponseEntity<UpdateHabitResponse> updateHabit(
            @Parameter(description = "ID of the habit to edit", example = "1")
            @PathVariable int habitId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fields to change. Include at least one.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateHabitRequest.class),
                            examples = {
                                    @ExampleObject(name = "Edit all fields", value =
                                            "{\"habitName\":\"Get 12k steps a day\",\"frequency\":\"daily\",\"goal\":12000}"),
                                    @ExampleObject(name = "Edit one field", value =
                                            "{\"frequency\":\"weekly\"}")
                            }))
            @RequestBody UpdateHabitRequest request) {

        UpdateHabitResponse response = habitService.updateHabit(habitId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // As a user, I want to assign a category to a habit.
    @Operation(
            summary = "Assign a category to a habit",
            description ="Assign an existing category to the habit."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category assigned to the habit successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssignCategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "No habit or no category exists with the given id",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Habit not found",
                                            value = "{\"message\": \"Habit not found\"}"),
                                    @ExampleObject(name = "Category not found",
                                            value = "{\"message\": \"Category not found\"}")
                            }))
    })
    @PutMapping("/habits/{habitId}/category/{categoryId}")
    public ResponseEntity<AssignCategoryResponse> assignCategory(
            @Parameter( description = "ID of the habit", example = "1")
            @PathVariable int habitId,
            @Parameter( description = "ID of the category", example = "1")
            @PathVariable int categoryId) {

        AssignCategoryResponse response = habitService.assignCategory(habitId, categoryId);
        return ResponseEntity.ok(response);
    }


    // As a user, I want to pause a habit without deleting its history.
    @Operation(
            summary = "Pause an existing habit",
            description = "Pauses an existing habit without deleting it or its tracking history. "
                    + "The habit is identified by its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Habit paused successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(
                            implementation = HabitDtos.PauseHabitResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid habit ID",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Invalid habit ID format\"}"))),
            @ApiResponse(responseCode = "404", description = "No habit exists with the provided ID",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Habit not found\"}"))),
            @ApiResponse(responseCode = "409", description = "Habit is already paused",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\":\"Habit is already paused\"}"))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"An unexpected error occurred. Please try again later.\"}" )))
    })
    @PatchMapping("/habits/{id}/pause")
    public ResponseEntity<HabitDtos.PauseHabitResponse> pauseHabit(
            @Parameter(description = "Unique ID of the habit to pause", required = true,
                    example = "1", schema = @Schema(type = "integer", format = "int32", minimum = "1"))
            @PathVariable Integer id) {

        HabitDtos.PauseHabitResponse response = habitService.pauseHabit(id);
        return ResponseEntity.ok(response);
    }
}
