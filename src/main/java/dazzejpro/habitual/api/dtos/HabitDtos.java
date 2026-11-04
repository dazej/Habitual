package dazzejpro.habitual.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public class HabitDtos {

    @Schema(description = "Details of a new habit to create for a user. Only habitName is required.")
    public static class CreateHabitRequest {

        @Schema(description = "Name of the habit. Must not be blank.",
                example = "Drink 8 glasses of water",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String habitName;

        @Schema(description = "How often the habit should be done, e.g. daily or weekly. Optional.",
                example = "daily",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String frequency;

        @Schema(description = "Numeric target for the habit. Optional.",
                example = "8",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private Integer goal;

        public String getHabitName() {
            return habitName;
        }
        public void setHabitName(String habitName) {
            this.habitName = habitName;
        }
        public String getFrequency() {
            return frequency;
        }
        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }
        public Integer getGoal() {
            return goal;
        }
        public void setGoal(Integer goal) {
            this.goal = goal;
        }
    }

    @Schema(description = "The habit that was created.")
    public static class CreateHabitResponse {

        @Schema(description = "Unique ID assigned to the new habit", example = "2")
        private final int habitId;

        @Schema(description = "ID of the user who owns the habit", example = "1")
        private final int userId;

        @Schema(description = "Name of the habit", example = "Drink 8 glasses of water")
        private final String habitName;

        @Schema(description = "How often the habit should be done; null if none was provided", example = "daily")
        private final String frequency;

        @Schema(description = "Numeric target for the habit; null if none was provided", example = "8")
        private final Integer goal;

        public CreateHabitResponse(Integer habitId, Integer userId, String habitName, String frequency, Integer goal) {
            this.habitId = habitId;
            this.userId = userId;
            this.habitName = habitName;
            this.frequency = frequency;
            this.goal = goal;
        }

        public int getHabitId() {
            return habitId;
        }
        public int getUserId() {
            return userId;
        }
        public String getHabitName() {
            return habitName;
        }
        public String getFrequency() {
            return frequency;
        }
        public Integer getGoal() {
            return goal;
        }
    }

    // As a user, I want to edit an existing habit so I can change its name, frequency, or goal.
    @Schema(description = "Fields to change on an existing habit. This is a partial update: "
            + "send only the fields you want to change, but at least one is required.")
    public static class UpdateHabitRequest {

        @Schema(description = "New name for the habit. Must not be blank if provided.",
                example = "Get 12k steps a day",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String habitName;

        @Schema(description = "How often the habit should be done. Must not be blank if provided.",
                example = "daily",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String frequency;

        @Schema(description = "Numeric target for the habit. Must be a positive number if provided.",
                example = "12000",
                minimum = "1",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private Integer goal;

        public String getHabitName() {
            return habitName;
        }
        public void setHabitName(String habitName) {
            this.habitName = habitName;
        }
        public String getFrequency() {
            return frequency;
        }
        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }
        public Integer getGoal() {
            return goal;
        }
        public void setGoal(Integer goal) {
            this.goal = goal;
        }
    }

    @Schema(description = "The habit as it looks after the edit was applied.")
    public static class UpdateHabitResponse {

        @Schema(description = "Unique ID of the habit", example = "1")
        private final int habitId;

        @Schema(description = "ID of the user who owns the habit", example = "1")
        private final int userId;

        @Schema(description = "Current name of the habit", example = "Get 12k steps a day")
        private final String habitName;

        @Schema(description = "Current frequency of the habit", example = "daily")
        private final String frequency;

        @Schema(description = "Current numeric goal of the habit", example = "12000")
        private final Integer goal;

        public UpdateHabitResponse(Integer habitId, Integer userId, String habitName, String frequency, Integer goal) {
            this.habitId = habitId;
            this.userId = userId;
            this.habitName = habitName;
            this.frequency = frequency;
            this.goal = goal;
        }

        public int getHabitId() {
            return habitId;
        }
        public int getUserId() {
            return userId;
        }
        public String getHabitName() {
            return habitName;
        }
        public String getFrequency() {
            return frequency;
        }
        public Integer getGoal() {
            return goal;
        }
    }



    // Response returned after successfully pausing a habit.
    @Schema(description = "Response containing the habit information after it has been paused.")
    public static class PauseHabitResponse {

        @Schema(description = "Unique ID of the paused habit", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)

        private final int habitId;

        @Schema(description = "Name of the paused habit", example = "Drink 8 glasses of water",
                requiredMode = Schema.RequiredMode.REQUIRED)

        private final String habitName;

        @Schema(description = "Current status of the habit after pausing", example = "PAUSED",
                allowableValues = {"PAUSED"}, requiredMode = Schema.RequiredMode.REQUIRED)

        private final String status;

        public PauseHabitResponse(Integer habitId, String habitName, String status) {
            this.habitId = habitId;
            this.habitName = habitName;
            this.status = status;
        }

        public int getHabitId() { return habitId; }
        public String getHabitName() { return habitName; }
        public String getStatus() { return status; }
    }
}