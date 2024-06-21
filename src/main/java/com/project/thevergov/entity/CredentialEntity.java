package com.project.thevergov.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static jakarta.persistence.FetchType.*;

/**
 * CredentialEntity: Represents user credentials (password) in the database, with a one-to-one relationship with a UserEntity.
 */
@Getter // Automatically generates getters for all fields
@Setter // Automatically generates setters for all fields
@ToString // Automatically generates a toString() method
@Builder // Provides a builder pattern for creating instances
@NoArgsConstructor // Creates a no-argument constructor
@AllArgsConstructor // Creates a constructor with all arguments
@Entity // Marks this class as a JPA entity
@Table(name = "credentials") // Specifies the database table name
@JsonInclude(NON_DEFAULT) // Include only non-default values in JSON serialization
public class CredentialEntity extends Auditable { // Inherits audit fields from the Auditable base class

    // User's Password
    private String password;

    // One-to-One Relationship with UserEntity
    @OneToOne(targetEntity = UserEntity.class, fetch = EAGER) // Eagerly fetch the UserEntity
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column, not null
    @OnDelete(action = OnDeleteAction.CASCADE) // Cascade delete: if the UserEntity is deleted, also delete this CredentialEntity
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") // Handle circular references in JSON (1)
    @JsonIdentityReference(alwaysAsId = true) // Reference UserEntity by its ID in JSON (2)
    @JsonProperty("user_id") // Rename the userEntity field to "user_id" in JSON
    private UserEntity userEntity;

    /**
     * Constructor: Creates a CredentialEntity with a given UserEntity and password.
     *
     * @param userEntity The associated UserEntity
     * @param password   The user's password (should be hashed in practice)
     */
    public CredentialEntity(UserEntity userEntity, String password) {
        this.userEntity = userEntity;
        this.password = password;
    }
}
