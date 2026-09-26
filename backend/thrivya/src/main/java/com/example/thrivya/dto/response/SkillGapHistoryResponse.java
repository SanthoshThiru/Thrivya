package com.example.thrivya.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapHistoryResponse {

    private String roleTitle;
    private List<SkillGapHistoryItemResponse> history;
}