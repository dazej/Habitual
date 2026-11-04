package dazzejpro.habitual.api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "streaks")
public class Streak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "last_completion_date")
    private LocalDate lastCompletionDate;

    @NotNull
    @Column(name = "streak_status", nullable = false)
    private boolean streakStatus = false;

    @NotNull
    @Column(name = "streak_count", nullable = false)
    private Integer streakCount;

    public Integer getStreakId() {
        return id;
    }

    public void setStreakId(Integer id) {
        this.id = id;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public LocalDate getLastCompletionDate() {
        return lastCompletionDate;
    }

    public void setLastCompletionDate(LocalDate lastCompletionDate) {
        this.lastCompletionDate = lastCompletionDate;
    }

    public Boolean getStreakStatus() {
        return streakStatus;
    }

    public void setStreakStatus(Boolean streakStatus) {
        this.streakStatus = streakStatus;
    }

    public Integer getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(Integer streakCount) {
        this.streakCount = streakCount;
    }

    protected Streak() {
    }

    public Streak(Habit habit, LocalDate completionDate, boolean streakStatus, int streakCount) {
        this.habit = habit;
        this.lastCompletionDate = completionDate;
        this.streakStatus = streakStatus;
        this.streakCount = streakCount;
    }
}