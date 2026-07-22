package com.cinejunction.userlist.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WatchlistStatusUpdateRequest {
    private boolean watched;
}
