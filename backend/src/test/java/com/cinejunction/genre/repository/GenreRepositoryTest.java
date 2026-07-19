package com.cinejunction.genre.repository;

import com.cinejunction.genre.entity.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void saveGenre_ReturnsSavedGenre() {
        Genre genre = new Genre();
        genre.setName("Action");

        Genre saved = genreRepository.save(genre);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Action");
    }

    @Test
    void findByNameIgnoreCase_ReturnsGenre() {
        Genre genre = new Genre();
        genre.setName("Action");
        genreRepository.save(genre);

        Optional<Genre> found = genreRepository.findByNameIgnoreCase("Action");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Action");
    }

    @Test
    void existsByNameIgnoreCase_ReturnsTrue() {
        Genre genre = new Genre();
        genre.setName("Action");
        genreRepository.save(genre);

        boolean exists = genreRepository.existsByNameIgnoreCase("Action");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_ReturnsFalse() {
        boolean exists = genreRepository.existsByNameIgnoreCase("NonExistent");

        assertThat(exists).isFalse();
    }
}
