package com.example.thrivya.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceResponse {

    private Long id;

    private String skillName;

    private String type;

    private String title;

    private String url;

    private String provider;

    private String description;

    private String difficulty;

    private Integer estimatedMinutes;

    private Boolean verified;

    private String source;
}