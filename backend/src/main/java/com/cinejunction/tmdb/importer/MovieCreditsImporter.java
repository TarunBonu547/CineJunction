package com.cinejunction.tmdb.importer;

import com.cinejunction.enums.Department;
import com.cinejunction.enums.Gender;
import com.cinejunction.exception.MovieNotFoundException;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.movieperson.entity.MoviePerson;
import com.cinejunction.movieperson.enums.RoleType;
import com.cinejunction.movieperson.repository.MoviePersonRepository;
import com.cinejunction.person.entity.Person;
import com.cinejunction.person.repository.PersonRepository;
import com.cinejunction.tmdb.client.TMDbClient;
import com.cinejunction.tmdb.dto.CastMemberDto;
import com.cinejunction.tmdb.dto.CreditsResponseDto;
import com.cinejunction.tmdb.dto.CrewMemberDto;
import com.cinejunction.tmdb.dto.ImportCreditsSummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MovieCreditsImporter {

    private static final Logger log = LoggerFactory.getLogger(MovieCreditsImporter.class);

    private final TMDbClient tmDbClient;
    private final MovieRepository movieRepository;
    private final PersonRepository personRepository;
    private final MoviePersonRepository moviePersonRepository;

    public MovieCreditsImporter(TMDbClient tmDbClient, MovieRepository movieRepository, PersonRepository personRepository, MoviePersonRepository moviePersonRepository) {
        this.tmDbClient = tmDbClient;
        this.movieRepository = movieRepository;
        this.personRepository = personRepository;
        this.moviePersonRepository = moviePersonRepository;
    }

    @Transactional
    public ImportCreditsSummaryResponse importCredits(Long tmdbMovieId) {
        log.info("Movie credits import started for TMDb movie ID: {}", tmdbMovieId);

        String title = tmDbClient.getMovieById(tmdbMovieId).getTitle();
        List<Movie> localMovies = movieRepository.findAllByTitleIgnoreCase(title);

        if (localMovies.isEmpty()) {
            throw new MovieNotFoundException("Movie not found in local database. Import the movie first.");
        }

        Movie movie = localMovies.get(0);
        log.info("Found local movie: {} (ID: {})", movie.getTitle(), movie.getId());

        CreditsResponseDto credits = tmDbClient.getMovieCredits(tmdbMovieId);
        log.info("Fetched {} cast members and {} crew members from TMDb for movie ID: {}",
                credits.getCast() != null ? credits.getCast().size() : 0,
                credits.getCrew() != null ? credits.getCrew().size() : 0,
                tmdbMovieId);

        int personsCreated = 0;
        int personsReused = 0;
        int relationshipsCreated = 0;
        int relationshipsSkipped = 0;

        Set<Person> allPersons = new HashSet<>();

        if (credits.getCast() != null) {
            for (CastMemberDto castMember : credits.getCast()) {
                Person person = findOrCreatePerson(castMember.getName(), castMember.getKnownForDepartment());
                allPersons.add(person);
                if (person.getId() == null) {
                    personsCreated++;
                } else {
                    personsReused++;
                }

                boolean created = createRelationshipIfNotExists(movie, person, RoleType.ACTOR, castMember.getCharacter(), null, castMember.getOrder());
                if (created) {
                    relationshipsCreated++;
                } else {
                    relationshipsSkipped++;
                }
            }
        }

        if (credits.getCrew() != null) {
            for (CrewMemberDto crewMember : credits.getCrew()) {
                RoleType roleType = mapJobToRoleType(crewMember.getJob());
                if (roleType == null) {
                    continue;
                }

                Person person = findOrCreatePerson(crewMember.getName(), crewMember.getDepartment());
                allPersons.add(person);
                if (person.getId() == null) {
                    personsCreated++;
                } else {
                    personsReused++;
                }

                boolean created = createRelationshipIfNotExists(movie, person, roleType, null, crewMember.getJob(), 0);
                if (created) {
                    relationshipsCreated++;
                } else {
                    relationshipsSkipped++;
                }
            }
        }

        log.info("Movie credits import completed for movie ID: {}. Persons created: {}, reused: {}, relationships created: {}, skipped: {}",
                movie.getId(), personsCreated, personsReused, relationshipsCreated, relationshipsSkipped);

        return ImportCreditsSummaryResponse.builder()
                .movieId(movie.getId())
                .movieTitle(movie.getTitle())
                .personsCreated(personsCreated)
                .personsReused(personsReused)
                .relationshipsCreated(relationshipsCreated)
                .relationshipsSkipped(relationshipsSkipped)
                .message("Movie credits imported successfully")
                .build();
    }

    private Person findOrCreatePerson(String name, String knownForDepartment) {
        String trimmedName = name != null ? name.trim() : null;
        if (trimmedName == null || trimmedName.isEmpty()) {
            return null;
        }

        return personRepository.findByNameIgnoreCase(trimmedName)
                .orElseGet(() -> {
                    Person person = new Person();
                    person.setName(trimmedName);
                    person.setGender(Gender.OTHER);
                    person.setDepartment(mapDepartment(knownForDepartment));
                    person.setAdult(false);
                    person.setPopularity(0.0);
                    person.setProfileImageUrl(null);
                    Person saved = personRepository.save(person);
                    log.info("Created new person: {}", saved.getName());
                    return saved;
                });
    }

    private boolean createRelationshipIfNotExists(Movie movie, Person person, RoleType roleType, String characterName, String creditedAs, Integer billingOrder) {
        if (person == null || movie == null || roleType == null) {
            return false;
        }

        if (moviePersonRepository.existsByMovieAndPersonAndRoleType(movie, person, roleType)) {
            return false;
        }

        MoviePerson moviePerson = new MoviePerson();
        moviePerson.setMovie(movie);
        moviePerson.setPerson(person);
        moviePerson.setRoleType(roleType);
        moviePerson.setCharacterName(characterName != null && !characterName.isEmpty() ? characterName : null);
        moviePerson.setCreditedAs(creditedAs != null && !creditedAs.isEmpty() ? creditedAs : null);
        moviePerson.setBillingOrder(billingOrder != null ? billingOrder : 0);

        moviePersonRepository.save(moviePerson);
        log.debug("Created relationship: Movie={}, Person={}, Role={}", movie.getId(), person.getId(), roleType);
        return true;
    }

    private Department mapDepartment(String departmentName) {
        if (departmentName == null || departmentName.isEmpty()) {
            return Department.ACTOR;
        }

        try {
            return Department.valueOf(departmentName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Department.ACTOR;
        }
    }

    private RoleType mapJobToRoleType(String job) {
        if (job == null || job.isEmpty()) {
            return null;
        }

        String upperJob = job.toUpperCase();
        if (upperJob.contains("DIRECTOR")) {
            return RoleType.DIRECTOR;
        } else if (upperJob.contains("SCREENPLAY") || upperJob.contains("STORY")) {
            return RoleType.WRITER;
        } else if (upperJob.contains("PRODUCER")) {
            return RoleType.PRODUCER;
        } else if (upperJob.contains("MUSIC") || upperJob.contains("COMPOSER")) {
            return RoleType.MUSIC;
        } else if (upperJob.contains("EDITOR")) {
            return RoleType.EDITOR;
        } else if (upperJob.contains("CINEMATOGRAPHER") || upperJob.contains("DIRECTOR OF PHOTOGRAPHY")) {
            return RoleType.CINEMATOGRAPHER;
        }

        return null;
    }
}
