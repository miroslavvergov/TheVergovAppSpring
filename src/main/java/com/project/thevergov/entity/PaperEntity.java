package com.project.thevergov.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;
import static jakarta.persistence.FetchType.*;

/**
 * Represents a user in the application.
 * <p>
 * Each user has a unique username and email, and a password for authentication. Users are assigned roles
 * which dictate their permissions within the application.
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "papers")
@JsonInclude(NON_DEFAULT)
public class PaperEntity extends Auditable {

    @Column(name = "paper_id", updatable = false, unique = true, nullable = false)
    private String paperId;

    private String name;

    private String description;

    private String uri;

    private long size;

    private String formattedSize;

    private String icon;

    private String extension;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "fk_papers_owner",
                    foreignKeyDefinition = "foreign key /* FK */ (user_id) references UserEntity",
                    value = ConstraintMode.CONSTRAINT)
    )
    private UserEntity owner;
}

