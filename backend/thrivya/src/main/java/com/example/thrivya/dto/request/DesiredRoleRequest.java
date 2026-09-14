package com.example.thrivya.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesiredRoleRequest {

    @NotBlank(message = "Desired role is required")
    private String desiredRole;
}