package com.project.thevergov.repository;

import com.project.thevergov.model.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

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
    List<Article> findByAuthorId(UUID authorId);
}


