package com.project.thevergov.model.dto;

import com.project.thevergov.model.enums.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for querying articles.
 * <p>
 * This class is used to transfer data when querying articles from the database.
 */
@Getter
@Setter
public class ArticleResponse {

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


}

