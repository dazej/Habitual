package dazzejpro.habitual.api.repositories;

import dazzejpro.habitual.api.entities.Completion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CompletionRepository extends JpaRepository<Completion, Integer> {

    List<Completion> findByHabit_Id(Integer habitId);

    // All completions for one habit, oldest first (the streak calculation relies on this order)
    List<Completion> findByHabit_IdOrderByCompletionDateAsc(Integer habitId);

    // A habit can only be completed once per date; other habits may use the same date
    boolean existsByHabit_IdAndCompletionDate(Integer habitId, LocalDate completionDate);
}
