package federicolepore.backend.repositories;

import federicolepore.backend.entities.Category;
import federicolepore.backend.entities.Skill;
import federicolepore.backend.entities.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    List<Skill> findByType(Type type);

    List<Skill> findByNormalizedTitle(String title);

    List<Skill> findByCategory(Category category);

    List<Skill> findByUserIdAndCategory(UUID userId, Category category);

    List<Skill> findByUserIdAndType(UUID userId, Type type);

    List<Skill> findByUserIdAndCategoryAndType(UUID userId, Category category, Type type);

    @Query("""
            SELECT s
            FROM Skill s
            WHERE s.user.id <> :loggedUserId
            AND (:q = '' OR s.normalizedTitle LIKE CONCAT('%', :q, '%'))
            """)
    Page<Skill> searchOthersByTitle(@Param("q") String normalizedQuery,
                                    @Param("loggedUserId") UUID loggedUserId,
                                    Pageable pageable);

}
