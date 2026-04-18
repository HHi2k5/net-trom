package com.webtruyen.backend.repository;

import com.webtruyen.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByStoryIdOrderByCreatedAtAsc(Long storyId, Pageable pageable);
    Page<Comment> findByChapterIdOrderByCreatedAtAsc(Long chapterId, Pageable pageable);
    Page<Comment> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
