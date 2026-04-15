package com.webtruyen.backend.service;

import com.webtruyen.backend.dto.PagedResponse;
import com.webtruyen.backend.exception.ResourceNotFoundException;
import com.webtruyen.backend.model.Chapter;
import com.webtruyen.backend.model.Story;
import com.webtruyen.backend.repository.ChapterRepository;
import com.webtruyen.backend.repository.StoryRepository;
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
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;

    public PagedResponse<Chapter> getChaptersPaginated(Long storyId, int page, int pageSize, String order) {
        if (!storyRepository.existsById(storyId)) {
            throw new ResourceNotFoundException("Story not found with id: " + storyId);
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(direction, "chapterNumber"));

        Page<Chapter> pagedResult = chapterRepository.findByStoryId(storyId, pageable);

        return new PagedResponse<>(
                pagedResult.getContent(),
                pagedResult.getTotalElements(),
                page,
                pageSize
        );
    }

    public Chapter getChapterByStoryIdAndChapterNumber(Long storyId, Integer chapterNumber) {
        return chapterRepository.findByStoryIdAndChapterNumber(storyId, chapterNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter " + chapterNumber + " not found for story id: " + storyId));
    }

    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + id));
    }

    public Chapter createChapter(Long storyId, Chapter chapterRequest) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));
        
        chapterRequest.setStory(story);
        log.info("Creating chapter with {} pages for story ID: {}", 
                 chapterRequest.getPages() != null ? chapterRequest.getPages().size() : 0, storyId);
        return chapterRepository.save(chapterRequest);
    }

    public Chapter updateChapter(Long id, Chapter chapterDetails) {
        Chapter chapter = getChapterById(id);
        if(chapterDetails.getChapterNumber() != null) chapter.setChapterNumber(chapterDetails.getChapterNumber());
        if(chapterDetails.getTitle() != null) chapter.setTitle(chapterDetails.getTitle());
        if(chapterDetails.getPages() != null) chapter.setPages(chapterDetails.getPages());
        log.info("Updating chapter ID: {} with {} pages", 
                 id, chapter.getPages() != null ? chapter.getPages().size() : 0);
        return chapterRepository.save(chapter);
    }

    public void deleteChapter(Long id) {
        Chapter chapter = getChapterById(id);
        chapterRepository.delete(chapter);
    }
}
