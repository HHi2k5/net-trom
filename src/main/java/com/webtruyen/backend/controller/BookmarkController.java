package com.webtruyen.backend.controller;

import com.webtruyen.backend.model.Chapter;
import com.webtruyen.backend.model.ChapterBookmark;
import com.webtruyen.backend.model.User;
import com.webtruyen.backend.repository.BookmarkRepository;
import com.webtruyen.backend.repository.ChapterRepository;
import com.webtruyen.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChapterBookmark>> getUserBookmarks(@PathVariable Long userId) {
        return ResponseEntity.ok(bookmarkRepository.findByUserId(userId));
    }

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleBookmark(@RequestParam Long userId, @RequestParam Long chapterId) {
        Optional<ChapterBookmark> existing = bookmarkRepository.findByUserIdAndChapterId(userId, chapterId);

        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return ResponseEntity.ok("Bookmark removed");
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            ChapterBookmark bookmark = ChapterBookmark.builder()
                    .user(user)
                    .chapter(chapter)
                    .build();

            bookmarkRepository.save(bookmark);
            return ResponseEntity.ok("Bookmark added");
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isBookmarked(@RequestParam Long userId, @RequestParam Long chapterId) {
        return ResponseEntity.ok(bookmarkRepository.existsByUserIdAndChapterId(userId, chapterId));
    }
}
