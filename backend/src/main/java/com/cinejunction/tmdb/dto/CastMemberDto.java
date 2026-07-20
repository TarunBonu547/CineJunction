package com.cinejunction.tmdb.dto;

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
public class CastMemberDto {

    private Long id;
    private String name;
    private String character;
    private Integer order;
    private String profilePath;
    private String knownForDepartment;
}
