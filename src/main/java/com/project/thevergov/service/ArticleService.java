package com.project.thevergov.service;

import com.project.thevergov.model.dto.ArticleResponseDTO;
import com.project.thevergov.model.dto.CreationArticleDTO;
import com.project.thevergov.model.entity.Article;
import com.project.thevergov.model.enums.Category;


import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for managing articles in the application.
 *
 * This interface defines common operations for article management such as saving,
 * retrieving, and deleting articles. It is implemented by a class that handles
 * the business logic for these operations.
 */
public interface ArticleService {
    Article createArticle(CreationArticleDTO articleDTO);
    Optional<ArticleResponseDTO> getArticleById(Long id);
    List<ArticleResponseDTO> getArticlesByAuthorId(UUID authorId);
    void deleteArticle(Long id);

    // Method to get pageable articles sorted by date of creation
    Page<ArticleResponseDTO> getAllArticlesSortByDate(int page, int size);

    // Method to get pageable articles by category
    Page<ArticleResponseDTO> getAllArticlesByCategory(Set<Category> categories, int page, int size);
}


