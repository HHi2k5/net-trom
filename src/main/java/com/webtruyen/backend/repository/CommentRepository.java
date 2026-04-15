package com.webtruyen.backend.repository;

import com.webtruyen.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStoryIdOrderByCreatedAtAsc(Long storyId);
    List<Comment> findByChapterIdOrderByCreatedAtAsc(Long chapterId);
    List<Comment> findAllByOrderByCreatedAtDesc();
}
