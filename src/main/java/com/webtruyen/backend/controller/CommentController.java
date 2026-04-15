package com.webtruyen.backend.controller;

import com.webtruyen.backend.dto.CommentRequest;
import com.webtruyen.backend.model.Comment;
import com.webtruyen.backend.model.User;
import com.webtruyen.backend.repository.CommentRepository;
import com.webtruyen.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(
            @RequestParam(required = false) Long storyId,
            @RequestParam(required = false) Long chapterId) {
        
        if (chapterId != null) {
            return ResponseEntity.ok(commentRepository.findByChapterIdOrderByCreatedAtAsc(chapterId));
        } else if (storyId != null) {
            return ResponseEntity.ok(commentRepository.findByStoryIdOrderByCreatedAtAsc(storyId));
        }
        return ResponseEntity.ok(commentRepository.findAllByOrderByCreatedAtDesc());
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
