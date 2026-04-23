package com.webtruyen.backend.controller;

import com.webtruyen.backend.dto.PagedResponse;
import com.webtruyen.backend.model.Chapter;
import com.webtruyen.backend.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories/{storyId}/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @GetMapping
    public ResponseEntity<PagedResponse<Chapter>> getChaptersByStoryId(
            @PathVariable Long storyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "asc") String order) {
        
        return ResponseEntity.ok(chapterService.getChaptersPaginated(storyId, page, pageSize, order));
    }

    @GetMapping("/{chapterNumber}")
    public ResponseEntity<Chapter> getChapterByStoryIdAndChapterNumber(
            @PathVariable Long storyId,
            @PathVariable Integer chapterNumber) {
        return ResponseEntity.ok(chapterService.getChapterByStoryIdAndChapterNumber(storyId, chapterNumber));
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Chapter> createChapter(
            @PathVariable Long storyId,
            @RequestBody Chapter chapterRequest) {
        Chapter createdChapter = chapterService.createChapter(storyId, chapterRequest);
        return new ResponseEntity<>(createdChapter, HttpStatus.CREATED);
    }

    @PutMapping("/{chapterId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Chapter> updateChapter(
            @PathVariable Long storyId,
            @PathVariable Long chapterId,
            @RequestBody Chapter chapterDetails) {
        return ResponseEntity.ok(chapterService.updateChapter(chapterId, chapterDetails));
    }

    @DeleteMapping("/{chapterId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteChapter(
            @PathVariable Long storyId,
            @PathVariable Long chapterId) {
        chapterService.deleteChapter(chapterId);
        return ResponseEntity.noContent().build();
    }
}
