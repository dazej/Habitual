package dazzejpro.habitual.api.repositories;

import dazzejpro.habitual.api.entities.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreakRepository extends JpaRepository<Streak, Integer> {

    // Each habit has at most one streak row, which is updated in place
    Optional<Streak> findByHabit_Id(Integer habitId);
}
