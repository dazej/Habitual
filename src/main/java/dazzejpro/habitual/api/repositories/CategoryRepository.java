package dazzejpro.habitual.api.repositories;

import dazzejpro.habitual.api.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}