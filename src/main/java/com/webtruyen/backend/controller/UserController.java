package com.webtruyen.backend.controller;

import com.webtruyen.backend.dto.UserUpdateRequest;
import com.webtruyen.backend.model.User;
import com.webtruyen.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.webtruyen.backend.dto.PagedResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/admin/users")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<User>> getAllUsers(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id").descending());
        Page<User> userPage;
        
        if (q != null && !q.isEmpty()) {
            userPage = userRepository.findByNameContainingIgnoreCase(q, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        
        PagedResponse<User> response = new PagedResponse<>(
                userPage.getContent(),
                userPage.getTotalElements(),
                page,
                pageSize
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest updates) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Handle Password Change
        if (updates.getPassword() != null && !updates.getPassword().isEmpty()) {
            if (updates.getOldPassword() == null || updates.getOldPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Old password is required to change password");
            }
            if (!passwordEncoder.matches(updates.getOldPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body("Incorrect old password");
            }
            user.setPassword(passwordEncoder.encode(updates.getPassword()));
        }

        if (updates.getName() != null) user.setName(updates.getName());
        if (updates.getRole() != null) user.setRole(updates.getRole());
        if (updates.getEmail() != null) user.setEmail(updates.getEmail());
        
        return ResponseEntity.ok(userRepository.save(user));
    }

    @DeleteMapping("/admin/users/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
