package dazzejpro.habitual.api.service;

import dazzejpro.habitual.api.dtos.StreakDtos.StreakResponse;
import dazzejpro.habitual.api.entities.Completion;
import dazzejpro.habitual.api.entities.Habit;
import dazzejpro.habitual.api.entities.Streak;
import dazzejpro.habitual.api.exception.ApiException;
import dazzejpro.habitual.api.repositories.CompletionRepository;
import dazzejpro.habitual.api.repositories.HabitRepository;
import dazzejpro.habitual.api.repositories.StreakRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class StreakService {

    private final HabitRepository habitRepository;
    private final CompletionRepository completionRepository;
    private final StreakRepository streakRepository;

    public StreakService(HabitRepository habitRepository,
                         CompletionRepository completionRepository,
                         StreakRepository streakRepository) {
        this.habitRepository = habitRepository;
        this.completionRepository = completionRepository;
        this.streakRepository = streakRepository;
    }

    // Streak business logic
    @Transactional
    public StreakResponse streak(int habitId) {

        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Habit not found"
                ));

        List<Completion> completions = completionRepository.findByHabit_IdOrderByCompletionDateAsc(habitId);

        // Nothing logged yet: there is no streak to calculate or store
        if (completions.isEmpty()) {
            return new StreakResponse(habitId, null, false, 0);
        }

        Completion latestCompletion = completions.get(completions.size() - 1);

        int completionId = latestCompletion.getCompletionId();
        LocalDate completionDate = latestCompletion.getCompletionDate();

        int streakCount = streakCalculator(habitId, completionId, completions);
        boolean streakStatus = streakStatus(completionId, completionDate, streakCount);

        // One streak row per habit: update it if it exists, otherwise create it
        Streak streak = streakRepository.findByHabit_Id(habitId)
                .orElseGet(() -> new Streak(habit, completionDate, streakStatus, streakCount));
        streak.setLastCompletionDate(completionDate);
        streak.setStreakStatus(streakStatus);
        streak.setStreakCount(streakCount);

        streakRepository.save(streak);

        return new StreakResponse(
                completionId,
                habitId,
                streak.getLastCompletionDate(),
                streakStatus,
                streakCount
        );
    }


    // Determines if a habit has an active streak
    public boolean streakStatus(int completionid, LocalDate completionDate, Integer streakCount) {
        boolean streakStatus = false;
        if (streakCount > 0) {
            return true;
        }
        return false;
    }


    //Calculates the current streak for a habit
    public int streakCalculator(int habitId, int completionId, List<Completion> completions) {
        int streakCount = 1;
        for (int i = completions.size() - 1; i > 0; i--) {
            if (completions.get(i).getCompletionDate()
                    .minusDays(1)
                    .equals(completions.get(i - 1).getCompletionDate())) {
                streakCount++;
            } else {
                break;

            }
        }
        return streakCount;
    }
}
