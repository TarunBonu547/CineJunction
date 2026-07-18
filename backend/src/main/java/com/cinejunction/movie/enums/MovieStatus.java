package com.cinejunction.movie.enums;

/**
 * Represents the production and release status of a movie.
 */
public enum MovieStatus {

    /**
     * Movie has been announced but production has not started.
     */
    UPCOMING,

    /**
     * Movie is currently being filmed.
     */
    IN_PRODUCTION,

    /**
     * Filming is complete and the movie is in post-production (editing, VFX, sound, etc.).
     */
    POST_PRODUCTION,

    /**
     * Movie has been released to the public.
     */
    RELEASED,

    /**
     * Movie production has been cancelled and will not be released.
     */
    CANCELLED
}
