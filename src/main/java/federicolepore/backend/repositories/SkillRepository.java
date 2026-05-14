package federicolepore.backend.repositories;

import federicolepore.backend.entities.Category;
import federicolepore.backend.entities.Skill;
import federicolepore.backend.entities.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    Optional<Skill> findByType(Type type);

    Optional<Skill> findByNormalizedTitle(String title);

    Optional<Skill> findByCategory(Category category);
}
