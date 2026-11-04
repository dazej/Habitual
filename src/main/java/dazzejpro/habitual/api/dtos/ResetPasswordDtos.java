package dazzejpro.habitual.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public class ResetPasswordDtos {

    @Schema(name = "ResetPasswordRequest", description = "Payload for resetting an account's password")
    public static class ResetPasswordRequest {

        @Schema(description = "Email address of the account whose password is being reset",
                example = "jane.doe@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "string")
        private String email;

        @Schema(description = "The new password to set on the account. Must not be blank.",
                example = "N3wSecur3Pass!",
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "string")
        private String newPassword;

        public ResetPasswordRequest(){
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getNewPassword() {
            return newPassword;
        }
        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    @Schema(name = "ResetPasswordResponse", description = "Confirmation that the password was reset")
    public static class ResetPasswordResponse {

        @Schema(description = "Result of the reset", example = "Password reset successful")
        private String message;

        public ResetPasswordResponse(String message){
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
}
