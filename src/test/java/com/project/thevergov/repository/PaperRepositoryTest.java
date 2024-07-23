package com.project.thevergov.repository;

import com.project.thevergov.domain.RequestContext;
import com.project.thevergov.dto.api.iPaper;
import com.project.thevergov.entity.PaperEntity;
import com.project.thevergov.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class PaperRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.2")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {
        postgreSQLContainer.start();
        System.setProperty("DB_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgreSQLContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgreSQLContainer.getPassword());
    }

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;
    private PaperEntity testPaper;

    @BeforeEach
    public void setUp() {
        // Set a user ID in the RequestContext
        RequestContext.setUserId(0L); // Assuming user ID is 0L for testing

        testUser = UserEntity.builder()
                .userId("testUserId")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .loginAttempts(0)
                .bio("Test bio")
                .imageUrl("http://example.com/image.jpg")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .enabled(true)
                .mfa(false)
                .qrCodeSecret("secret")
                .qrCodeImageUri("http://example.com/qrcode.jpg")
                .build();

        userRepository.save(testUser);

        testPaper = PaperEntity.builder()
                .paperId("testPaperId")
                .name("Test Paper")
                .owner(testUser)
                .build();

        paperRepository.save(testPaper);
    }

    @Test
    public void whenFindPapers_thenReturnPageOfPapers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<iPaper> papers = paperRepository.findPapers(pageable);

        assertThat(papers).isNotEmpty();
    }

    @Test
    public void whenFindPapersByName_thenReturnPageOfPapers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<iPaper> papers = paperRepository.findPapersByName("Test Paper", pageable);

        assertThat(papers).isNotEmpty();
    }

    @Test
    public void whenFindPaperByPaperId_thenReturnPaper() {
        Optional<iPaper> paper = paperRepository.findPaperByPaperId(testPaper.getPaperId());

        assertThat(paper).isPresent();
        assertThat(paper.get().getPaper_Id()).isEqualTo(testPaper.getPaperId());
    }

    @Test
    public void whenFindByPaperId_thenReturnPaperEntity() {
        Optional<PaperEntity> paper = paperRepository.findByPaperId(testPaper.getPaperId());

        assertThat(paper).isPresent();
        assertThat(paper.get().getPaperId()).isEqualTo(testPaper.getPaperId());
    }

    @Test
    public void whenFindByName_thenReturnPaperEntity() {
        Optional<PaperEntity> paper = paperRepository.findByName(testPaper.getName());

        assertThat(paper).isPresent();
        assertThat(paper.get().getName()).isEqualTo(testPaper.getName());
    }

    @Test
    public void whenNoPapers_thenReturnEmptyPage() {
        paperRepository.deleteAll(); // Ensure no papers are present

        Pageable pageable = PageRequest.of(0, 10);
        Page<iPaper> papers = paperRepository.findPapers(pageable);

        assertThat(papers).isEmpty();
    }

    @Test
    public void whenFindPapersByNameWithNoMatch_thenReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<iPaper> papers = paperRepository.findPapersByName("Nonexistent Paper Name", pageable);

        assertThat(papers).isEmpty();
    }

    @Test
    public void whenFindPaperByNonExistentId_thenReturnEmptyOptional() {
        Optional<iPaper> paper = paperRepository.findPaperByPaperId("NonExistentPaperId");

        assertThat(paper).isEmpty();
    }

    @Test
    public void whenFindPapersByNameWithMatch_thenReturnCorrectPage() {
        PaperEntity anotherPaper = PaperEntity.builder()
                .paperId("anotherPaperId")
                .name("Test Paper")
                .owner(testUser)
                .build();
        paperRepository.save(anotherPaper);

        Pageable pageable = PageRequest.of(0, 10);
        Page<iPaper> papers = paperRepository.findPapersByName("Test Paper", pageable);

        assertThat(papers).isNotEmpty();
        assertThat(papers.getTotalElements()).isGreaterThan(1); // Ensures there are multiple results
    }

    @Test
    public void whenFindPapersWithPageSizeGreaterThanNumberOfRecords_thenReturnAllPapers() {
        Pageable pageable = PageRequest.of(0, 100); // Assuming less than 100 papers exist
        Page<iPaper> papers = paperRepository.findPapers(pageable);

        assertThat(papers).isNotEmpty();
        assertThat(papers.getTotalElements()).isEqualTo(1); // Ensure the count matches the number of papers
    }

    @Test
    public void whenCreateAndRetrievePaper_thenShouldBeCorrect() {
        PaperEntity newPaper = PaperEntity.builder()
                .paperId("newPaperId")
                .name("New Paper")
                .owner(testUser)
                .build();
        paperRepository.save(newPaper);

        Optional<PaperEntity> retrievedPaper = paperRepository.findByPaperId("newPaperId");

        assertThat(retrievedPaper).isPresent();
        assertThat(retrievedPaper.get().getName()).isEqualTo("New Paper");
    }

    @Test
    public void whenFindPapersWithPagination_thenReturnCorrectPage() {
        for (int i = 1; i <= 15; i++) {
            PaperEntity paper = PaperEntity.builder()
                    .paperId("paperId" + i)
                    .name("Paper " + i)
                    .owner(testUser)
                    .build();
            paperRepository.save(paper);
        }

        Pageable firstPage = PageRequest.of(0, 10); // First page with 10 papers
        Pageable secondPage = PageRequest.of(1, 10); // Second page with the remaining papers

        Page<iPaper> firstPagePapers = paperRepository.findPapers(firstPage);
        Page<iPaper> secondPagePapers = paperRepository.findPapers(secondPage);

        assertThat(firstPagePapers.getTotalPages()).isEqualTo(2); // 15 papers, 10 per page, so 2 pages
        assertThat(firstPagePapers.getNumber()).isEqualTo(0); // First page
        assertThat(secondPagePapers.getNumber()).isEqualTo(1); // Second page
        assertThat(secondPagePapers.getContent().size()).isEqualTo(5); // 15 total, 10 on first page, so 5 on second
    }
}
