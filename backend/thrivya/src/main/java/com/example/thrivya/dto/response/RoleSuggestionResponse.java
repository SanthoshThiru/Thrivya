package com.example.thrivya.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleSuggestionResponse {

    private Long roleId;
    private String title;
    private String description;
    private Double relevanceScore;
    private Integer rankNumber;
    private Boolean selected;
}