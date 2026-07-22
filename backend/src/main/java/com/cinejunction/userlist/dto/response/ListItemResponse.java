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
public class ListItemResponse {
    private Long id;
    private Long movieId;
    private String title;
    private String posterUrl;
    private Integer sortOrder;
}
