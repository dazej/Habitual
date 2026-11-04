package dazzejpro.habitual.api.controller;

import dazzejpro.habitual.api.dtos.LoginDtos.LoginRequest;
import dazzejpro.habitual.api.dtos.LoginDtos.LoginResponse;
import dazzejpro.habitual.api.dtos.RegisterDtos.RegisterRequest;
import dazzejpro.habitual.api.dtos.RegisterDtos.RegisterResponse;
import dazzejpro.habitual.api.dtos.ResetPasswordDtos.ResetPasswordRequest;
import dazzejpro.habitual.api.dtos.ResetPasswordDtos.ResetPasswordResponse;
import dazzejpro.habitual.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;



// handles the routes for login and register

@RestController
@Tag(name = "Authentication", description = "Account registration and login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // As a user, I want to be able to create an account.
    // POST method and URI or /register
    @Operation(
            summary = "Register a new account",
            description = "Creates a new user account from a username, first and last name, email and password. "
                    + "The username and email must not already be registered. "
                    + "On success returns the details of the created account."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request: missing body, a blank or missing field, an invalid email, "
                            + "a username outside 3-50 characters, or a password outside 8-100 characters",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Missing password",
                                            value = "{\"message\": \"Password is required\"}"),
                                    @ExampleObject(name = "Password too short",
                                            value = "{\"message\": \"Password must be between 8 and 100 characters\"}"),
                                    @ExampleObject(name = "Invalid email",
                                            value = "{\"message\": \"Email must be a valid email address\"}"),
                                    @ExampleObject(name = "Missing body",
                                            value = "{\"message\": \"Request body is missing or is not valid JSON\"}")
                            })),
            @ApiResponse(responseCode = "409", description = "The username or email is already registered",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Username 'jdoe' is already taken\"}"))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"An unexpected error occurred. Please try again later.\"}")))
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Parameter(description = "Registration details for the new account", required = true)
            @Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // As a user, I want to be able to log in to my account so I can see my goals.
    // POST method and /login for API URI
    @Operation(
            summary = "Log in to an existing account",
            description = "Authenticates a user by username and password. On success returns the user's id, "
                    + "username and an authentication token to send with later requests.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(name = "Valid login", value = """
                                    {
                                      "username": "jdoe",
                                      "password": "Secur3Pass!"
                                    }
                                    """)))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful; token returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request: missing body, or a blank or missing username or password",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Missing username",
                                            value = "{\"message\": \"Username is required\"}"),
                                    @ExampleObject(name = "Missing password",
                                            value = "{\"message\": \"Password is required\"}")
                            })),
            @ApiResponse(responseCode = "401", description = "The username and password do not match an account",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Invalid username or password\"}"))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"An unexpected error occurred. Please try again later.\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "Username and password of the account", required = true)
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // As a user, I want to reset my account password.
    @Operation(
            summary = "Reset an account's password",
            description = "Sets a new password on the account registered with the given email address. "
                    + "Both the email and the new password are required.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email of the account and the new password",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordRequest.class),
                            examples = @ExampleObject(name = "Valid reset", value = """
                                    {
                                      "email": "jane.doe@example.com",
                                      "newPassword": "N3wSecur3Pass!"
                                    }
                                    """)))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request: missing body, or a blank or missing email or new password",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Missing field",
                                            value = "{\"message\": \"email and password are required\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "No account is registered with that email",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"user not found\"}")))
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(
            @Parameter(description = "Email of the account and the new password", required = true)
            @RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }


}
