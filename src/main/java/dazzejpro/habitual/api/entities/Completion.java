package dazzejpro.habitual.api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "completions")
public class Completion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @NotNull
    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    protected Completion() {
    }

    public Completion(Integer id, Habit habit, LocalDate completionDate) {
        this.id = id;
        this.habit = habit;
        this.completionDate = completionDate;
    }

    public Integer getCompletionId() {
        return id;
    }

    public void setCompletionId(Integer id) {
        this.id = id;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

}