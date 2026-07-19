package com.cinejunction.movieperson.service;

import com.cinejunction.exception.MovieNotFoundException;
import com.cinejunction.exception.PersonNotFoundException;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.movieperson.dto.MoviePersonRequest;
import com.cinejunction.movieperson.dto.MoviePersonResponse;
import com.cinejunction.movieperson.dto.MoviePersonSummaryResponse;
import com.cinejunction.movieperson.entity.MoviePerson;
import com.cinejunction.movieperson.enums.RoleType;
import com.cinejunction.movieperson.exception.MoviePersonAlreadyExistsException;
import com.cinejunction.movieperson.exception.MoviePersonNotFoundException;
import com.cinejunction.movieperson.mapper.MoviePersonMapper;
import com.cinejunction.movieperson.repository.MoviePersonRepository;
import com.cinejunction.movieperson.service.impl.MoviePersonServiceImpl;
import com.cinejunction.person.entity.Person;
import com.cinejunction.person.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoviePersonServiceTest {

    @Mock
    private MoviePersonRepository moviePersonRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private MoviePersonMapper moviePersonMapper;

    @InjectMocks
    private MoviePersonServiceImpl moviePersonService;

    private Movie movie;
    private Person person;
    private MoviePerson moviePerson;
    private MoviePersonRequest request;
    private MoviePersonResponse response;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Interstellar");

        person = new Person();
        person.setId(2L);
        person.setName("Christopher Nolan");
        person.setDepartment(com.cinejunction.enums.Department.DIRECTOR);

        moviePerson = new MoviePerson();
        moviePerson.setId(1L);
        moviePerson.setMovie(movie);
        moviePerson.setPerson(person);
        moviePerson.setRoleType(RoleType.DIRECTOR);
        moviePerson.setBillingOrder(0);

        request = new MoviePersonRequest();
        request.setMovieId(1L);
        request.setPersonId(2L);
        request.setRoleType(RoleType.DIRECTOR);
        request.setBillingOrder(0);

        response = new MoviePersonResponse();
        response.setRelationshipId(1L);
        response.setMovieId(1L);
        response.setMovieTitle("Interstellar");
        response.setPersonId(2L);
        response.setPersonName("Christopher Nolan");
        response.setDepartment("Director");
        response.setRoleType(RoleType.DIRECTOR);
        response.setBillingOrder(0);
    }

    @Test
    void assignPersonToMovie_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(personRepository.findById(2L)).thenReturn(Optional.of(person));
        when(moviePersonRepository.existsByMovieAndPersonAndRoleType(any(), any(), any())).thenReturn(false);
        when(moviePersonMapper.toEntity(any())).thenReturn(moviePerson);
        when(moviePersonRepository.save(any())).thenReturn(moviePerson);

        MoviePersonResponse result = moviePersonService.assignPersonToMovie(request);

        assertThat(result).isNotNull();
        assertThat(result.getMovieTitle()).isEqualTo("Interstellar");
        verify(moviePersonRepository).save(any());
    }

    @Test
    void assignPersonToMovie_MovieNotFound_ThrowsException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> moviePersonService.assignPersonToMovie(request));
    }

    @Test
    void assignPersonToMovie_PersonNotFound_ThrowsException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(personRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> moviePersonService.assignPersonToMovie(request));
    }

    @Test
    void assignPersonToMovie_Duplicate_ThrowsException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(personRepository.findById(2L)).thenReturn(Optional.of(person));
        when(moviePersonRepository.existsByMovieAndPersonAndRoleType(any(), any(), any())).thenReturn(true);

        assertThrows(MoviePersonAlreadyExistsException.class, () -> moviePersonService.assignPersonToMovie(request));
    }

    @Test
    void updateRelationship_Success() {
        when(moviePersonRepository.findById(1L)).thenReturn(Optional.of(moviePerson));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(personRepository.findById(2L)).thenReturn(Optional.of(person));
        when(moviePersonRepository.existsByMovieAndPersonAndRoleType(any(), any(), any())).thenReturn(false);
        when(moviePersonRepository.save(any())).thenReturn(moviePerson);

        MoviePersonResponse result = moviePersonService.updateRelationship(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getRelationshipId()).isEqualTo(1L);
    }

    @Test
    void deleteRelationship_Success() {
        when(moviePersonRepository.findById(1L)).thenReturn(Optional.of(moviePerson));

        moviePersonService.deleteRelationship(1L);

        verify(moviePersonRepository).delete(moviePerson);
    }

    @Test
    void getMovieCast_ReturnsActors() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(moviePersonRepository.findByMovieIdAndRoleType(1L, RoleType.ACTOR)).thenReturn(List.of(moviePerson));
        when(moviePersonMapper.toSummary(any())).thenReturn(new MoviePersonSummaryResponse());

        List<MoviePersonSummaryResponse> cast = moviePersonService.getMovieCast(1L);

        assertThat(cast).hasSize(1);
    }

    @Test
    void getMovieCrew_ReturnsNonActors() {
        moviePerson.setRoleType(RoleType.DIRECTOR);
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(moviePersonRepository.findByMovieId(1L)).thenReturn(List.of(moviePerson));
        when(moviePersonMapper.toSummary(any())).thenReturn(new MoviePersonSummaryResponse());

        List<MoviePersonSummaryResponse> crew = moviePersonService.getMovieCrew(1L);

        assertThat(crew).hasSize(1);
    }

    @Test
    void getPersonFilmography_ReturnsPage() {
        when(personRepository.existsById(2L)).thenReturn(true);
        when(moviePersonRepository.findByPersonId(2L)).thenReturn(List.of(moviePerson));
        when(moviePersonMapper.toSummary(any())).thenReturn(new MoviePersonSummaryResponse());

        Page<MoviePersonSummaryResponse> filmography = moviePersonService.getPersonFilmography(2L, Pageable.unpaged());

        assertThat(filmography).isNotNull();
        assertThat(filmography.getContent()).hasSize(1);
    }

    @Test
    void assignPersonToMovie_ActorWithoutCharacter_ThrowsException() {
        request.setRoleType(RoleType.ACTOR);
        request.setCharacterName(null);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(personRepository.findById(2L)).thenReturn(Optional.of(person));

        assertThrows(IllegalArgumentException.class, () -> moviePersonService.assignPersonToMovie(request));
    }
}
