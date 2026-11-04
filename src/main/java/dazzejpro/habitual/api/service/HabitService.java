package dazzejpro.habitual.api.service;

import dazzejpro.habitual.api.dtos.AssignCategoryResponse;
import dazzejpro.habitual.api.dtos.HabitDtos;
import dazzejpro.habitual.api.dtos.HabitDtos.CreateHabitRequest;
import dazzejpro.habitual.api.dtos.HabitDtos.CreateHabitResponse;
import dazzejpro.habitual.api.dtos.HabitDtos.UpdateHabitRequest;
import dazzejpro.habitual.api.dtos.HabitDtos.UpdateHabitResponse;
import dazzejpro.habitual.api.entities.Category;
import dazzejpro.habitual.api.entities.Habit;
import dazzejpro.habitual.api.entities.HabitStatus;
import dazzejpro.habitual.api.entities.User;
import dazzejpro.habitual.api.exception.ApiException;
import dazzejpro.habitual.api.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CompletionRepository completionRepository;
    private final StreakRepository streakRepository;

    public HabitService(HabitRepository habitRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository,
                        CompletionRepository completionRepository,
                        StreakRepository streakRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.completionRepository = completionRepository;
        this.streakRepository = streakRepository;
    }

    //Create a new habit for an exisiting user
    @Transactional
    public CreateHabitResponse createHabit(int userId, CreateHabitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (request == null || isBlank(request.getHabitName())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid/missing field");
        }

        // The id is left empty on purpose: the database generates it when the habit is saved
        Habit habit = new Habit(null, user, null, request.getHabitName(), request.getFrequency(), request.getGoal());

        habit = habitRepository.save(habit);

        return new CreateHabitResponse(
                habit.getHabitId(),
                habit.getUser().getUserId(),
                habit.getHabitName(),
                habit.getFrequency(),
                habit.getGoal()
        );
    }

    // As a user, I want to pause a habit without deleting its history.
    @Transactional
    public HabitDtos.PauseHabitResponse pauseHabit(Integer habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Habit not found"));

        if (habit.getHabitStatus() == HabitStatus.PAUSED.getCode()) {
            throw new ApiException(HttpStatus.CONFLICT, "Habit is already paused");
        }

        habit.setHabitStatus(HabitStatus.PAUSED.getCode());
        habitRepository.save(habit);

        return new HabitDtos.PauseHabitResponse(
                habit.getHabitId(),
                habit.getHabitName(),
                HabitStatus.PAUSED.name()
        );
    }

    // As a user, I want to edit an existing habit so I can change its name, frequency, or goal.
    @Transactional
    public UpdateHabitResponse updateHabit(int habitId, UpdateHabitRequest request) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Habit not found"));

        if (request == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Request body is required");
        }

        // Partial update: only fields the client actually sent get changed.
        // Untouched fields keep their current value.
        boolean nameProvided = request.getHabitName() != null;
        boolean frequencyProvided = request.getFrequency() != null;
        boolean goalProvided = request.getGoal() != null;

        if (!nameProvided && !frequencyProvided && !goalProvided) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "At least one field (habitName, frequency, goal) is required");
        }

        // Validate everything first so a bad field never leaves a half-applied edit behind
        if (nameProvided && isBlank(request.getHabitName())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Habit name cannot be blank");
        }
        if (frequencyProvided && isBlank(request.getFrequency())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Frequency cannot be blank");
        }
        if (goalProvided && request.getGoal() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Goal must be a positive number");
        }

        if (nameProvided) {
            habit.setHabitName(request.getHabitName());
        }
        if (frequencyProvided) {
            habit.setFrequency(request.getFrequency());
        }
        if (goalProvided) {
            habit.setGoal(request.getGoal());
        }

        habitRepository.save(habit);

        return new UpdateHabitResponse(
                habit.getHabitId(),
                habit.getUser().getUserId(),
                habit.getHabitName(),
                habit.getFrequency(),
                habit.getGoal()
        );
    }

    // Deletes an existing habit together with its completions and streak
    @Transactional
    public void deleteHabit(Integer habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Habit not found"));

        // Completions and the streak point at the habit, so they have to be removed first
        completionRepository.deleteAll(completionRepository.findByHabit_Id(habitId));
        streakRepository.findByHabit_Id(habitId).ifPresent(streakRepository::delete);

        habitRepository.delete(habit);
    }
    //creates the response for assigning a category to a habit
    @Transactional
    public AssignCategoryResponse assignCategory(int habitId, int categoryId) {

        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Habit not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));

        habit.setCategory(category);
        habitRepository.save(habit);

        return new AssignCategoryResponse(
                habit.getHabitId(),
                category.getCategoryId(),
                category.getName()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
