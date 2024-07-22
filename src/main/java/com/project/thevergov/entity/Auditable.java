package com.project.thevergov.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.thevergov.domain.RequestContext;
import com.project.thevergov.exception.ApiException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

@Getter
@Setter
@MappedSuperclass // Indicates this is a base class for other entities
@EntityListeners(AuditingEntityListener.class) // Automatically updates audit fields
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
// Hide audit fields in JSON, but allow reading
public abstract class Auditable {

    // Primary Key (generated automatically)
    @Id
    @SequenceGenerator(name = "primary_key_seq", sequenceName = "primary_key_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_key_seq")
    @Column(name = "id", updatable = false)
    private Long id;

    // Unique Reference ID (alternative to UUID)
    private String referenceId = new AlternativeJdkIdGenerator().generateId().toString();


    //TODO
    //we need to use the schema to produce foreign keys
    // Mandatory Fields for Tracking User Actions
    @NotNull
    private Long createdBy;
    @NotNull
    private Long updatedBy;

    @Value("${spring.profiles.active}")
    private static String currentProfile;

    //if you want to achieve a program in which you insert the foreign key through jpa additional classes would be needed
    //  // Foreign Keys for createdBy and updatedBy
    //  @ManyToOne
    //  @JoinColumn(name = "creator_id",
    //          referencedColumnName = "id",
    //          foreignKey = @ForeignKey(name = "fk_user_creator", value = ConstraintMode.CONSTRAINT))
    //  private UserEntity creator;
    //  @ManyToOne
    //  @JoinColumn(name = "updater_id",
    //          referencedColumnName = "id",
    //          foreignKey = @ForeignKey(name = "fk_user_updater", value = ConstraintMode.CONSTRAINT))
    //  private UserEntity updater;


    // Timestamps for Creation and Last Update
    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @CreatedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * beforePersist: JPA lifecycle callback executed before saving a new entity.
     * - Sets createdAt, createdBy, updatedBy, and updatedAt using the current time and user ID from RequestContext.
     * - Throws an exception if the user ID is not available in the RequestContext.
     */
    @PrePersist
    public void beforePersist() {
        Long userId;
        if (currentProfile.equals("test")) {
            userId = 0L;
        } else {

            userId = RequestContext.getUserId();

            if (userId == null) {
                throw new ApiException("Cannot update entity without user ID in RequestContext for this thread");
            }
        }
        var user = new UserEntity();
        user.setId(userId);

        setCreatedAt(now());
        setCreatedBy(userId);
        setUpdatedBy(userId);
        setUpdatedAt(now());
    }

    /**
     * beforeUpdate: JPA lifecycle callback executed before updating an existing entity.
     * - Sets updatedBy and updatedAt using the current time and user ID from RequestContext.
     * - Throws an exception if the user ID is not available in the RequestContext.
     */
    @PreUpdate
    public void beforeUpdate() {
        Long userId;
        if (currentProfile.equals("test")) {
            userId = 0L;
        } else {

            userId = RequestContext.getUserId();

            if (userId == null) {
                throw new ApiException("Cannot update entity without user ID in RequestContext for this thread");
            }
        }
        var user = new UserEntity();
        user.setId(userId);

        setUpdatedBy(userId);
        setUpdatedAt(now());
    }
}