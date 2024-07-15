package com.project.thevergov.dto;

import com.project.thevergov.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Paper {

    private Long id;

    private String paperId;

    private String name;

    private String description;

    private String uri;

    private long size;

    private String formattedSize;

    private String icon;

    private String extension;

    private String referenceId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String ownerName;

    private String ownerEmail;

    private String ownerPhone;

    private String ownerLastLogin;

    private String updaterName;
}
