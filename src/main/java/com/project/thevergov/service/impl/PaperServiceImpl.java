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


@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class PaperServiceImpl implements PaperService {

    private final PaperRepository paperRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    @Override
    public Page<iPaper> getPapers(int page, int size) {
        // TODO
        return paperRepository.findPapers(PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Page<iPaper> getPapers(int page, int size, String name) {
        return paperRepository.findPapersByName(name, PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Collection<Paper> savePapers(String userId, List<MultipartFile> papers) {
        List<Paper> newPapers = new ArrayList<>();
        var userEntity = userRepository.findUserByUserId(userId).get();

        var storage = Paths.get(FILE_STORAGE).toAbsolutePath().normalize();
        try {
            for (MultipartFile paper : papers) {
                var filename = cleanPath(Objects.requireNonNull(paper.getOriginalFilename()));
                if ("..".contains(filename)) {
                    throw new ApiException(String.format("Invalid file name: %s", filename));
                }
                var paperEntity = PaperEntity.builder().paperId(UUID.randomUUID().toString()).name(filename).owner(userEntity).extension(getExtension(filename)).uri(getPaperUri(filename)).formattedSize(byteCountToDisplaySize(paper.getSize())).icon(setIcon((getExtension(filename)))).build();
                var savedPaper = paperRepository.save(paperEntity);
                Files.copy(paper.getInputStream(), storage.resolve(filename), REPLACE_EXISTING);
                Paper newPaper = fromPaperEntity(savedPaper, userService.getUserById(savedPaper.getCreatedBy()), userService.getUserById(savedPaper.getUpdatedBy()));
                newPapers.add(newPaper);

            }
            return newPapers;
        } catch (Exception exception) {
            throw new ApiException("Unable to save papers");
        }
    }

    private String getPaperUri(String filename) {
        return null;
    }

    @Override
    public iPaper updatePaper(String paperId, String name, String description) {
        try {
            var paperEntity = getPaperEntity(paperId);
            var paper = Paths.get(FILE_STORAGE).resolve(paperEntity.getName()).toAbsolutePath().normalize();
            Files.move(paper, paper.resolveSibling(name), REPLACE_EXISTING);
            paperEntity.setName(name);
            paperEntity.setDescription(description);
            paperRepository.save(paperEntity);
            return getPaperByPaperId(paperId);
        } catch (Exception exception) {
            throw new ApiException("Unable to updated paper");
        }
    }

    private PaperEntity getPaperEntity(String paperId) {
        return paperRepository.findByPaperId(paperId).orElseThrow(() -> new ApiException("Paper not found"));
    }

    @Override
    public void deletePaper(String paperId) {

    }

    @Override
    public iPaper getPaperByPaperId(String paperId) {
        return paperRepository.findPaperByPaperId(paperId).orElseThrow(() -> new ApiException("Paper not found"));
    }

    @Override
    public Resource getResource(String paperName) {
        try {
            var filePath = Paths.get(FILE_STORAGE).toAbsolutePath().normalize().resolve(paperName);
            if (Files.exists(filePath)) {
                throw new ApiException("Paper not found");
            }
            return new UrlResource(filePath.toUri());
        } catch (Exception exception) {
            throw new ApiException("Unable to download paper");
        }
    }
}
