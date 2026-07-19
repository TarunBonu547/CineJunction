package com.cinejunction.review.repository;

import com.cinejunction.entity.User;
import com.cinejunction.enums.Role;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.enums.MovieStatus;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.review.entity.MovieReview;
import com.cinejunction.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MovieReviewRepositoryTest {

    @Autowired
    private MovieReviewRepository movieReviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveReview_ReturnsSaved() {
        Movie movie = createMovie("Inception");
        User user = createUser("test@example.com");
        MovieReview movieReview = createReview(movie, user, "Great movie", "This is a great movie with amazing plot twists.", false);
        MovieReview saved = movieReviewRepository.save(movieReview);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void existsByMovieAndUser_ReturnsTrue() {
        Movie movie = createMovie("Inception");
        User user = createUser("test@example.com");
        movieReviewRepository.save(createReview(movie, user, "Great movie", "This is a great movie with amazing plot twists.", false));

        boolean exists = movieReviewRepository.existsByMovieAndUser(movie, user);

        assertThat(exists).isTrue();
    }

    @Test
    void findByMovieId_ReturnsList() {
        Movie movie = createMovie("Inception");
        User user1 = createUser("test1@example.com");
        User user2 = createUser("test2@example.com");
        movieReviewRepository.save(createReview(movie, user1, "Great movie", "This is a great movie with amazing plot twists.", false));
        movieReviewRepository.save(createReview(movie, user2, "Awesome", "Another great review with lots of details about the movie.", true));

        List<MovieReview> result = movieReviewRepository.findByMovieId(movie.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findByUserId_ReturnsList() {
        Movie movie1 = createMovie("Inception");
        Movie movie2 = createMovie("Interstellar");
        User user = createUser("test@example.com");
        movieReviewRepository.save(createReview(movie1, user, "Great movie", "This is a great movie with amazing plot twists.", false));
        movieReviewRepository.save(createReview(movie2, user, "Awesome", "Another great review with lots of details about the movie.", true));

        List<MovieReview> result = movieReviewRepository.findByUserId(user.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findByMovieIdAndUserId_ReturnsReview() {
        Movie movie = createMovie("Inception");
        User user = createUser("test@example.com");
        movieReviewRepository.save(createReview(movie, user, "Great movie", "This is a great movie with amazing plot twists.", false));

        Optional<MovieReview> result = movieReviewRepository.findByMovieIdAndUserId(movie.getId(), user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Great movie");
    }

    @Test
    void deleteByMovieIdAndUserId_DeletesReview() {
        Movie movie = createMovie("Inception");
        User user = createUser("test@example.com");
        movieReviewRepository.save(createReview(movie, user, "Great movie", "This is a great movie with amazing plot twists.", false));

        movieReviewRepository.deleteByMovieIdAndUserId(movie.getId(), user.getId());

        Optional<MovieReview> result = movieReviewRepository.findByMovieIdAndUserId(movie.getId(), user.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void countByMovie_ReturnsCount() {
        Movie movie = createMovie("Inception");
        User user1 = createUser("test1@example.com");
        User user2 = createUser("test2@example.com");
        movieReviewRepository.save(createReview(movie, user1, "Great movie", "This is a great movie with amazing plot twists.", false));
        movieReviewRepository.save(createReview(movie, user2, "Awesome", "Another great review with lots of details about the movie.", true));

        long count = movieReviewRepository.countByMovie(movie);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void findByMovieIdAndContainsSpoilersTrue_ReturnsSpoilerReviews() {
        Movie movie = createMovie("Inception");
        User user1 = createUser("test1@example.com");
        User user2 = createUser("test2@example.com");
        movieReviewRepository.save(createReview(movie, user1, "Great movie", "This is a great movie with amazing plot twists.", false));
        movieReviewRepository.save(createReview(movie, user2, "Awesome", "Another great review with lots of details about the movie.", true));

        List<MovieReview> spoilerReviews = movieReviewRepository.findByMovieIdAndContainsSpoilersTrue(movie.getId());

        assertThat(spoilerReviews).hasSize(1);
        assertThat(spoilerReviews.get(0).getContainsSpoilers()).isTrue();
    }

    @Test
    void searchReviews_ReturnsMatchingReviews() {
        Movie movie = createMovie("Inception");
        User user = createUser("test@example.com");
        movieReviewRepository.save(createReview(movie, user, "Great movie", "This is a great movie with amazing plot twists.", false));

        List<MovieReview> result = movieReviewRepository.searchReviews("amazing", Pageable.unpaged()).getContent();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Great movie");
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

    private MovieReview createReview(Movie movie, User user, String title, String reviewText, Boolean containsSpoilers) {
        MovieReview movieReview = new MovieReview();
        movieReview.setMovie(movie);
        movieReview.setUser(user);
        movieReview.setTitle(title);
        movieReview.setReviewText(reviewText);
        movieReview.setContainsSpoilers(containsSpoilers);
        movieReview.setCreatedAt(Instant.now());
        movieReview.setUpdatedAt(Instant.now());
        return movieReview;
    }
}
