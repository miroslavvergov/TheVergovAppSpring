package com.project.thevergov.model.dto;

import com.project.thevergov.model.enums.Category;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object for creating and updating articles.
 * <p>
 * This DTO is used to receive article data from client requests.
 */
@Getter
public class CreationArticleDTO {

    private String title;
    private String content;
    private UUID authorId;
    private Set<Category> categories;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
}

