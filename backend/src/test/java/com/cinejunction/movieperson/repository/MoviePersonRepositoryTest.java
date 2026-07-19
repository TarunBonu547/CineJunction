package com.cinejunction.movieperson.repository;

import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.enums.MovieStatus;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.movieperson.entity.MoviePerson;
import com.cinejunction.movieperson.enums.RoleType;
import com.cinejunction.person.entity.Person;
import com.cinejunction.person.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MoviePersonRepositoryTest {

    @Autowired
    private MoviePersonRepository moviePersonRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PersonRepository personRepository;

    @Test
    void saveMoviePerson_ReturnsSaved() {
        Movie movie = createMovie("Interstellar");
        Person person = createPerson("Christopher Nolan");
        MoviePerson moviePerson = createMoviePerson(movie, person, RoleType.DIRECTOR);
        MoviePerson saved = moviePersonRepository.save(moviePerson);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void existsByMovieAndPersonAndRoleType_ReturnsTrue() {
        Movie movie = createMovie("Interstellar");
        Person person = createPerson("Christopher Nolan");
        moviePersonRepository.save(createMoviePerson(movie, person, RoleType.DIRECTOR));

        boolean exists = moviePersonRepository.existsByMovieAndPersonAndRoleType(movie, person, RoleType.DIRECTOR);

        assertThat(exists).isTrue();
    }

    @Test
    void findByMovieId_ReturnsList() {
        Movie movie = createMovie("Interstellar");
        Person person1 = createPerson("Christopher Nolan");
        Person person2 = createPerson("Matthew McConaughey");
        moviePersonRepository.save(createMoviePerson(movie, person1, RoleType.DIRECTOR));
        moviePersonRepository.save(createMoviePerson(movie, person2, RoleType.ACTOR));

        List<MoviePerson> result = moviePersonRepository.findByMovieId(movie.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findByPersonId_ReturnsList() {
        Movie movie1 = createMovie("Interstellar");
        Movie movie2 = createMovie("Inception");
        Person person = createPerson("Christopher Nolan");
        moviePersonRepository.save(createMoviePerson(movie1, person, RoleType.DIRECTOR));
        moviePersonRepository.save(createMoviePerson(movie2, person, RoleType.WRITER));

        List<MoviePerson> result = moviePersonRepository.findByPersonId(person.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findByMovieIdAndRoleType_ReturnsList() {
        Movie movie = createMovie("Interstellar");
        Person person1 = createPerson("Christopher Nolan");
        Person person2 = createPerson("Matthew McConaughey");
        moviePersonRepository.save(createMoviePerson(movie, person1, RoleType.DIRECTOR));
        moviePersonRepository.save(createMoviePerson(movie, person2, RoleType.ACTOR));

        List<MoviePerson> result = moviePersonRepository.findByMovieIdAndRoleType(movie.getId(), RoleType.ACTOR);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoleType()).isEqualTo(RoleType.ACTOR);
    }

    @Test
    void deleteByMovieId_DeletesAll() {
        Movie movie = createMovie("Interstellar");
        Person person1 = createPerson("Christopher Nolan");
        Person person2 = createPerson("Matthew McConaughey");
        moviePersonRepository.save(createMoviePerson(movie, person1, RoleType.DIRECTOR));
        moviePersonRepository.save(createMoviePerson(movie, person2, RoleType.ACTOR));

        moviePersonRepository.deleteByMovieId(movie.getId());

        List<MoviePerson> remaining = moviePersonRepository.findByMovieId(movie.getId());
        assertThat(remaining).isEmpty();
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
        Instant now = Instant.now();
        movie.setCreatedAt(now);
        movie.setUpdatedAt(now);
        return movieRepository.save(movie);
    }

    private Person createPerson(String name) {
        Person person = new Person();
        person.setName(name);
        person.setBiography("Test biography");
        person.setBirthDate(java.time.LocalDate.of(1970, 1, 1));
        person.setNationality("American");
        person.setGender(com.cinejunction.enums.Gender.MALE);
        person.setDepartment(com.cinejunction.enums.Department.ACTOR);
        person.setPopularity(50.0);
        person.setAdult(false);
        java.time.Instant now = java.time.Instant.now();
        person.setCreatedAt(now);
        person.setUpdatedAt(now);
        return personRepository.save(person);
    }

    private MoviePerson createMoviePerson(Movie movie, Person person, RoleType roleType) {
        MoviePerson moviePerson = new MoviePerson();
        moviePerson.setMovie(movie);
        moviePerson.setPerson(person);
        moviePerson.setRoleType(roleType);
        moviePerson.setBillingOrder(0);
        moviePerson.setCreatedAt(java.time.Instant.now());
        moviePerson.setUpdatedAt(java.time.Instant.now());
        return moviePerson;
    }
}
