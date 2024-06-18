package com.project.thevergov.repository;

import com.project.thevergov.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Comment} entity.
 *
 * This interface provides methods for performing CRUD operations on Comment entities,
 * as well as custom queries for finding comments by the article ID and user ID.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Finds comments by the ID of the article.
     *
     * @param articleId the ID of the article
     * @return a list of comments for the specified article
     */
    List<Comment> findByArticleId(Long articleId);

    /**
     * Finds comments by the ID of the user.
     *
     * @param userId the ID of the user
     * @return a list of comments made by the specified user
     */
    List<Comment> findByUserId(UUID userId);
}

