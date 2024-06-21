package com.project.thevergov.domain.dto;

import com.project.thevergov.enumeration.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Data Transfer Object for creating and updating articles.
 * <p>
 * This DTO is used to receive article data from client requests.
 */
@Getter
@Setter
public class ArticleCreation {

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotBlank
    private String authorEmail;


    private Set<Category> categories;


}

