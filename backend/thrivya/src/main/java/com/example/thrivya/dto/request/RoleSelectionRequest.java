package com.example.thrivya.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleSelectionRequest {

    @NotNull(message = "Role ID is required")
    private Long roleId;
}