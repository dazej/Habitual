package dazzejpro.habitual.api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "habits")
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull
    @Column(name = "habit_name", nullable = false, length = Integer.MAX_VALUE)
    private String habitName;

    // How often the habit should be done, e.g. "daily", "weekly". Free text for now.
    @Column(name = "frequency")
    private String frequency;

    // Numeric target tied to the habit, e.g. a step count or a weekly count goal.
    @Column(name = "goal")
    private Integer goal;

    @OneToMany(mappedBy = "habit")
    private Set<Completion> completions = new LinkedHashSet<>();

    @OneToOne(mappedBy = "habit")
    private Streak streak;

    @NotNull
    @Column(name = "habit_status", nullable = false)
    private Integer habitStatus = HabitStatus.ACTIVE.getCode();

    public Integer getHabitStatus() {
        return habitStatus;
    }

    public void setHabitStatus(Integer habitStatus) {
        this.habitStatus = habitStatus;
    }

    public Integer getHabitId() {
        return id;
    }

    public void setHabitId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public Set<Completion> getCompletions() {
        return completions;
    }

    public void setCompletions(Set<Completion> completions) {
        this.completions = completions;
    }

    public Streak getStreak() {
        return streak;
    }

    public void setStreak(Streak streak) {
        this.streak = streak;
    }

    protected Habit() {
    }

    public Habit(Integer id, User user, Category category, String habitName, String frequency, Integer goal) {
        this.id = id;
        this.user = user;
        this.category = category;
        this.habitName = habitName;
        this.frequency = frequency;
        this.goal = goal;
        this.habitStatus = HabitStatus.ACTIVE.getCode();
    }
}
