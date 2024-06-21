package com.project.thevergov.restcontroller;



import com.project.thevergov.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final UserService userService;

    //DEMO endpoint
  //  @PostMapping("/makeAdmin")
  //  public ResponseEntity<?> makeAdmin(@RequestBody MakeAdminDTO user) {
  //      try {
  //          userService.makeAdmin(user.getEmail());
  //      } catch (NotFoundException e) {
  //          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(400, e.getMessage()));
  //      }
//
  //      return ResponseEntity.status(HttpStatus.OK).build();
  //  }
}
