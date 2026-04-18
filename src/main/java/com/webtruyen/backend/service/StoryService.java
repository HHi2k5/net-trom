package com.webtruyen.backend.service;

import com.webtruyen.backend.dto.PagedResponse;
import com.webtruyen.backend.exception.ResourceNotFoundException;
import com.webtruyen.backend.model.Story;
import com.webtruyen.backend.repository.StoryRepository;
import com.webtruyen.backend.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoryService {

    private final StoryRepository storyRepository;

    public PagedResponse<Story> getStoriesPaginated(String q, Long categoryId, String status, String author,
                                                    int page, int pageSize, String sortBy, String order) {

        if (q != null && q.trim().isEmpty()) q = null;
        if (status != null && status.trim().isEmpty()) status = null;
        if (author != null && author.trim().isEmpty()) author = null;

        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(direction, sortBy));

        Page<Story> pagedResult = storyRepository.findStoriesDynamically(q, status, author, categoryId, pageable);

        return new PagedResponse<>(
                pagedResult.getContent(),
                pagedResult.getTotalElements(),
                page,
                pageSize
        );
    }

    public Story getStoryById(Long id) {
        return storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + id));
    }

    public Story getStoryBySlug(String slug) {
        return storyRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with slug: " + slug));
    }

    public Story createStory(Story story) {
        if (story.getSlug() == null || story.getSlug().isEmpty()) {
            story.setSlug(generateUniqueSlug(story.getTitle()));
        }
        log.info("Creating story with cover image: {}", story.getCoverImage());
        return storyRepository.save(story);
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = SlugUtils.makeSlug(title);
        String slug = baseSlug;
        int count = 1;
        while (storyRepository.findBySlug(slug).isPresent()) {
            slug = baseSlug + "-" + count++;
        }
        return slug;
    }

    public Story updateStory(Long id, Story storyDetails) {
        Story story = getStoryById(id);
        if(storyDetails.getTitle() != null) story.setTitle(storyDetails.getTitle());
        
        // If slug is explicitly provided, use it. Otherwise, if the story currently has no slug, generate one.
        if (storyDetails.getSlug() != null && !storyDetails.getSlug().isEmpty()) {
            story.setSlug(storyDetails.getSlug());
        } else if (story.getSlug() == null || story.getSlug().isEmpty()) {
            story.setSlug(generateUniqueSlug(story.getTitle()));
        }

        if(storyDetails.getAuthor() != null) story.setAuthor(storyDetails.getAuthor());
        if(storyDetails.getDescription() != null) story.setDescription(storyDetails.getDescription());
        if(storyDetails.getCoverImage() != null) story.setCoverImage(storyDetails.getCoverImage());
        if(storyDetails.getStatus() != null) story.setStatus(storyDetails.getStatus());
        if(storyDetails.getCategories() != null) story.setCategories(storyDetails.getCategories());
        log.info("Updating story ID: {} with cover image: {}", id, story.getCoverImage());
        return storyRepository.save(story);
    }

    public void incrementViews(Long id) {
        Story story = getStoryById(id);
        story.setViews(story.getViews() + 1);
        storyRepository.save(story);
    }

    public void deleteStory(Long id) {
        Story story = getStoryById(id);
        storyRepository.delete(story);
    }
}
