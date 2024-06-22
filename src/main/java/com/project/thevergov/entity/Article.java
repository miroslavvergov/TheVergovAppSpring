package com.project.thevergov.entity;

import com.project.thevergov.enumeration.Category;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entity representing an article in the application.
 * <p>
 * The Article entity includes details such as title, content, author, and metadata.
 * A category is also included to classify the article.
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "articles")
public class Article extends Auditable {

    @Column(name = "article_id",updatable = false, unique = true, nullable = false)
    private String articleId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(columnDefinition = "text", nullable = false)
    private String icon;

    @ElementCollection(targetClass = Category.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "article_categories", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "category", nullable = false)
    private Set<Category> categories;

}



