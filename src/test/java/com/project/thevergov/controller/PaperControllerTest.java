//package com.project.thevergov.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.thevergov.dto.Paper;
//import com.project.thevergov.dto.UpdatePaperRequest;
//import com.project.thevergov.dto.User;
//import com.project.thevergov.restcontroller.PaperController;
//import com.project.thevergov.service.PaperService;
//import com.project.thevergov.service.impl.PaperServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.Resource;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.http.HttpStatus.CREATED;
//import static org.springframework.http.HttpStatus.OK;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(PaperController.class)
//public class PaperControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private PaperServiceImpl paperService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private User testUser;
//    private MockMultipartFile mockFile;
//    private UpdatePaperRequest updatePaperRequest;
//
//    @BeforeEach
//    public void setUp() {
//        testUser = new User();
//        testUser.setId(1L);
//        testUser.setUserId("testUserId");
//        testUser.setUsername("johndoe");
//        testUser.setRole("ADMIN");
//
//        mockFile = new MockMultipartFile(
//                "files", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());
//
//        updatePaperRequest = new UpdatePaperRequest();
//        updatePaperRequest.setPaperId("paperId");
//        updatePaperRequest.setName("Updated Paper");
//        updatePaperRequest.setDescription("Updated Description");
//    }
//
//    @Test
//    @WithMockUser(authorities = {"paper:create"}, username = "johndoe")
//    public void whenSavePapers_thenReturnSavedPapers() throws Exception {
//        Paper paper1 = Paper.builder()
//                .paperId(UUID.randomUUID().toString())
//                .name("test.txt")
//                .ownerName(testUser.getUsername())
//                .ownerEmail("johndoe@example.com")
//                .build();
//
//        Paper paper2 = Paper.builder()
//                .paperId(UUID.randomUUID().toString())
//                .name("test2.txt")
//                .ownerName(testUser.getUsername())
//                .ownerEmail("johndoe@example.com")
//                .build();
//
//        Mockito.when(paperService.savePapers(Mockito.anyString(), Mockito.anyList()))
//                .thenReturn(List.of(paper1, paper2));
//
//        mockMvc.perform(multipart("/papers/upload")
//                        .file(mockFile)
//                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.message").value("Paper(s) uploaded"))
//                .andExpect(jsonPath("$.status").value(CREATED.value()))
//                .andExpect(jsonPath("$.data.papers").isArray())
//                .andExpect(jsonPath("$.data.papers.length()").value(2))
//                .andExpect(jsonPath("$.data.papers[0].name").value("test.txt"))
//                .andExpect(jsonPath("$.data.papers[1].name").value("test2.txt"));
//    }
//
//    @Test
//    @WithMockUser(authorities = {"paper:read"}, username = "johndoe")
//    public void whenGetPapers_thenReturnPapers() throws Exception {
//        Page<Paper> paperPage = new PageImpl<>(List.of());
//        Mockito.when(paperService.getPapers(Mockito.anyInt(), Mockito.anyInt()))
//                .thenReturn(paperPage);
//
//        mockMvc.perform(get("/papers")
//                        .param("page", "0")
//                        .param("size", "5")
//                        .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Paper(s) retrieved"))
//                .andExpect(jsonPath("$.status").value(OK.value()))
//                .andExpect(jsonPath("$.data.papers").isArray())
//                .andExpect(jsonPath("$.data.papers.length()").value(0));
//    }
//
//    @Test
//    @WithMockUser(authorities = {"paper:read"}, username = "johndoe")
//    public void whenSearchPapers_thenReturnPapers() throws Exception {
//        Page<Paper> paperPage = new PageImpl<>(List.of());
//        Mockito.when(paperService.getPapers(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()))
//                .thenReturn(paperPage);
//
//        mockMvc.perform(get("/papers/search")
//                        .param("page", "0")
//                        .param("size", "5")
//                        .param("name", "test")
//                        .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Paper(s) retrieved"))
//                .andExpect(jsonPath("$.status").value(OK.value()))
//                .andExpect(jsonPath("$.data.papers").isArray())
//                .andExpect(jsonPath("$.data.papers.length()").value(0));
//    }
//
//    @Test
//    @WithMockUser(authorities = {"paper:read"}, username = "johndoe")
//    public void whenGetPaper_thenReturnPaper() throws Exception {
//        Paper paper = Paper.builder()
//                .paperId("paperId")
//                .name("test.txt")
//                .ownerName(testUser.getUsername())
//                .ownerEmail("johndoe@example.com")
//                .build();
//
//        Mockito.when(paperService.getPaperByPaperId(Mockito.anyString()))
//                .thenReturn(paper);
//
//        mockMvc.perform(get("/papers/{paperId}", "paperId")
//                        .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Paper retrieved"))
//                .andExpect(jsonPath("$.status").value(OK.value()))
//                .andExpect(jsonPath("$.data.paper.name").value("test.txt"));
//    }
//
//    @Test
//    @WithMockUser(authorities = {"paper:update"}, username = "johndoe")
//    public void whenUpdatePaper_thenReturnUpdatedPaper() throws Exception {
//        Paper updatedPaper = Paper.builder()
//                .paperId("paperId")
//                .name("Updated Paper")
//                .description("Updated Description")
//                .ownerName(testUser.getUsername())
//                .ownerEmail("johndoe@example.com")
//                .build();
//
//        Mockito.when(paperService.updatePaper(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
//                .thenReturn(updatedPaper);
//
//        mockMvc.perform(patch("/papers")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatePaperRequest))
//                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Paper updated"))
//                .andExpect(jsonPath("$.status").value(OK.value()))
//                .andExpect(jsonPath("$.data.paper.name").value("Updated Paper"))
//                .andExpect(jsonPath("$.data.paper.description").value("Updated Description"));
//    }
//
//    @Test
//    @WithMockUser(authorities = {"paper:read"}, username = "johndoe")
//    public void whenDownloadPaper_thenReturnFileResource() throws Exception {
//        Path filePath = Paths.get("src/test/resources/test.txt");
//        Resource resource = new ByteArrayResource(Files.readAllBytes(filePath));
//
//        Mockito.when(paperService.getResource(Mockito.anyString())).thenReturn(resource);
//
//        MvcResult result = mockMvc.perform(get("/papers/download/{paperName}", "test.txt")
//                        .with(SecurityMockMvcRequestPostProcessors.user(testUser)))
//                .andExpect(status().isOk())
//                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=test.txt"))
//                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/plain"))
//                .andReturn();
//
//        byte[] responseBytes = result.getResponse().getContentAsByteArray();
//        byte[] expectedBytes = Files.readAllBytes(filePath);
//
//        assertThat(responseBytes).isEqualTo(expectedBytes);
//    }
//}
