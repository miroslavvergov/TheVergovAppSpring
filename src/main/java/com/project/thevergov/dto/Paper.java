package com.project.thevergov.dto;

import com.project.thevergov.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Paper: A Data Transfer Object (DTO) representing the details of a paper entity.
 * This class encapsulates various attributes related to a paper, including metadata and user information.
 * It uses Lombok annotations to reduce boilerplate code for getters, setters, constructors, and builders.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Paper {

    /**
     * Unique identifier for the paper.
     * This is a database-generated ID that uniquely identifies each paper entity.
     */
    private Long id;

    /**
     * Unique identifier for the paper, typically a string representation.
     * This ID is used to uniquely identify the paper within the system.
     */
    private String paperId;

    /**
     * Name or title of the paper.
     * This field represents the main identifier or title used for the paper.
     */
    private String name;

    /**
     * Description of the paper.
     * This field provides a detailed summary or additional information about the paper.
     */
    private String description;

    /**
     * URI where the paper can be accessed or downloaded.
     * This field holds the location or link to the paper's file.
     */
    private String uri;

    /**
     * Size of the paper file in bytes.
     * This field indicates the size of the paper file, used for management and display purposes.
     */
    private long size;

    /**
     * Formatted size of the paper file.
     * This field represents the human-readable size of the paper file (e.g., "15 MB").
     */
    private String formattedSize;

    /**
     * Icon associated with the paper.
     * This field contains a reference to an icon or image representing the paper.
     */
    private String icon;

    /**
     * File extension of the paper.
     * This field indicates the format of the paper file (e.g., ".pdf", ".docx").
     */
    private String extension;

    /**
     * Reference ID associated with the paper.
     * This field provides additional reference information related to the paper.
     */
    private String referenceId;

    /**
     * Timestamp when the paper was created.
     * This field indicates when the paper was originally uploaded or created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the paper was last updated.
     * This field indicates the last time the paper's details were modified.
     */
    private LocalDateTime updatedAt;

    /**
     * Name of the person who owns the paper.
     * This field contains the name of the user or entity that owns the paper.
     */
    private String ownerName;

    /**
     * Email address of the paper's owner.
     * This field holds the contact email of the user or entity that owns the paper.
     */
    private String ownerEmail;

    /**
     * Phone number of the paper's owner.
     * This field contains the contact phone number of the user or entity that owns the paper.
     */
    private String ownerPhone;

    /**
     * Last login timestamp of the paper's owner.
     * This field provides the last login time of the user or entity that owns the paper.
     */
    private String ownerLastLogin;

    /**
     * Name of the person who last updated the paper.
     * This field contains the name of the user or entity that last modified the paper.
     */
    private String updaterName;
}
