package com.project.thevergov.service.impl;

import com.project.thevergov.dto.Paper;
import com.project.thevergov.dto.api.iPaper;
import com.project.thevergov.entity.PaperEntity;
import com.project.thevergov.exception.ApiException;
import com.project.thevergov.repository.PaperRepository;
import com.project.thevergov.repository.UserRepository;
import com.project.thevergov.service.PaperService;
import com.project.thevergov.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.project.thevergov.constant.Constants.FILE_STORAGE;
import static com.project.thevergov.utils.PaperUtil.fromPaperEntity;
import static com.project.thevergov.utils.PaperUtil.setIcon;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.springframework.util.StringUtils.cleanPath;

/**
 * Service implementation for managing paper documents.
 * Provides methods to save, update, delete, and retrieve paper documents.
 */
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class) // Ensures the transaction is rolled back in case of an exception
public class PaperServiceImpl implements PaperService {

    private final PaperRepository paperRepository; // Repository for paper entities
    private final UserRepository userRepository; // Repository for user entities
    private final UserService userService; // Service for user operations

    /**
     * Retrieves a paginated list of papers with default sorting by name.
     *
     * @param page The page number to retrieve.
     * @param size The number of papers per page.
     * @return A page of {@link iPaper} objects.
     */
    @Override
    public Page<iPaper> getPapers(int page, int size) {
        return paperRepository.findPapers(PageRequest.of(page, size, Sort.by("name")));
    }

    /**
     * Retrieves a paginated list of papers filtered by name with default sorting by name.
     *
     * @param page The page number to retrieve.
     * @param size The number of papers per page.
     * @param name The name filter for the papers.
     * @return A page of {@link iPaper} objects.
     */
    @Override
    public Page<iPaper> getPapers(int page, int size, String name) {
        return paperRepository.findPapersByName(name, PageRequest.of(page, size, Sort.by("name")));
    }

    /**
     * Saves multiple paper files uploaded by a user.
     * <p>
     * Validates each file, creates a paper entity, saves the file to storage, and adds it to the database.
     * </p>
     *
     * @param userId The ID of the user uploading the papers.
     * @param papers The list of files to be saved.
     * @return A collection of {@link Paper} objects representing the saved papers.
     */
    @Override
    public Collection<Paper> savePapers(String userId, List<MultipartFile> papers) {
        List<Paper> newPapers = new ArrayList<>();
        var userEntity = userRepository.findUserByUserId(userId).get();

        var storage = Paths.get(FILE_STORAGE).toAbsolutePath().normalize();
        try {
            for (MultipartFile paper : papers) {
                var filename = cleanPath(Objects.requireNonNull(paper.getOriginalFilename()));
                // Validate filename to prevent directory traversal attacks
                if (filename.contains("..")) {
                    throw new ApiException(String.format("Invalid file name: %s", filename));
                }
                // Create and save PaperEntity
                var paperEntity = PaperEntity.builder()
                        .paperId(UUID.randomUUID().toString())
                        .name(filename)
                        .owner(userEntity)
                        .extension(getExtension(filename))
                        .uri(getPaperUri(filename))
                        .formattedSize(byteCountToDisplaySize(paper.getSize()))
                        .icon(setIcon(getExtension(filename)))
                        .build();
                var savedPaper = paperRepository.save(paperEntity);

                // Save the file to the storage directory
                Files.copy(paper.getInputStream(), storage.resolve(filename), REPLACE_EXISTING);

                // Convert PaperEntity to Paper DTO and add to the list
                Paper newPaper = fromPaperEntity(
                        savedPaper,
                        userService.getUserById(savedPaper.getCreatedBy()),
                        userService.getUserById(savedPaper.getUpdatedBy())
                );
                newPapers.add(newPaper);
            }
            return newPapers;
        } catch (Exception exception) {
            throw new ApiException("Unable to save papers");
        }
    }

    /**
     * Generates a URI for accessing the paper file based on its filename.
     * <p>
     * This method is a placeholder and needs to be implemented.
     * </p>
     *
     * @param filename The name of the file.
     * @return The URI of the paper.
     */
    private String getPaperUri(String filename) {
        return null; // Placeholder implementation
    }

    /**
     * Updates the details of an existing paper, including renaming the file and updating its description.
     *
     * @param paperId     The ID of the paper to update.
     * @param name        The new name for the paper.
     * @param description The new description for the paper.
     * @return The updated {@link iPaper} object.
     */
    @Override
    public iPaper updatePaper(String paperId, String name, String description) {
        try {
            var paperEntity = getPaperEntity(paperId);
            var paperPath = Paths.get(FILE_STORAGE).resolve(paperEntity.getName()).toAbsolutePath().normalize();
            // Rename the file in storage
            Files.move(paperPath, paperPath.resolveSibling(name), REPLACE_EXISTING);
            paperEntity.setName(name);
            paperEntity.setDescription(description);
            paperRepository.save(paperEntity);
            return getPaperByPaperId(paperId);
        } catch (Exception exception) {
            throw new ApiException("Unable to update paper");
        }
    }

    /**
     * Retrieves a PaperEntity by its ID.
     *
     * @param paperId The ID of the paper to retrieve.
     * @return The {@link PaperEntity} object.
     */
    private PaperEntity getPaperEntity(String paperId) {
        return paperRepository.findByPaperId(paperId)
                .orElseThrow(() -> new ApiException("Paper not found"));
    }

    /**
     * Deletes a paper by its ID.
     * <p>
     * This method is currently not implemented.
     * </p>
     *
     * @param paperId The ID of the paper to delete.
     */
    @Override
    public void deletePaper(String paperId) {
        // TODO: Implement paper deletion logic
    }

    /**
     * Retrieves a paper by its ID.
     *
     * @param paperId The ID of the paper to retrieve.
     * @return The {@link iPaper} object.
     */
    @Override
    public iPaper getPaperByPaperId(String paperId) {
        return paperRepository.findPaperByPaperId(paperId)
                .orElseThrow(() -> new ApiException("Paper not found"));
    }

    /**
     * Retrieves the file resource for a specified paper.
     * <p>
     * Checks if the file exists in storage and returns a URL resource for the file.
     * </p>
     *
     * @param paperName The name of the paper file.
     * @return The {@link Resource} for the paper file.
     */
    @Override
    public Resource getResource(String paperName) {
        try {
            var filePath = Paths.get(FILE_STORAGE).toAbsolutePath().normalize().resolve(paperName);
            // Check if the file exists
            if (!Files.exists(filePath)) {
                throw new ApiException("Paper not found");
            }
            return new UrlResource(filePath.toUri());
        } catch (Exception exception) {
            throw new ApiException("Unable to download paper");
        }
    }
}
