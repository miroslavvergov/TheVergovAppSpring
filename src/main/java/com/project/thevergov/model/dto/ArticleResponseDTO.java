package com.project.thevergov.model.dto;

import com.project.thevergov.model.enums.Category;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for querying articles.
 * <p>
 * This class is used to transfer data when querying articles from the database.
 */
@Getter
public class ArticleResponseDTO {

    private Long id;

    private String title;

    private String content;

    private String authorName;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private Set<Category> categories;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
}

