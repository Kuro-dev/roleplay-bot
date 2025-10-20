package org.kurodev.rp.db.repository;

import org.kurodev.rp.db.entity.OriginalCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OriginalCharacterRepository extends JpaRepository<OriginalCharacter, Long> {
    List<OriginalCharacter> findAllByUserId(Long userId);

    @Query("SELECT c FROM OriginalCharacter c " +
            "WHERE c.userId = :userId AND " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<OriginalCharacter> findByUserIdAndNameLike(@Param("userId") Long userId,
                                                    @Param("name") String name);

    Optional<OriginalCharacter> findByUserIdAndNameEquals(Long userId, String name);

    void deleteAllByUserId(Long userId);
}
