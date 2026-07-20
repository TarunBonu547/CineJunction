package com.cinejunction.tmdb.importer;

import com.cinejunction.genre.entity.Genre;
import com.cinejunction.genre.repository.GenreRepository;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.enums.MovieStatus;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.tmdb.client.TMDbClient;
import com.cinejunction.tmdb.dto.GenreDto;
import com.cinejunction.tmdb.dto.MovieDetailsDto;
import com.cinejunction.exception.MovieAlreadyExistsException;
import com.cinejunction.exception.MovieNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MovieImporter {

    private static final Logger log = LoggerFactory.getLogger(MovieImporter.class);

    private final TMDbClient tmDbClient;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    public MovieImporter(TMDbClient tmDbClient, MovieRepository movieRepository, GenreRepository genreRepository) {
        this.tmDbClient = tmDbClient;
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }

    @Transactional
    public Movie importMovie(Long tmdbMovieId) {
        log.info("Movie import started for TMDb movie ID: {}", tmdbMovieId);

        MovieDetailsDto tmdbMovie = tmDbClient.getMovieById(tmdbMovieId);
        log.info("Movie fetched from TMDb for movie ID: {}", tmdbMovieId);

        if (isMovieAlreadyImported(tmdbMovie)) {
            throw new MovieAlreadyExistsException("Movie already imported: " + tmdbMovie.getTitle());
        }

        Movie movie = mapToEntity(tmdbMovie);
        log.info("Movie mapped for TMDb movie ID: {}", tmdbMovieId);

        Movie saved = movieRepository.save(movie);
        log.info("Movie saved with ID: {}", saved.getId());

        return saved;
    }

    private boolean isMovieAlreadyImported(MovieDetailsDto tmdbMovie) {
        String title = tmdbMovie.getTitle();
        LocalDate releaseDate = parseReleaseDate(tmdbMovie.getRelease_date());

        List<Movie> existingMovies = movieRepository.findAllByTitleIgnoreCase(title);
        if (existingMovies.isEmpty()) {
            return false;
        }

        if (releaseDate != null) {
            return existingMovies.stream()
                    .anyMatch(movie -> movie.getReleaseDate() != null && movie.getReleaseDate().equals(releaseDate));
        }
        return true;
    }

    private Movie mapToEntity(MovieDetailsDto tmdbMovie) {
        Movie movie = new Movie();
        movie.setTitle(tmdbMovie.getTitle());
        movie.setOverview(tmdbMovie.getOverview());
        movie.setReleaseDate(parseReleaseDate(tmdbMovie.getRelease_date()));
        movie.setRuntime(tmdbMovie.getRuntime());
        movie.setLanguage(tmdbMovie.getOriginal_language() != null ? tmdbMovie.getOriginal_language() : "en");
        movie.setPosterUrl(buildPosterUrl(tmdbMovie.getPoster_path()));
        movie.setBackdropUrl(buildBackdropUrl(tmdbMovie.getBackdrop_path()));
        movie.setStatus(MovieStatus.RELEASED);
        movie.setAdult(tmdbMovie.getAdult() != null && tmdbMovie.getAdult());
        movie.setBudget(tmdbMovie.getBudget() != null ? tmdbMovie.getBudget() : 0L);
        movie.setRevenue(tmdbMovie.getRevenue() != null ? tmdbMovie.getRevenue() : 0L);
        movie.setAverageRating(tmdbMovie.getVote_average() != null ? BigDecimal.valueOf(tmdbMovie.getVote_average()) : BigDecimal.ZERO);
        movie.setVoteCount(tmdbMovie.getVote_count() != null ? tmdbMovie.getVote_count() : 0);
        movie.setPopularity(tmdbMovie.getPopularity() != null ? BigDecimal.valueOf(tmdbMovie.getPopularity()) : BigDecimal.ZERO);
        movie.setGenres(mapGenres(tmdbMovie.getGenres()));
        return movie;
    }

    private Set<Genre> mapGenres(java.util.List<GenreDto> genreDtos) {
        Set<Genre> genres = new HashSet<>();
        if (genreDtos != null) {
            for (GenreDto genreDto : genreDtos) {
                Genre genre = genreRepository.findByNameIgnoreCase(genreDto.getName())
                        .orElseGet(() -> {
                            Genre newGenre = new Genre();
                            newGenre.setName(genreDto.getName());
                            return genreRepository.save(newGenre);
                        });
                genres.add(genre);
            }
        }
        return genres;
    }

    private LocalDate parseReleaseDate(String releaseDate) {
        if (releaseDate == null || releaseDate.isEmpty()) {
            return null;
        }
        return LocalDate.parse(releaseDate);
    }

    private String buildPosterUrl(String posterPath) {
        if (posterPath == null || posterPath.isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }

    private String buildBackdropUrl(String backdropPath) {
        if (backdropPath == null || backdropPath.isEmpty()) {
            return null;
        }
        return "https://image.tmdb.org/t/p/original" + backdropPath;
    }
}
