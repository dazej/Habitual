package dazzejpro.habitual.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public class StreakDtos {
    // Handles data transfer for streak objects

    @Schema(name = "StreakResponse",  description = "Streak info for a habit")
    public static class StreakResponse {
        @Schema(description = "Id of the completion", example= "1")
        private int completionId;
        @Schema(description = "Id of the habit being checked", example= "1")
        private int habitId;
        @Schema(description = "Date of the habit completion", example = "2026-10-05")
        private LocalDate completionDate;
        @Schema(description = "Streak status of the habit", example = "true")
        private boolean streakStatus;
        @Schema(description = "Count of the habits current streak", example = "1")
        private Integer streakCount;

        public StreakResponse(int completionId, int habitId, LocalDate completionDate, boolean streakStatus, Integer streakCount) {
            this.completionId = completionId;
            this.habitId = habitId;
            this.completionDate = completionDate;
            this.streakStatus = streakStatus;
            this.streakCount = streakCount;
        }

        public StreakResponse(Integer habitId, LocalDate lastCompletionDate, boolean streakStatus, int streakCount) {
            this.habitId = habitId;
            this.completionDate = lastCompletionDate;
            this.streakStatus = streakStatus;
            this.streakCount = streakCount;
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

        public boolean getStreakStatus() {
            return streakStatus; }

        public Integer getStreakCount() {
            return streakCount;
        }
    }
}
