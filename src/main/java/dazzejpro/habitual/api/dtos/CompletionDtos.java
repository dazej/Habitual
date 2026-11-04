package dazzejpro.habitual.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class CompletionDtos {
    // Handles data transfer for completion objects
    @Schema(name = "CompletionRequest",  description = "Request body for habit completion")
    public static class CompletionRequest {
        @Schema(description = "Date of the habit completion", example = "2026-10-05",
                requiredMode = Schema.RequiredMode.REQUIRED)
        public LocalDate completionDate;

        public LocalDate getCompletionDate() {
                return completionDate;
            }
    }
        public static class CompletionResponse {
            @Schema(description = "Id of completion", example = "1")
            public int completionId;
            @Schema(description = "Id of the habit that was completed", example = "1")
            public int habitId;
            @Schema(description = "Date of the habit completion", example = "2026-10-05")
            public LocalDate completionDate;

            public CompletionResponse(Integer completionId, Integer habitId, LocalDate completionDate) {
                this.completionId = completionId;
                this.habitId = habitId;
                this.completionDate = completionDate;
            }


            public int getCompletionId() {
                return completionId;
            }

            public int getHabitId() {
                return habitId;
            }

            public LocalDate getCompletionDate() {
                return completionDate;
            }
        }
    }


