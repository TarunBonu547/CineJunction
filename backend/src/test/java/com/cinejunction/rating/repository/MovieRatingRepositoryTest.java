package com.cinejunction.rating.repository;

import com.cinejunction.entity.User;
import com.cinejunction.enums.Role;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.enums.MovieStatus;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.rating.entity.MovieRating;
import com.cinejunction.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MovieRatingRepositoryTest {

    @Autowired
    private MovieRatingRepository movieRatingRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveMovieRating_ReturnsSaved() {
        Movie movie = createMovie("Interstellar");
        User user = createUser("test@example.com");
        MovieRating movieRating = createMovieRating(movie, user, 8);
        MovieRating saved = movieRatingRepository.save(movieRating);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void existsByMovieAndUser_ReturnsTrue() {
        Movie movie = createMovie("Interstellar");
        User user = createUser("test@example.com");
        movieRatingRepository.save(createMovieRating(movie, user, 8));

        boolean exists = movieRatingRepository.existsByMovieAndUser(movie, user);

        assertThat(exists).isTrue();
    }

    @Test
    void findByMovieId_ReturnsList() {
        Movie movie = createMovie("Interstellar");
        User user1 = createUser("test1@example.com");
        User user2 = createUser("test2@example.com");
        movieRatingRepository.save(createMovieRating(movie, user1, 8));
        movieRatingRepository.save(createMovieRating(movie, user2, 9));

        List<MovieRating> result = movieRatingRepository.findByMovieId(movie.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findByUserId_ReturnsList() {
        Movie movie1 = createMovie("Interstellar");
        Movie movie2 = createMovie("Inception");
        User user = createUser("test@example.com");
        movieRatingRepository.save(createMovieRating(movie1, user, 8));
        movieRatingRepository.save(createMovieRating(movie2, user, 9));

        List<MovieRating> result = movieRatingRepository.findByUserId(user.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findByMovieIdAndUserId_ReturnsRating() {
        Movie movie = createMovie("Interstellar");
        User user = createUser("test@example.com");
        movieRatingRepository.save(createMovieRating(movie, user, 8));

        Optional<MovieRating> result = movieRatingRepository.findByMovieIdAndUserId(movie.getId(), user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getRating()).isEqualTo(8);
    }

    @Test
    void deleteByMovieIdAndUserId_DeletesRating() {
        Movie movie = createMovie("Interstellar");
        User user = createUser("test@example.com");
        movieRatingRepository.save(createMovieRating(movie, user, 8));

        movieRatingRepository.deleteByMovieIdAndUserId(movie.getId(), user.getId());

        Optional<MovieRating> result = movieRatingRepository.findByMovieIdAndUserId(movie.getId(), user.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void countByMovie_ReturnsCount() {
        Movie movie = createMovie("Interstellar");
        User user1 = createUser("test1@example.com");
        User user2 = createUser("test2@example.com");
        movieRatingRepository.save(createMovieRating(movie, user1, 8));
        movieRatingRepository.save(createMovieRating(movie, user2, 9));

        long count = movieRatingRepository.countByMovie(movie);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void findAverageRatingByMovieId_ReturnsAverage() {
        Movie movie = createMovie("Interstellar");
        User user1 = createUser("test1@example.com");
        User user2 = createUser("test2@example.com");
        movieRatingRepository.save(createMovieRating(movie, user1, 8));
        movieRatingRepository.save(createMovieRating(movie, user2, 10));

        Double average = movieRatingRepository.findAverageRatingByMovieId(movie.getId());

        assertThat(average).isEqualTo(9.0);
    }

    @Test
    void findRatingDistributionByMovieId_ReturnsDistribution() {
        Movie movie = createMovie("Interstellar");
        User user1 = createUser("test1@example.com");
        User user2 = createUser("test2@example.com");
        User user3 = createUser("test3@example.com");
        movieRatingRepository.save(createMovieRating(movie, user1, 1));
        movieRatingRepository.save(createMovieRating(movie, user2, 5));
        movieRatingRepository.save(createMovieRating(movie, user3, 10));

        List<Object[]> distribution = movieRatingRepository.findRatingDistributionByMovieId(movie.getId());

        assertThat(distribution).hasSize(3);
        Map<Integer, Long> distributionMap = distribution.stream()
                .collect(java.util.stream.Collectors.toMap(
                        result -> ((Number) result[0]).intValue(),
                        result -> ((Number) result[1]).longValue()
                ));
        assertThat(distributionMap.get(1)).isEqualTo(1L);
        assertThat(distributionMap.get(5)).isEqualTo(1L);
        assertThat(distributionMap.get(10)).isEqualTo(1L);
    }

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
        movie.setCreatedAt(Instant.now());
        movie.setUpdatedAt(Instant.now());
        return movieRepository.save(movie);
    }

    private User createUser(String email) {
        User user = new User();
        user.setFullName("Test User " + email);
        user.setUsername("testuser_" + email.replaceAll("[^a-zA-Z0-9]", ""));
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setUpdatedAt(java.time.LocalDateTime.now());
        return userRepository.save(user);
    }

    private MovieRating createMovieRating(Movie movie, User user, Integer rating) {
        MovieRating movieRating = new MovieRating();
        movieRating.setMovie(movie);
        movieRating.setUser(user);
        movieRating.setRating(rating);
        movieRating.setCreatedAt(Instant.now());
        movieRating.setUpdatedAt(Instant.now());
        return movieRating;
    }
}
