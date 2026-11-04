package dazzejpro.habitual.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public class MilestoneDtos {

    @Schema(name = "MilestoneShareResponse",
            description = "A shareable message for a habit whose streak has reached a milestone")
    public static class MilestoneShareResponse{

        @Schema(description = "ID of the habit the milestone belongs to", example = "1")
        private final int habitId;

        @Schema(description = "Current streak length in days. A successful response always has a streak of 10, 50 or 100.",
                example = "10")
        private final int streakCount;

        @Schema(description = "Whether a shareable milestone was reached. Always true in a successful response.",
                example = "true")
        private final boolean milestoneReached;

        @Schema(description = "Congratulation message for the user",
                example = "Congratulations! You reached a 10-day streak!")
        private final String message;

        @Schema(description = "Ready-to-post text the user can share",
                example = "I just reached a 10-day streak on Habitual!")
        private final String shareText;

        public MilestoneShareResponse(
                int habitId,
                int streakCount,
                boolean milestoneReached,
                String messsage,
                String shareText) {
            this.habitId = habitId;
            this.streakCount = streakCount;
            this.milestoneReached = milestoneReached;
            this.message = messsage;
            this.shareText = shareText;
        }
        public int getHabitId() {
            return habitId;
        }
        public int getStreakCount() {
            return streakCount;
        }
        public boolean isMilestoneReached() {
            return milestoneReached;
        }
        public String getMessage() {
            return message;
        }
        public String getShareText() {
            return shareText;
        }
    }
}
