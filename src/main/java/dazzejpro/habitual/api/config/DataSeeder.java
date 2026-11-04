package dazzejpro.habitual.api.config;

import dazzejpro.habitual.api.entities.Category;
import dazzejpro.habitual.api.entities.Completion;
import dazzejpro.habitual.api.entities.Habit;
import dazzejpro.habitual.api.entities.HabitStatus;
import dazzejpro.habitual.api.entities.User;
import dazzejpro.habitual.api.repositories.CategoryRepository;
import dazzejpro.habitual.api.repositories.CompletionRepository;
import dazzejpro.habitual.api.repositories.HabitRepository;
import dazzejpro.habitual.api.repositories.UserRepository;
import dazzejpro.habitual.api.security.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fills an empty database when the app starts.
 *
 * 1. Categories (Health, Fitness, Productivity) are always created when the categories table is empty,
 *    because the "assign category" endpoint needs them.
 * 2. Demo users, habits and completions are created only when the users table is empty and
 *    habitual.seed.demo-data is true (the default). Set it to false in application.properties
 *    to start with empty tables.
 *
 * Everything runs in one transaction: if anything fails, nothing is saved and the app keeps running.
 * The demo accounts and their passwords are listed in the README.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final CompletionRepository completionRepository;
    private final PasswordHasher passwordHasher;
    private final PlatformTransactionManager transactionManager;
    private final boolean demoDataEnabled;

    public DataSeeder(CategoryRepository categoryRepository,
                      UserRepository userRepository,
                      HabitRepository habitRepository,
                      CompletionRepository completionRepository,
                      PasswordHasher passwordHasher,
                      PlatformTransactionManager transactionManager,
                      @Value("${habitual.seed.demo-data:true}") boolean demoDataEnabled) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
        this.completionRepository = completionRepository;
        this.passwordHasher = passwordHasher;
        this.transactionManager = transactionManager;
        this.demoDataEnabled = demoDataEnabled;
    }

    // ---------------------------------------------------------------------------------------------
    // Demo data. Fixed on purpose (not random at runtime) so everyone on the team sees the same
    // accounts, and the README can list them.
    //
    // A habit counts as "complete" when its status is COMPLETED and "incomplete" when it is ACTIVE.
    // Completion dates are given as "days ago" so the streaks always look current.
    // ---------------------------------------------------------------------------------------------

    private record DemoHabit(String name, String category, String frequency, Integer goal,
                             HabitStatus status, int[] completedDaysAgo) {
    }

    private record DemoUser(String username, String firstName, String lastName, String email,
                            String password, List<DemoHabit> habits) {
    }

    private static final List<DemoUser> DEMO_USERS = List.of(
            new DemoUser("mayaokafor", "Maya", "Okafor", "maya.okafor@example.com", "Sunrise#2291", List.of(
                    // incomplete: 5-day streak in progress
                    new DemoHabit("Morning run", "Fitness", "daily", 1, HabitStatus.ACTIVE,
                            new int[]{5, 4, 3, 2, 1}),
                    // incomplete: nothing logged yet
                    new DemoHabit("Drink 8 glasses of water", "Health", "daily", 8, HabitStatus.ACTIVE,
                            new int[]{}),
                    // complete: finished a 7-day run earlier
                    new DemoHabit("Read 20 pages", "Productivity", "daily", 20, HabitStatus.COMPLETED,
                            new int[]{14, 13, 12, 11, 10, 9, 8}))),

            new DemoUser("liambrennan", "Liam", "Brennan", "liam.brennan@example.com", "Tidal*Wave77", List.of(
                    // incomplete: 4-day streak including today
                    new DemoHabit("Meditate for 10 minutes", "Health", "daily", 10, HabitStatus.ACTIVE,
                            new int[]{3, 2, 1, 0}),
                    // complete: weekly habit, one completion per week (so the day-streak is 1)
                    new DemoHabit("Weekly meal prep", "Health", "weekly", 1, HabitStatus.COMPLETED,
                            new int[]{21, 14, 7}))),

            new DemoUser("priyanair", "Priya", "Nair", "priya.nair@example.com", "Quartz&Fern19", List.of(
                    // incomplete: exactly 10 days in a row, so the "share milestone" endpoint works for it
                    new DemoHabit("Study Java for 30 minutes", "Productivity", "daily", 30, HabitStatus.ACTIVE,
                            new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0}))),

            new DemoUser("diegoalvarez", "Diego", "Alvarez", "diego.alvarez@example.com", "Ember_Lake58", List.of(
                    // incomplete: nothing logged yet
                    new DemoHabit("Stretch before bed", "Health", "daily", 1, HabitStatus.ACTIVE,
                            new int[]{}),
                    // incomplete: no category, and a broken streak (gap between day 4 and day 1)
                    new DemoHabit("Practice guitar", null, "daily", 30, HabitStatus.ACTIVE,
                            new int[]{5, 4, 1})))
    );

    @Override
    public void run(String... args) {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        try {
            transaction.executeWithoutResult(status -> {
                seedCategories();
                if (demoDataEnabled) {
                    seedDemoData();
                }
            });
        } catch (RuntimeException e) {
            log.error("Seeding failed and was rolled back. The app will keep running without the seed data.", e);
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            return;
        }
        for (String name : List.of("Health", "Fitness", "Productivity")) {
            Category category = new Category();
            category.setName(name);
            categoryRepository.save(category);
        }
        log.info("Seeded 3 categories (Health, Fitness, Productivity)");
    }

    private void seedDemoData() {
        if (userRepository.count() > 0) {
            log.info("Demo data skipped: the users table already has data");
            return;
        }

        Map<String, Category> categories = new HashMap<>();
        for (Category category : categoryRepository.findAll()) {
            categories.put(category.getName(), category);
        }

        LocalDate today = LocalDate.now();
        int habitCount = 0;
        int completionCount = 0;

        for (DemoUser demoUser : DEMO_USERS) {
            // Ids are left empty on purpose: the database generates them on save
            User user = new User();
            user.setUsername(demoUser.username());
            user.setFirstName(demoUser.firstName());
            user.setLastName(demoUser.lastName());
            user.setEmail(demoUser.email());
            user.setPasswordHash(passwordHasher.hash(demoUser.password()));
            user = userRepository.save(user);

            for (DemoHabit demoHabit : demoUser.habits()) {
                Habit habit = new Habit(null, user, categories.get(demoHabit.category()),
                        demoHabit.name(), demoHabit.frequency(), demoHabit.goal());
                habit.setHabitStatus(demoHabit.status().getCode());
                habit = habitRepository.save(habit);
                habitCount++;

                for (int daysAgo : demoHabit.completedDaysAgo()) {
                    completionRepository.save(new Completion(null, habit, today.minusDays(daysAgo)));
                    completionCount++;
                }
            }
        }

        log.info("Seeded demo data: {} users, {} habits, {} completions", DEMO_USERS.size(), habitCount, completionCount);
    }
}
