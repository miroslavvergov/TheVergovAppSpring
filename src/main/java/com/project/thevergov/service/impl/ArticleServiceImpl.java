package com.project.thevergov.service.impl;

import com.project.thevergov.model.entity.Article;
import com.project.thevergov.repository.ArticleRepository;
import com.project.thevergov.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing articles.
 *
 * This class provides implementations for the methods defined in the ArticleService
 * interface. It interacts with the ArticleRepository to perform CRUD operations and
 * uses transactions to ensure data integrity.
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * Saves an article to the database.
     *
     * This method is transactional to ensure that the article is saved consistently.
     *
     * @param article the article to be saved
     * @return the saved article
     */
    @Override
    @Transactional
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    /**
     * Retrieves an article by its ID.
     *
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param id the ID of the article
     * @return an Optional containing the found article, or empty if no article found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    /**
     * Retrieves articles by the ID of their author.
     *
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param authorId the ID of the author
     * @return a list of articles written by the specified author
     */
    @Override
    @Transactional(readOnly = true)
    public List<Article> getArticlesByAuthorId(UUID authorId) {
        return articleRepository.findByAuthorId(authorId);
    }

    /**
     * Deletes an article by its ID.
     *
     * This method is transactional to ensure that the article is deleted consistently.
     *
     * @param id the ID of the article to be deleted
     */
    @Override
    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}

