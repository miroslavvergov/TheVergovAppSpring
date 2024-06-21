package com.project.thevergov.service;

import com.project.thevergov.entity.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing comments in the application.
 *
 * This interface defines common operations for comment management such as saving,
 * retrieving, and deleting comments. It is implemented by a class that handles
 * the business logic for these operations.
 */
public interface CommentService {
    Comment saveComment(Comment comment);
    Optional<Comment> getCommentById(Long id);
    List<Comment> getCommentsByArticleId(Long articleId);
    List<Comment> getCommentsByUserId(Long userId);
    void deleteComment(Long id);
}

