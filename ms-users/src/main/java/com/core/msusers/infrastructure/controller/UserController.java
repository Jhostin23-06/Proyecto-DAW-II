package com.core.msusers.infrastructure.controller;

import com.core.msusers.application.port.usecase.CreateUserPort;
import com.core.msusers.application.port.usecase.DeleteUserPort;
import com.core.msusers.application.port.usecase.GetUserPort;
import com.core.msusers.application.port.usecase.UpdateUserPort;
import com.core.msusers.domain.bean.UserRequest;
import com.core.msusers.domain.bean.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserPort createUserPort;
    private final GetUserPort getUserPort;
    private final UpdateUserPort updateUserPort;
    private final DeleteUserPort deleteUserPort;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        // Asegurar que el rol sea USER (evitar que alguien se auto-asigne ADMIN)
        request.setUserRole("ADMIN");
        UserResponse response = createUserPort.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = createUserPort.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        UserResponse response = getUserPort.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse response = getUserPort.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) String role) {
        List<UserResponse> responses;
        if (role != null) {
            responses = getUserPort.getUsersByRole(role);
        } else {
            responses = getUserPort.getAllUsers();
        }
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserRequest request) {
        UserResponse response = updateUserPort.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        deleteUserPort.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
