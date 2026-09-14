package com.example.thrivya.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectedRoleResponse {

    private Long profileId;
    private Long roleId;
    private String roleTitle;
    private String message;
}