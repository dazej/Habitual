package dazzejpro.habitual.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterDtos {

    @Schema(name = "RegisterRequest", description = "Payload for creating a new user account")
    public static class RegisterRequest {

        @Schema(
            description = "Unique login name. Letters, numbers, dots, underscores and hyphens only.",
            example = "jdoe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string",
            minLength = 3,
            maxLength = 50,
            pattern = "^[A-Za-z0-9._-]+$"
        )
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$",
                message = "Username may only contain letters, numbers, dots, underscores and hyphens")
        private String username;

        @Schema(
            description = "User's first (given) name",
            example = "Jane",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string",
            maxLength = 50
        )
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must be at most 50 characters")
        @JsonProperty("first_name")
        private String firstName;

        @Schema(
            description = "User's last (family) name",
            example = "Doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string",
            maxLength = 50
        )
        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must be at most 50 characters")
        @JsonProperty("last_name")
        private String lastName;

        @Schema(
            description = "User's email address; must be a valid email format and unique",
            example = "jane.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string",
            format = "email",
            maxLength = 100
        )
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        @Size(max = 100, message = "Email must be at most 100 characters")
        private String email;

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

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @Schema(name = "RegisterResponse", description = "The newly created user account (password excluded)")
    public static class RegisterResponse {

        @Schema(
                description = "Database-generated id of the new user",
                example = "42",
                requiredMode = Schema.RequiredMode.REQUIRED,
                accessMode = Schema.AccessMode.READ_ONLY,
                type = "integer",
                format = "int32"
        )
        private final int id;

        @Schema(description = "Username of the new account", example = "jdoe",
                requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
        private final String username;

        @Schema(description = "User's first name", example = "Jane",
                requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
        @JsonProperty("first_name")
        private final String firstName;

        @Schema(description = "User's last name", example = "Doe",
                requiredMode = Schema.RequiredMode.REQUIRED, type = "string")
        @JsonProperty("last_name")
        private final String lastName;

        @Schema(description = "User's email address", example = "jane.doe@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED, type = "string", format = "email")
        private final String email;

        public RegisterResponse(Integer id, String username, String firstName, String lastName, String email) {
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        public Integer getId() { return id; }
        public String getUsername() { return username; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
    }
}