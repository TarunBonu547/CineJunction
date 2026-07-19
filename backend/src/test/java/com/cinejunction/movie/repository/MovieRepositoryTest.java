package com.cinejunction.movie.repository;

import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.enums.MovieStatus;
import com.cinejunction.movie.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    private Movie createMovie(String title) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setLanguage("English");
        movie.setStatus(MovieStatus.RELEASED);
        movie.setAdult(false);
        movie.setRuntime(148);
        movie.setBudget(160000000L);
        movie.setRevenue(829895144L);
        movie.setAverageRating(new BigDecimal("8.8"));
        movie.setVoteCount(20000);
        movie.setPopularity(new BigDecimal("85.5"));
        Instant now = Instant.now();
        movie.setCreatedAt(now);
        movie.setUpdatedAt(now);
        return movie;
    }

    @Test
    void findByTitleIgnoreCase_ReturnsMovie() {
        Movie movie = createMovie("Inception");
        movieRepository.save(movie);

        Optional<Movie> found = movieRepository.findByTitleIgnoreCase("Inception");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Inception");
    }

    @Test
    void existsByTitleIgnoreCase_ReturnsTrue() {
        Movie movie = createMovie("Inception");
        movieRepository.save(movie);

        boolean exists = movieRepository.existsByTitleIgnoreCase("Inception");

        assertThat(exists).isTrue();
    }

    @Test
    void findByTitleContainingIgnoreCase_ReturnsPage() {
        Movie movie1 = createMovie("Inception");
        Movie movie2 = createMovie("Inception 2");
        movie2.setRuntime(150);
        movie2.setBudget(200000000L);
        movie2.setRevenue(900000000L);
        movie2.setAverageRating(new BigDecimal("7.5"));
        movie2.setVoteCount(15000);
        movie2.setPopularity(new BigDecimal("70.0"));

        movieRepository.save(movie1);
        movieRepository.save(movie2);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<Movie> result = movieRepository.findByTitleContainingIgnoreCase("Inception", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
    }
}
