package com.webtruyen.backend.controller;

import com.webtruyen.backend.dto.CommentRequest;
import com.webtruyen.backend.model.Comment;
import com.webtruyen.backend.model.User;
import com.webtruyen.backend.repository.CommentRepository;
import com.webtruyen.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.webtruyen.backend.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<PagedResponse<Comment>> getComments(
            @RequestParam(required = false) Long storyId,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Comment> commentPage;

        if (chapterId != null) {
            commentPage = commentRepository.findByChapterIdOrderByCreatedAtAsc(chapterId, pageable);
        } else if (storyId != null) {
            commentPage = commentRepository.findByStoryIdOrderByCreatedAtAsc(storyId, pageable);
        } else {
            // General admin view, show newest first
            pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
            commentPage = commentRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        PagedResponse<Comment> response = new PagedResponse<>(
                commentPage.getContent(),
                commentPage.getTotalElements(),
                page,
                pageSize
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = Comment.builder()
                .user(user)
                .storyId(request.getStoryId())
                .chapterId(request.getChapterId())
                .parentId(request.getParentId())
                .content(request.getContent())
                .build();

        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            commentRepository.delete(comment.get());
            // Optionally: delete all replies where parentId = id here.
            return ResponseEntity.ok("Comment deleted");
        }
        return ResponseEntity.notFound().build();
    }
}
