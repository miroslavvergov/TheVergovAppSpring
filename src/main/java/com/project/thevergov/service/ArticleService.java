package com.project.thevergov.service;

import com.project.thevergov.model.entity.Article;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing articles in the application.
 *
 * This interface defines common operations for article management such as saving,
 * retrieving, and deleting articles. It is implemented by a class that handles
 * the business logic for these operations.
 */
public interface ArticleService {
    Article saveArticle(Article article);
    Optional<Article> getArticleById(Long id);
    List<Article> getArticlesByAuthorId(UUID authorId);
    void deleteArticle(Long id);
}

