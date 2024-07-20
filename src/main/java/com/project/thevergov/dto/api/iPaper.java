package com.project.thevergov.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * iPaper: An interface defining the contract for a Paper entity with getter and setter methods.
 * This interface specifies the required properties and their corresponding JSON property names
 * for serialization and deserialization purposes.
 */
public interface iPaper {

    /**
     * Gets the unique identifier of the paper.
     *
     * @return the unique ID of the paper
     */
    Long getId();

    /**
     * Sets the unique identifier of the paper.
     *
     * @param id the unique ID of the paper
     */
    void setId(Long id);

    /**
     * Gets the paper ID as a string.
     *
     * @return the paper ID
     */
    String getPaper_Id();

    /**
     * Sets the paper ID.
     *
     * @param paperId the paper ID
     */
    void setPaper_Id(String paperId);

    /**
     * Gets the name of the paper.
     *
     * @return the name of the paper
     */
    String getName();

    /**
     * Sets the name of the paper.
     *
     * @param name the name of the paper
     */
    void setName(String name);

    /**
     * Gets the description of the paper.
     *
     * @return the description of the paper
     */
    String getDescription();

    /**
     * Sets the description of the paper.
     *
     * @param description the description of the paper
     */
    void setDescription(String description);

    /**
     * Gets the URI of the paper.
     *
     * @return the URI of the paper
     */
    String getUri();

    /**
     * Sets the URI of the paper.
     *
     * @param uri the URI of the paper
     */
    void setUri(String uri);

    /**
     * Gets the icon associated with the paper.
     *
     * @return the icon of the paper
     */
    String getIcon();

    /**
     * Sets the icon associated with the paper.
     *
     * @param icon the icon of the paper
     */
    void setIcon(String icon);

    /**
     * Gets the size of the paper.
     *
     * @return the size of the paper in bytes
     */
    long getSize();

    /**
     * Sets the size of the paper.
     *
     * @param size the size of the paper in bytes
     */
    void setSize(long size);

    /**
     * Gets the reference ID of the paper.
     *
     * @return the reference ID
     */
    @JsonProperty("referenceId")
    String getReference_Id();

    /**
     * Sets the reference ID of the paper.
     *
     * @param referenceId the reference ID
     */
    void setReference_Id(String referenceId);

    /**
     * Gets the creation timestamp of the paper.
     *
     * @return the creation timestamp
     */
    @JsonProperty("createdAt")
    LocalDateTime getCreated_At();

    /**
     * Sets the creation timestamp of the paper.
     *
     * @param createdAt the creation timestamp
     */
    void setCreated_At(LocalDateTime createdAt);

    /**
     * Gets the last update timestamp of the paper.
     *
     * @return the last update timestamp
     */
    @JsonProperty("updatedAt")
    LocalDateTime getUpdated_At();

    /**
     * Sets the last update timestamp of the paper.
     *
     * @param updatedAt the last update timestamp
     */
    void setUpdated_At(LocalDateTime updatedAt);

    /**
     * Gets the name of the paper's owner.
     *
     * @return the owner's name
     */
    @JsonProperty("ownerName")
    String getOwner_Name();

    /**
     * Sets the name of the paper's owner.
     *
     * @param ownerName the owner's name
     */
    void setOwner_Name(String ownerName);

    /**
     * Gets the email address of the paper's owner.
     *
     * @return the owner's email address
     */
    @JsonProperty("ownerEmail")
    String getOwner_Email();

    /**
     * Sets the email address of the paper's owner.
     *
     * @param ownerEmail the owner's email address
     */
    void setOwner_Email(String ownerEmail);

    /**
     * Gets the phone number of the paper's owner.
     *
     * @return the owner's phone number
     */
    @JsonProperty("ownerPhone")
    String getOwner_Phone();

    /**
     * Sets the phone number of the paper's owner.
     *
     * @param ownerPhone the owner's phone number
     */
    void setOwner_Phone(String ownerPhone);

    /**
     * Gets the last login timestamp of the paper's owner.
     *
     * @return the owner's last login timestamp
     */
    @JsonProperty("ownerLastLogin")
    LocalDateTime getOwner_Last_Login();

    /**
     * Sets the last login timestamp of the paper's owner.
     *
     * @param ownerLastLogin the owner's last login timestamp
     */
    void setOwner_Last_Login(LocalDateTime ownerLastLogin);

    /**
     * Gets the name of the person who last updated the paper.
     *
     * @return the updater's name
     */
    @JsonProperty("updaterName")
    String getUpdater_Name();

    /**
     * Sets the name of the person who last updated the paper.
     *
     * @param updaterName the updater's name
     */
    void setUpdater_Name(String updaterName);
}
