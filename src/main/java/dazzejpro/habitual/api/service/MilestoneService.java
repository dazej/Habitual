package dazzejpro.habitual.api.service;

import dazzejpro.habitual.api.dtos.MilestoneDtos.MilestoneShareResponse;
import dazzejpro.habitual.api.entities.Streak;
import dazzejpro.habitual.api.exception.ApiException;
import dazzejpro.habitual.api.repositories.HabitRepository;
import dazzejpro.habitual.api.repositories.StreakRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MilestoneService {

    private final HabitRepository habitRepository;
    private final StreakRepository streakRepository;

    public MilestoneService(HabitRepository habitRepository, StreakRepository streakRepository) {
        this.habitRepository = habitRepository;
        this.streakRepository = streakRepository;
    }

    @Transactional(readOnly = true)
    public MilestoneShareResponse shareMilestone(int habitId) {
        if (!habitRepository.existsById(habitId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Habit not found");
        }

        Streak streak = streakRepository.findByHabit_Id(habitId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Streak not found"));

        int streakCount = streak.getStreakCount();

        boolean milestoneReached =
                streakCount == 10 ||
                        streakCount == 50 ||
                        streakCount == 100;

        if (!milestoneReached) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Shareable milestone has not been reached"
            );
        }
        String message =
                "Congratulations! You reached a " +
                        streakCount +
                        "-day streak!";

        String shareText =
                "I just reached a " +
                        streakCount +
                        "-day streak on Habitual!";

        return new MilestoneShareResponse(
                habitId,
                streakCount,
                true,
                message,
                shareText
        );
    }
}
