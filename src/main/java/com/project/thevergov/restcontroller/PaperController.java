package com.project.thevergov.restcontroller;

import com.project.thevergov.domain.Response;
import com.project.thevergov.dto.User;
import com.project.thevergov.service.PaperService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
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
                        "Paper(s) fetched",
                        OK));

    }


    private URI getUri() {
        return URI.create("");
    }

}
