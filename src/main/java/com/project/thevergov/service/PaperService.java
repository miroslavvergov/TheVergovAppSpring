package com.project.thevergov.service;

import com.project.thevergov.dto.Paper;
import com.project.thevergov.dto.api.iPaper;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface PaperService {

    Page<iPaper> getPapers(int page, int size);

    Page<iPaper> getPapers(int page, int size, String name);

    Collection<Paper> savePapers(String userId, List<MultipartFile> papers);

    iPaper updatePaper(String paperId, String name, String description);

    void deletePaper(String paperId);

    iPaper getPaperByPaperId(String paperId);

    Resource getResource(String documentName);
}
