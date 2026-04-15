package com.webtruyen.backend.repository;

import com.webtruyen.backend.model.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Page<Chapter> findByStoryId(Long storyId, Pageable pageable);
    Optional<Chapter> findByStoryIdAndChapterNumber(Long storyId, Integer chapterNumber);
}
