package dazzejpro.habitual.api.service;

import dazzejpro.habitual.api.dtos.CompletionDtos.CompletionRequest;
import dazzejpro.habitual.api.dtos.CompletionDtos.CompletionResponse;
import dazzejpro.habitual.api.entities.Completion;
import dazzejpro.habitual.api.entities.Habit;
import dazzejpro.habitual.api.entities.HabitStatus;
import dazzejpro.habitual.api.exception.ApiException;
import dazzejpro.habitual.api.repositories.CompletionRepository;
import dazzejpro.habitual.api.repositories.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompletionService {

    private final HabitRepository habitRepository;
    private final CompletionRepository completionRepository;

    public CompletionService(HabitRepository habitRepository, CompletionRepository completionRepository) {
        this.habitRepository = habitRepository;
        this.completionRepository = completionRepository;
    }

    // Completion business logic
    @Transactional
    public CompletionResponse completion(int habitId, CompletionRequest request) {

        if (request == null || request.getCompletionDate() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Completion date is required"
            );
        }

        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Habit not found"
                ));

        // Only the same habit on the same date counts as a duplicate
        if (completionRepository.existsByHabit_IdAndCompletionDate(habitId, request.getCompletionDate())) {
            throw new ApiException(HttpStatus.CONFLICT, "duplicate completion"
            );
        }

        if (habit.getHabitStatus() == HabitStatus.PAUSED.getCode()) {
            throw new ApiException(HttpStatus.CONFLICT, "Habit is Paused");
        }

        // The id is left empty on purpose: the database generates it when the completion is saved
        Completion completion = completionRepository.save(
                new Completion(null, habit, request.getCompletionDate())
        );

        return new CompletionResponse(
                completion.getCompletionId(),
                habit.getHabitId(),
                completion.getCompletionDate()
        );
    }

    // Returns every completion logged for a habit, oldest first
    @Transactional(readOnly = true)
    public List<CompletionResponse> getCompletionHistory(int habitId) {

        if (!habitRepository.existsById(habitId)) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    "Habit not found"
            );
        }

        return completionRepository.findByHabit_IdOrderByCompletionDateAsc(habitId)
                .stream()
                .map(completion -> new CompletionResponse(
                        completion.getCompletionId(),
                        habitId,
                        completion.getCompletionDate()
                ))
                .toList();
    }

}
