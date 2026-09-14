package com.example.thrivya.controller;

import com.example.thrivya.dto.request.DesiredRoleRequest;
import com.example.thrivya.dto.request.RoleSelectionRequest;
import com.example.thrivya.dto.response.RoleSuggestionResponse;
import com.example.thrivya.dto.response.SelectedRoleResponse;
import com.example.thrivya.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/suggestions")
    public ResponseEntity<List<RoleSuggestionResponse>>
    suggestRoles(Authentication authentication) {
        return ResponseEntity.ok(
                roleService.suggestRoles(
                        authentication.getName()
                )
        );
    }

    @PostMapping("/desired")
    public ResponseEntity<List<RoleSuggestionResponse>>
    searchDesiredRole(
            Authentication authentication,
            @Valid @RequestBody DesiredRoleRequest request
    ) {
        return ResponseEntity.ok(
                roleService.searchDesiredRole(
                        authentication.getName(),
                        request.getDesiredRole()
                )
        );
    }

    @PostMapping("/select")
    public ResponseEntity<SelectedRoleResponse>
    selectRole(
            Authentication authentication,
            @Valid @RequestBody RoleSelectionRequest request
    ) {
        return ResponseEntity.ok(
                roleService.selectRole(
                        authentication.getName(),
                        request.getRoleId()
                )
        );
    }
}