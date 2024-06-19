package com.project.thevergov.service.impl;

import com.project.thevergov.model.dto.ArticleResponseDTO;
import com.project.thevergov.model.dto.CreationArticleDTO;
import com.project.thevergov.model.entity.Article;
import com.project.thevergov.model.entity.User;
import com.project.thevergov.model.enums.Category;
import com.project.thevergov.repository.ArticleRepository;
import com.project.thevergov.service.ArticleService;
import com.project.thevergov.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing articles.
 * <p>
 * This class provides implementations for the methods defined in the ArticleService
 * interface. It interacts with the ArticleRepository to perform CRUD operations and
 * uses transactions to ensure data integrity.
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;


    /**
     * Creates a new article.
     *
     * @param articleDTO the DTO containing article data
     * @return the created article
     */
    @Override
    @Transactional
    public Article createArticle(CreationArticleDTO articleDTO) {
        // Retrieve the author by ID
        Optional<User> authorOptional = userService.getUserById(articleDTO.getAuthorId());
        if (!authorOptional.isPresent()) {
            throw new IllegalArgumentException("Author not found");
        }
        User author = authorOptional.get();

        // Map the DTO to the entity
        Article article = modelMapper.map(articleDTO, Article.class);
        article.setAuthor(author);
        article.setCreatedDate(LocalDateTime.now());
        article.setUpdatedDate(LocalDateTime.now());

        return articleRepository.save(article);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArticleResponseDTO> getArticleById(Long id) {
        return articleRepository.findById(id).map(article -> modelMapper.map(article, ArticleResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleResponseDTO> getArticlesByAuthorId(UUID authorId) {
        List<Article> articles = articleRepository.findByAuthorId(authorId);
        return articles.stream()
                .map(article -> modelMapper.map(article, ArticleResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleResponseDTO> getAllArticlesSortByDate(int page, int size) {
        Page<Article> articlesPage = articleRepository.findAll(PageRequest.of(page, size, Sort.by("createdDate")));
        List<ArticleResponseDTO> articlesDTOs = articlesPage.getContent().stream()
                .map(article -> modelMapper.map(article, ArticleResponseDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<>(articlesDTOs, articlesPage.getPageable(), articlesPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleResponseDTO> getAllArticlesByCategory(Set<Category> categories, int page, int size) {
        Page<Article> articlesPage = articleRepository.findByCategoriesIn(categories, PageRequest.of(page, size, Sort.by("createdDate")));
        List<ArticleResponseDTO> articlesDTOs = articlesPage.getContent().stream()
                .map(article -> modelMapper.map(article, ArticleResponseDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<>(articlesDTOs, articlesPage.getPageable(), articlesPage.getTotalElements());
    }
}
