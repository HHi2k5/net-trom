package com.webtruyen.backend.repository;

import com.webtruyen.backend.model.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    Optional<Story> findBySlug(String slug);

    @Query("SELECT DISTINCT s FROM Story s LEFT JOIN s.categories c " +
           "WHERE (:q IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:status IS NULL OR s.status = :status) " +
           "AND (:author IS NULL OR LOWER(s.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
           "AND (:categoryId IS NULL OR c.id = :categoryId)")
    Page<Story> findStoriesDynamically(
            @Param("q") String q,
            @Param("status") String status,
            @Param("author") String author,
            @Param("categoryId") Long categoryId,
            Pageable pageable);
}
