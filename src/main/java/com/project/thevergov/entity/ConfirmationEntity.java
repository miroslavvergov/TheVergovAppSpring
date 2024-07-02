package com.project.thevergov.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static jakarta.persistence.FetchType.EAGER;

@Getter // Automatically generates getters for all fields
@Setter // Automatically generates setters for all fields
@ToString // Automatically generates a toString() method
@Builder // Provides a builder pattern for creating instances
@NoArgsConstructor // Creates a no-argument constructor
@AllArgsConstructor // Creates a constructor with all arguments
@Entity // Marks this class as a JPA entity
@Table(name = "confirmations") // Specifies the database table name
@JsonInclude(NON_DEFAULT) // Include only non-default values in JSON serialization
public class ConfirmationEntity extends Auditable {


    //we name it tokenKey because we get an exception in the other situation
    @Column(name = "token_key")
    private String tokenKey;

    // One-to-One Relationship with UserEntity
    @OneToOne(targetEntity = UserEntity.class, fetch = EAGER) // Eagerly fetch the UserEntity
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column, not null
    @OnDelete(action = OnDeleteAction.CASCADE)
    // Cascade delete: if the UserEntity is deleted, also delete this CredentialEntity
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    // Handle circular references in JSON (1)
    @JsonIdentityReference(alwaysAsId = true) // Reference UserEntity by its ID in JSON (2)
    @JsonProperty("user_id") // Rename the userEntity field to "user_id" in JSON
    private UserEntity userEntity;

    /**
     * Constructor: Creates a ConfirmationEntity with a given UserEntity and password.
     *
     * @param userEntity The associated UserEntity
     */
    public ConfirmationEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.tokenKey = UUID.randomUUID().toString();
    }
}
