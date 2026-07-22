package com.cinejunction.userlist.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReorderMoviesRequest {

    @NotNull(message = "Movie IDs list is required")
    @Size(min = 1, message = "Movie IDs list must not be empty")
    private List<Long> movieIds;
}
