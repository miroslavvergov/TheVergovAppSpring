package com.project.thevergov.model.entity;

import com.project.thevergov.model.enums.Category;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entity representing an article in the application.
 *
 * The Article entity includes details such as title, content, author, and metadata.
 * A category is also included to classify the article.
 */
@Entity
@Table(name = "articles")
@Getter
@Setter
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdDate;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ElementCollection(targetClass = Category.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "article_categories", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "category", nullable = false)
    private Set<Category> categories;

}



