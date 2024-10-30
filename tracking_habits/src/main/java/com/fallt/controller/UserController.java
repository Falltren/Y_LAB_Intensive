package com.fallt.controller;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.UserService;
import com.fallt.service.ValidationService;
import com.fallt.util.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthenticationContext authenticationContext;

    private final SessionUtils sessionUtils;

    private final ValidationService validationService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody UpsertUserRequest request) {
        validationService.checkUpsertUserRequest(request);
        return userService.saveUser(request);
    }

    @PutMapping
    public UserResponse updateUser(@RequestBody UpsertUserRequest request) {
        String email = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return userService.updateUser(email, request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser() {
        String email = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        userService.deleteUser(email);
    }
}
