package com.project.thevergov.web;

import com.project.thevergov.domain.dto.ArticleResponse;
import com.project.thevergov.domain.dto.ArticleCreation;
import com.project.thevergov.enumeration.Category;
import com.project.thevergov.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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


    /**
     * Endpoint to create a new article.
     *
     * @param creationArticleDTO the DTO containing article data
     * @return the created article
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createArticle(@Valid @RequestBody ArticleCreation creationArticleDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().get(0).getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.createArticle(creationArticleDTO));
    }

    /**
     * Endpoint to retrieve an article by its ID.
     *
     * @param id the ID of the article
     * @return the article
     */
    @GetMapping("/{id}")
    public Optional<ArticleResponse> getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    /**
     * Endpoint to retrieve articles by author ID.
     *
     * @param authorId the ID of the author
     * @return the list of articles by the author
     */
    @GetMapping("/author/{authorId}")
    public List<ArticleResponse> getArticlesByAuthorId(@PathVariable Long authorId) {
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
    public Page<ArticleResponse> getArticles(@RequestParam(defaultValue = "0") int page,
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
    public Page<ArticleResponse> getArticlesByCategory(@RequestParam Set<Category> categories,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return articleService.getAllArticlesByCategory(categories, page, size);
    }
}

