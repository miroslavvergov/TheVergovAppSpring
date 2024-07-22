package com.project.thevergov.service;

import com.project.thevergov.dto.Paper;
import com.project.thevergov.dto.User;
import com.project.thevergov.dto.api.iPaper;
import com.project.thevergov.entity.PaperEntity;
import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.exception.ApiException;
import com.project.thevergov.repository.PaperRepository;
import com.project.thevergov.repository.UserRepository;
import com.project.thevergov.service.impl.PaperServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.project.thevergov.constant.Constants.FILE_STORAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaperServiceTest {

    @Mock
    private PaperRepository paperRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PaperServiceImpl paperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPapers() {
        Page<iPaper> mockPage = new PageImpl<>(Collections.emptyList());
        when(paperRepository.findPapers(any(PageRequest.class))).thenReturn(mockPage);

        Page<iPaper> result = paperService.getPapers(0, 10);

        assertThat(result).isEqualTo(mockPage);
        verify(paperRepository).findPapers(PageRequest.of(0, 10, Sort.by("name")));
    }

    @Test
    void testGetPapersByName() {
        Page<iPaper> mockPage = new PageImpl<>(Collections.emptyList());
        when(paperRepository.findPapersByName(anyString(), any(PageRequest.class))).thenReturn(mockPage);

        Page<iPaper> result = paperService.getPapers(0, 10, "test");

        assertThat(result).isEqualTo(mockPage);
        verify(paperRepository).findPapersByName("test", PageRequest.of(0, 10, Sort.by("name")));
    }

    @Test
    void testSavePapers() throws Exception {
        String userId = UUID.randomUUID().toString();
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getInputStream()).thenReturn(Files.newInputStream(Paths.get("test.txt")));

        PaperEntity mockPaperEntity = PaperEntity.builder().paperId(UUID.randomUUID().toString()).name("test.txt").build();
        when(paperRepository.save(any(PaperEntity.class))).thenReturn(mockPaperEntity);

        when(userRepository.findUserByUserId(anyString())).thenReturn(Optional.of(new UserEntity()));
        when(userService.getUserById(Long.valueOf(anyString()))).thenReturn(new User());

        Collection<Paper> result = paperService.savePapers(userId, List.of(mockFile));

        assertThat(result).hasSize(1);
        verify(paperRepository, times(1)).save(any(PaperEntity.class));
    }

    @Test
    void testUpdatePaper() throws Exception {
        String paperId = UUID.randomUUID().toString();
        PaperEntity mockPaperEntity = PaperEntity.builder().paperId(paperId).name("old.txt").build();

        when(paperRepository.findByPaperId(paperId)).thenReturn(Optional.of(mockPaperEntity));
        when(paperRepository.save(any(PaperEntity.class))).thenReturn(mockPaperEntity);

        Path oldFilePath = Paths.get(FILE_STORAGE).resolve("old.txt").toAbsolutePath().normalize();
        Path newFilePath = oldFilePath.resolveSibling("new.txt");

        // Mocking Files.move operation
        mockStatic(Files.class);
        when(Files.move(any(Path.class), any(Path.class))).thenReturn(newFilePath);

        iPaper updatedPaper = paperService.updatePaper(paperId, "new.txt", "new description");

        assertThat(updatedPaper).isNotNull();
        verify(paperRepository).save(mockPaperEntity);

        // Verify that the file move operation was called correctly
        verify(Files.move(oldFilePath, newFilePath), times(1));
    }

    @Test
    void testGetPaperByPaperId() {
        String paperId = UUID.randomUUID().toString();
        PaperEntity mockPaperEntity = PaperEntity.builder().paperId(paperId).build();

        when(paperRepository.findPaperByPaperId(paperId)).thenReturn(Optional.of((iPaper) mockPaperEntity));

        iPaper result = paperService.getPaperByPaperId(paperId);

        assertThat(result).isNotNull();
        verify(paperRepository).findPaperByPaperId(paperId);
    }

    @Test
    void testGetResource() throws Exception {
        String paperName = "test.txt";
        String filePath = Paths.get(FILE_STORAGE).resolve(paperName).toAbsolutePath().normalize().toString();

        Files.createFile(Paths.get(filePath));
        when(paperRepository.findByName(paperName)).thenReturn(Optional.of(new PaperEntity()));

        Resource resource = paperService.getResource(paperName);

        assertThat(resource).isInstanceOf(UrlResource.class);
        verify(paperRepository).findByName(paperName);

        Files.deleteIfExists(Paths.get(filePath));
    }

    @Test
    void testGetResourceThrowsExceptionWhenPaperNotFound() {
        String paperName = "test.txt";
        when(paperRepository.findByName(paperName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paperService.getResource(paperName))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Paper not found");
    }
}
