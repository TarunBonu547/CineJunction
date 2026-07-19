package com.cinejunction.genre.service.impl;

import com.cinejunction.exception.GenreAlreadyExistsException;
import com.cinejunction.exception.GenreNotFoundException;
import com.cinejunction.genre.dto.GenreRequest;
import com.cinejunction.genre.dto.GenreResponse;
import com.cinejunction.genre.entity.Genre;
import com.cinejunction.genre.mapper.GenreMapper;
import com.cinejunction.genre.repository.GenreRepository;
import com.cinejunction.genre.service.GenreService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for managing genres.
 */
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    @Transactional
    public GenreResponse createGenre(GenreRequest request) {
        String trimmedName = request.getName().trim();
        if (genreRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new GenreAlreadyExistsException("Genre already exists with name: " + trimmedName);
        }

        Genre genre = genreMapper.toEntity(request);
        genre.setName(trimmedName);
        genreRepository.save(genre);

        return genreMapper.toResponse(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenreResponse> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable).map(genreMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponse getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found with id: " + id));
        return genreMapper.toResponse(genre);
    }

    @Override
    @Transactional
    public GenreResponse updateGenre(Long id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found with id: " + id));

        String trimmedName = request.getName().trim();
        genreRepository.findByNameIgnoreCase(trimmedName).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new GenreAlreadyExistsException("Genre already exists with name: " + trimmedName);
            }
        });

        genre.setName(trimmedName);
        genreRepository.save(genre);

        return genreMapper.toResponse(genre);
    }

    @Override
    @Transactional
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found with id: " + id));

        if (genre.getMovies() != null && !genre.getMovies().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete genre because it is referenced by one or more movies");
        }

        genreRepository.delete(genre);
    }
}
