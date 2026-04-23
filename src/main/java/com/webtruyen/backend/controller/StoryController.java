package com.webtruyen.backend.controller;

import com.webtruyen.backend.dto.PagedResponse;
import com.webtruyen.backend.model.Story;
import com.webtruyen.backend.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @GetMapping
    public ResponseEntity<PagedResponse<Story>> getAllStories(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        
        return ResponseEntity.ok(storyService.getStoriesPaginated(q, categoryId, status, author, page, pageSize, sortBy, order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Story> getStoryById(@PathVariable Long id) {
        return ResponseEntity.ok(storyService.getStoryById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Story> getStoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(storyService.getStoryBySlug(slug));
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Story> createStory(@RequestBody Story story) {
        Story createdStory = storyService.createStory(story);
        return new ResponseEntity<>(createdStory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Story> updateStory(@PathVariable Long id, @RequestBody Story storyDetails) {
        return ResponseEntity.ok(storyService.updateStory(id, storyDetails));
    }

    @PatchMapping("/{id}/views")
    public ResponseEntity<Void> incrementViews(@PathVariable Long id) {
        storyService.incrementViews(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
        storyService.deleteStory(id);
        return ResponseEntity.noContent().build();
    }
}
