package com.project.thevergov.service;

import com.project.thevergov.domain.dto.ArticleResponse;
import com.project.thevergov.domain.dto.ArticleCreation;
import com.project.thevergov.entity.Article;
import com.project.thevergov.enumeration.Category;


import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for managing articles in the application.
 *
 * This interface defines common operations for article management such as saving,
 * retrieving, and deleting articles. It is implemented by a class that handles
 * the business logic for these operations.
 */
public interface ArticleService {
    Article createArticle(ArticleCreation articleDTO);
    Optional<ArticleResponse> getArticleById(Long id);
    List<ArticleResponse> getArticlesByAuthorId(Long authorId);
    void deleteArticle(Long id);

    // Method to get pageable articles sorted by date of creation
    Page<ArticleResponse> getAllArticlesSortByDate(int page, int size);

    // Method to get pageable articles by category
    Page<ArticleResponse> getAllArticlesByCategory(Set<Category> categories, int page, int size);
}


