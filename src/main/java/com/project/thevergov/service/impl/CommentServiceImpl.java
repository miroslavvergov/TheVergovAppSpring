package com.project.thevergov.service.impl;

import com.project.thevergov.model.entity.Comment;
import com.project.thevergov.repository.CommentRepository;
import com.project.thevergov.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing comments.
 * <p>
 * This class provides implementations for the methods defined in the CommentService
 * interface. It interacts with the CommentRepository to perform CRUD operations and
 * uses transactions to ensure data integrity.
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Saves a comment to the database.
     * <p>
     * This method is transactional to ensure that the comment is saved consistently.
     *
     * @param comment the comment to be saved
     * @return the saved comment
     */
    @Override
    @Transactional
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    /**
     * Retrieves a comment by its ID.
     * <p>
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param id the ID of the comment
     * @return an Optional containing the found comment, or empty if no comment found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    /**
     * Retrieves comments by the ID of the article they are associated with.
     * <p>
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param articleId the ID of the article
     * @return a list of comments for the specified article
     */
    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByArticleId(Long articleId) {
        return commentRepository.findByArticleId(articleId);
    }

    /**
     * Retrieves comments by the ID of the user who made them.
     * <p>
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param userId the ID of the user
     * @return a list of comments made by the specified user
     */
    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByUserId(UUID userId) {
        return commentRepository.findByUserId(userId);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}

