package com.project.thevergov.repository;

import com.project.thevergov.entity.Article;
import com.project.thevergov.enumeration.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

/**
 * Repository interface for {@link Article} entity.
 *
 * This interface provides methods for performing CRUD operations on Article entities,
 * as well as custom queries for finding articles by the author's ID.
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Finds articles by the ID of the author.
     *
     * @param authorId the ID of the author
     * @return a list of articles written by the specified author
     */
    List<Article> findByAuthorId(Long authorId);

    Page<Article> findByCategoriesIn(Set<Category> categories, Pageable pageable);
}


