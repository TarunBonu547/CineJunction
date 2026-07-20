package com.cinejunction.tmdb.client;

import com.cinejunction.tmdb.dto.CreditsResponseDto;
import com.cinejunction.tmdb.dto.KeywordDto;
import com.cinejunction.tmdb.dto.KeywordsResponseDto;
import com.cinejunction.tmdb.dto.MovieDetailsDto;
import com.cinejunction.tmdb.exception.TMDbMovieNotFoundException;
import com.cinejunction.tmdb.exception.TMDbRateLimitException;
import com.cinejunction.tmdb.exception.TMDbServerException;
import com.cinejunction.tmdb.exception.TMDbUnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TMDbClient {

    private static final Logger log = LoggerFactory.getLogger(TMDbClient.class);
    private static final String MOVIE_PATH = "/movie/{movieId}";
    private static final String CREDITS_PATH = "/movie/{movieId}/credits";
    private static final String KEYWORDS_PATH = "/movie/{movieId}/keywords";
    private static final String API_KEY_PARAM = "api_key";

    private final RestClient restClient;
    private final String apiKey;

    public TMDbClient(RestClient restClient, String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    public MovieDetailsDto getMovieById(Long movieId) {
        log.info("Requesting TMDb movie with ID: {}", movieId);

        try {
            String uri = UriComponentsBuilder.fromPath(MOVIE_PATH)
                    .queryParam(API_KEY_PARAM, apiKey)
                    .buildAndExpand(movieId)
                    .toUriString();

            MovieDetailsDto movie = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(MovieDetailsDto.class);

            log.info("Successfully retrieved TMDb movie with ID: {}", movieId);
            return movie;

        } catch (RestClientResponseException ex) {
            HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
            String errorMessage = "TMDb request failed for movie ID " + movieId + ": " + ex.getMessage();
            log.error("TMDb request failure for movie ID: {}. Status: {}, Message: {}", movieId, ex.getStatusCode(), ex.getMessage());

            if (status != null) {
                switch (status) {
                    case NOT_FOUND -> throw new TMDbMovieNotFoundException("Movie not found on TMDb with ID: " + movieId);
                    case UNAUTHORIZED -> throw new TMDbUnauthorizedException("Invalid TMDb API key");
                    case TOO_MANY_REQUESTS -> throw new TMDbRateLimitException("TMDb rate limit exceeded for movie ID: " + movieId);
                    default -> throw new TMDbServerException(errorMessage);
                }
            }
            throw new TMDbServerException(errorMessage);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error calling TMDb for movie ID " + movieId + ": " + ex.getMessage();
            log.error("TMDb request failure for movie ID: {}. Error: {}", movieId, ex.getMessage());
            throw new TMDbServerException(errorMessage);
        }
    }

    public CreditsResponseDto getMovieCredits(Long movieId) {
        log.info("Requesting TMDb credits for movie ID: {}", movieId);

        try {
            String uri = UriComponentsBuilder.fromPath(CREDITS_PATH)
                    .queryParam(API_KEY_PARAM, apiKey)
                    .buildAndExpand(movieId)
                    .toUriString();

            CreditsResponseDto credits = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(CreditsResponseDto.class);

            log.info("Successfully retrieved TMDb credits for movie ID: {}", movieId);
            return credits;

        } catch (RestClientResponseException ex) {
            HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
            String errorMessage = "TMDb credits request failed for movie ID " + movieId + ": " + ex.getMessage();
            log.error("TMDb credits request failure for movie ID: {}. Status: {}, Message: {}", movieId, ex.getStatusCode(), ex.getMessage());

            if (status != null) {
                switch (status) {
                    case NOT_FOUND -> throw new TMDbMovieNotFoundException("Movie not found on TMDb with ID: " + movieId);
                    case UNAUTHORIZED -> throw new TMDbUnauthorizedException("Invalid TMDb API key");
                    case TOO_MANY_REQUESTS -> throw new TMDbRateLimitException("TMDb rate limit exceeded for movie ID: " + movieId);
                    default -> throw new TMDbServerException(errorMessage);
                }
            }
            throw new TMDbServerException(errorMessage);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error calling TMDb credits for movie ID " + movieId + ": " + ex.getMessage();
            log.error("TMDb credits request failure for movie ID: {}. Error: {}", movieId, ex.getMessage());
            throw new TMDbServerException(errorMessage);
        }
    }

    public List<KeywordDto> getMovieKeywords(Long movieId) {
        log.info("Requesting TMDb keywords for movie ID: {}", movieId);

        try {
            String uri = UriComponentsBuilder.fromPath(KEYWORDS_PATH)
                    .queryParam(API_KEY_PARAM, apiKey)
                    .buildAndExpand(movieId)
                    .toUriString();

            KeywordsResponseDto keywordsResponse = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(KeywordsResponseDto.class);

            if (keywordsResponse != null && keywordsResponse.getKeywords() != null) {
                log.info("Successfully retrieved {} keywords for TMDb movie ID: {}", keywordsResponse.getKeywords().size(), movieId);
                return keywordsResponse.getKeywords();
            }

            log.info("No keywords found for TMDb movie ID: {}", movieId);
            return List.of();

        } catch (RestClientResponseException ex) {
            HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
            String errorMessage = "TMDb keywords request failed for movie ID " + movieId + ": " + ex.getMessage();
            log.error("TMDb keywords request failure for movie ID: {}. Status: {}, Message: {}", movieId, ex.getStatusCode(), ex.getMessage());

            if (status != null) {
                switch (status) {
                    case NOT_FOUND -> throw new TMDbMovieNotFoundException("Movie not found on TMDb with ID: " + movieId);
                    case UNAUTHORIZED -> throw new TMDbUnauthorizedException("Invalid TMDb API key");
                    case TOO_MANY_REQUESTS -> throw new TMDbRateLimitException("TMDb rate limit exceeded for movie ID: " + movieId);
                    default -> throw new TMDbServerException(errorMessage);
                }
            }
            throw new TMDbServerException(errorMessage);
        } catch (Exception ex) {
            String errorMessage = "Unexpected error calling TMDb keywords for movie ID " + movieId + ": " + ex.getMessage();
            log.error("TMDb keywords request failure for movie ID: {}. Error: {}", movieId, ex.getMessage());
            throw new TMDbServerException(errorMessage);
        }
    }
}
