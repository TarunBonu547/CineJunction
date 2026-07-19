package com.cinejunction.genre.service;

import com.cinejunction.genre.dto.GenreRequest;
import com.cinejunction.genre.dto.GenreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing genres.
 */
public interface GenreService {

    /**
     * Creates a new genre.
     *
     * @param request the genre creation request
     * @return the created genre response
     */
    GenreResponse createGenre(GenreRequest request);

    /**
     * Retrieves all genres with pagination.
     *
     * @param pageable pagination information
     * @return a page of genre responses
     */
    Page<GenreResponse> getAllGenres(Pageable pageable);

    /**
     * Retrieves a genre by its ID.
     *
     * @param id the genre ID
     * @return the genre response
     */
    GenreResponse getGenreById(Long id);

    /**
     * Updates an existing genre by ID.
     *
     * @param id      the genre ID
     * @param request the genre update request
     * @return the updated genre response
     */
    GenreResponse updateGenre(Long id, GenreRequest request);

    /**
     * Deletes a genre by ID.
     *
     * @param id the genre ID
     */
    void deleteGenre(Long id);
}
