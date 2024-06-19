package com.project.thevergov.web;

import com.project.thevergov.model.dto.ArticleResponseDTO;
import com.project.thevergov.model.dto.CreationArticleDTO;
import com.project.thevergov.model.entity.Article;
import com.project.thevergov.model.enums.Category;
import com.project.thevergov.service.ArticleService;
import com.project.thevergov.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * REST controller for managing articles.
 * <p>
 * This controller provides endpoints for CRUD operations and other
 * article-related actions.
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;


    /**
     * Endpoint to create a new article.
     *
     * @param creationArticleDTO the DTO containing article data
     * @return the created article
     */
    @PostMapping
    public Article createArticle(@RequestBody CreationArticleDTO creationArticleDTO) {
        return articleService.createArticle(creationArticleDTO);
    }

    /**
     * Endpoint to retrieve an article by its ID.
     *
     * @param id the ID of the article
     * @return the article
     */
    @GetMapping("/{id}")
    public Optional<ArticleResponseDTO> getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    /**
     * Endpoint to retrieve articles by author ID.
     *
     * @param authorId the ID of the author
     * @return the list of articles by the author
     */
    @GetMapping("/author/{authorId}")
    public List<ArticleResponseDTO> getArticlesByAuthorId(@PathVariable UUID authorId) {
        return articleService.getArticlesByAuthorId(authorId);
    }

    /**
     * Endpoint to delete an article by its ID.
     *
     * @param id the ID of the article
     */
    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
    }

    /**
     * Endpoint to retrieve a page of articles sorted by creation date.
     *
     * @param page the page number to retrieve
     * @param size the size of the page to retrieve
     * @return a page of articles sorted by creation date
     */
    @GetMapping
    public Page<ArticleResponseDTO> getArticles(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return articleService.getAllArticlesSortByDate(page, size);
    }

    /**
     * Endpoint to retrieve a page of articles filtered by categories.
     *
     * @param categories the set of categories to filter articles
     * @param page       the page number to retrieve
     * @param size       the size of the page to retrieve
     * @return a page of articles filtered by the specified categories
     */
    @GetMapping("/by-category")
    public Page<ArticleResponseDTO> getArticlesByCategory(@RequestParam Set<Category> categories,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        return articleService.getAllArticlesByCategory(categories, page, size);
    }
}

