package com.cinejunction.userlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomListResponse {
    private Long id;
    private String name;
    private String description;
    private boolean isPublic;
    private String coverImage;
    private Long userId;
    private int movieCount;
}
