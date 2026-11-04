package dazzejpro.habitual.api.repositories;

import dazzejpro.habitual.api.entities.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitRepository extends JpaRepository<Habit, Integer> {
}