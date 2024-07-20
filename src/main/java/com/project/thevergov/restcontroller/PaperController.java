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
import org.springframework.security.access.prepost.PreAuthorize;
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

/**
 * PaperController: Manages CRUD operations for papers in the system.
 * <p>
 * This controller provides endpoints to upload, retrieve, search, update, and download papers. Access to these endpoints
 * is controlled by user roles and permissions.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/papers")
public class PaperController {

    private final PaperService paperService;

    /**
     * Uploads a list of papers.
     * <p>
     * This endpoint allows users with specific roles to upload multiple paper files. The files are saved and a response
     * is returned with the details of the saved papers.
     *
     * @param user    the authenticated user performing the upload
     * @param papers  the list of multipart files representing the papers to be uploaded
     * @param request the HTTP request object for generating the response
     * @return a response entity containing the details of the uploaded papers
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('paper:create') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> savePapers(
            @AuthenticationPrincipal User user,
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

    /**
     * Retrieves a paginated list of papers.
     * <p>
     * This endpoint fetches papers based on the provided pagination parameters. Access is restricted to users with
     * appropriate permissions.
     *
     * @param user   the authenticated user making the request
     * @param request the HTTP request object for generating the response
     * @param page   the page number for pagination (default is 0)
     * @param size   the size of each page (default is 5)
     * @return a response entity containing the list of retrieved papers
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('paper:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getPapers(
            @AuthenticationPrincipal User user,
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

    /**
     * Searches for papers based on a name filter.
     * <p>
     * This endpoint allows searching for papers by name with pagination support. Only users with the appropriate permissions
     * can access this endpoint.
     *
     * @param user   the authenticated user making the request
     * @param request the HTTP request object for generating the response
     * @param page   the page number for pagination (default is 0)
     * @param size   the size of each page (default is 5)
     * @param name   the name filter for searching papers (default is an empty string)
     * @return a response entity containing the list of papers that match the search criteria
     */
    @GetMapping("search")
    @PreAuthorize("hasAnyAuthority('paper:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> searchPapers(
            @AuthenticationPrincipal User user,
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

    /**
     * Retrieves a specific paper by its ID.
     * <p>
     * This endpoint returns the details of a single paper identified by its ID. Access is restricted based on user permissions.
     *
     * @param user     the authenticated user making the request
     * @param paperId  the ID of the paper to be retrieved
     * @param request  the HTTP request object for generating the response
     * @return a response entity containing the details of the requested paper
     */
    @GetMapping("/{paperId}")
    @PreAuthorize("hasAnyAuthority('paper:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getPaper(
            @AuthenticationPrincipal User user,
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

    /**
     * Updates the details of a specific paper.
     * <p>
     * This endpoint allows for updating the name and description of a paper identified by its ID. Access is controlled by
     * user permissions.
     *
     * @param user   the authenticated user performing the update
     * @param paper  the request body containing the updated paper details
     * @param request the HTTP request object for generating the response
     * @return a response entity containing the details of the updated paper
     */
    @PatchMapping
    @PreAuthorize("hasAnyAuthority('paper:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> updatePaper(
            @AuthenticationPrincipal User user,
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

    /**
     * Downloads a paper by its name.
     * <p>
     * This endpoint allows users to download a paper file identified by its name. The file is returned as a resource with
     * the appropriate content type and headers.
     *
     * @param user       the authenticated user requesting the download
     * @param paperName  the name of the paper to be downloaded
     * @return a response entity containing the paper file as a resource
     * @throws IOException if there is an issue reading the file
     */
    @GetMapping("/download/{paperName}")
    @PreAuthorize("hasAnyAuthority('paper:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Resource> downloadPaper(
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

    /**
     * Provides a dummy URI for the response.
     * <p>
     * This method generates a URI for the location header in the response when a new resource is created.
     *
     * @return a dummy URI
     */
    private URI getUri() {
        return URI.create("");
    }
}
