package com.project.thevergov.restcontroller;

import com.project.thevergov.domain.Response;
import com.project.thevergov.domain.dto.UserRequest;
import com.project.thevergov.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.project.thevergov.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(path = {"/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> saveUser(
            @RequestBody @Valid UserRequest user,
            HttpServletRequest request) {

        userService.createUser(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword());

        return ResponseEntity
                .created(getUri())
                .body(getResponse(request, emptyMap(), "Account created. Check your email to enable your account",CREATED));
    }

    private URI getUri() {
        //TODO
        return URI.create("");
    }


}






























