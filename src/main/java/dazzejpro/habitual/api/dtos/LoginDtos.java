package dazzejpro.habitual.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class LoginDtos {
// setting and getting data transfer for user object

    @Schema(name = "LoginRequest", description = "Credentials used to log in")
    public static class LoginRequest {

        @Schema(
            description = "Username of the account",
            example = "jdoe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string",
            minLength = 3,
            maxLength = 50
        )
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;

        @Schema(
            description = "Account password. Write-only; never returned in any response.",
            example = "Secur3Pass!",
            requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.WRITE_ONLY,
            type = "string",
            format = "password",
            minLength = 8,
            maxLength = 100
        )
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @Schema(name = "LoginResponse", description = "Result of a successful login")
    public static class LoginResponse {

        @Schema(
            description = "Id of the authenticated user",
            example = "42",
            requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.READ_ONLY,
            type = "integer",
            format = "int32"
        )
        private final int userId;

        @Schema(description = "Username of the authenticated user", example = "jdoe",
                requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
        private final String username;

        @Schema(
            description = "Authentication token to send in the Authorization header of later requests",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZG9lIn0.abc123signature",
            requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.READ_ONLY,
            type = "string"
        )

        private final String token;

        public LoginResponse(Integer userId, String username, String token) {
            this.userId = userId;
            this.username = username;
            this.token = token;
        }

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getToken() { return token; }
    }
}
