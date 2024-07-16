package com.project.thevergov.restcontroller;

import com.project.thevergov.domain.Response;
import com.project.thevergov.dto.UpdatePaperRequest;
import com.project.thevergov.dto.User;
import com.project.thevergov.service.PaperService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static com.project.thevergov.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/papers")
public class PaperController {

    private PaperService paperService;

    @PostMapping("/upload")
    public ResponseEntity<Response> savePapers
            (@AuthenticationPrincipal User user,
             @RequestParam("files") List<MultipartFile> papers,
             HttpServletRequest request
            ) {

        var savedPapers = paperService.savePapers(user.getUserId(), papers);

        return ResponseEntity
                .created(getUri())
                .body(getResponse(request,
                        Map.of("papers", savedPapers),
                        "Paper(s) uploaded",
                        CREATED));

    }

    @GetMapping
    public ResponseEntity<Response> getPapers
            (@AuthenticationPrincipal User user,
             HttpServletRequest request,
             @RequestParam(value = "page", defaultValue = "0") int page,
             @RequestParam(value = "size", defaultValue = "5") int size
            ) {

        var papers = paperService.getPapers(page, size);

        return ResponseEntity
                .ok()
                .body(getResponse(request,
                        Map.of("papers", papers),
                        "Paper(s) retrieved",
                        OK));

    }

    @GetMapping("search")
    public ResponseEntity<Response> searchPapers
            (@AuthenticationPrincipal User user,
             HttpServletRequest request,
             @RequestParam(value = "page", defaultValue = "0") int page,
             @RequestParam(value = "size", defaultValue = "5") int size,
             @RequestParam(value = "name", defaultValue = "") String name
            ) {

        var papers = paperService.getPapers(page, size, name);

        return ResponseEntity
                .ok()
                .body(getResponse(request,
                        Map.of("papers", papers),
                        "Paper(s) retrieved",
                        OK));

    }

    @GetMapping("/{paperId}")
    public ResponseEntity<Response> getPaper
            (@AuthenticationPrincipal User user,
             @PathVariable("paperId") String paperId,
             HttpServletRequest request

            ) {

        var paper = paperService.getPaperByPaperId(paperId);

        return ResponseEntity
                .ok()
                .body(getResponse(request,
                        Map.of("paper", paper),
                        "Paper retrieved",
                        OK));

    }

    @PatchMapping
    public ResponseEntity<Response> updatePaper
            (@AuthenticationPrincipal User user,
             @RequestBody UpdatePaperRequest paper,
             HttpServletRequest request

            ) {

        var updatedPaper = paperService.updatePaper(paper.getPaperId(), paper.getName(), paper.getDescription());


        return ResponseEntity
                .ok()
                .body(getResponse(request,
                        Map.of("paper", updatedPaper),
                        "Paper updated",
                        OK));

    }

    @GetMapping("/download/{paperName}")
    public ResponseEntity<Resource> downloadPaper
            (
                    @AuthenticationPrincipal User user,
                    @PathVariable("paperName") String paperName
            ) throws IOException {

        var resource = paperService.getResource(paperName);
        var httpHeaders = new HttpHeaders();
        httpHeaders.add("File-Name", paperName);
        httpHeaders.add(
                HttpHeaders.CONTENT_DISPOSITION,
                String.format("attachment;File-Name=%s", resource.getFilename()));

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(resource.getFile().toPath())))
                .headers(httpHeaders)
                .body(resource);

    }


    private URI getUri() {
        return URI.create("");
    }

}
