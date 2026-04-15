package com.webtruyen.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private Long userId;
    private Long storyId;
    private Long chapterId;
    private Long parentId;
    private String content;
}
