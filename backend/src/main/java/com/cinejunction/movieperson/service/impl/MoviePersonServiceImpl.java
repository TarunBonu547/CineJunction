package com.cinejunction.movieperson.service.impl;

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
import com.cinejunction.movieperson.service.MoviePersonService;
import com.cinejunction.person.entity.Person;
import com.cinejunction.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoviePersonServiceImpl implements MoviePersonService {

    private final MoviePersonRepository moviePersonRepository;
    private final MovieRepository movieRepository;
    private final PersonRepository personRepository;
    private final MoviePersonMapper moviePersonMapper;

    @Override
    @Transactional
    public MoviePersonResponse assignPersonToMovie(MoviePersonRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + request.getMovieId()));

        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + request.getPersonId()));

        validateActorRequiresCharacter(request.getRoleType(), request.getCharacterName());

        boolean exists = moviePersonRepository.existsByMovieAndPersonAndRoleType(movie, person, request.getRoleType());
        if (exists) {
            throw new MoviePersonAlreadyExistsException(
                    "Relationship already exists between movie id: " + request.getMovieId() +
                            " and person id: " + request.getPersonId() + " with role: " + request.getRoleType()
            );
        }

        MoviePerson moviePerson = moviePersonMapper.toEntity(request);
        moviePerson.setMovie(movie);
        moviePerson.setPerson(person);
        moviePersonRepository.save(moviePerson);

        return buildMoviePersonResponse(moviePerson);
    }

    @Override
    @Transactional
    public MoviePersonResponse updateRelationship(Long id, MoviePersonRequest request) {
        MoviePerson moviePerson = moviePersonRepository.findById(id)
                .orElseThrow(() -> new MoviePersonNotFoundException("Relationship not found with id: " + id));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + request.getMovieId()));

        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + request.getPersonId()));

        validateActorRequiresCharacter(request.getRoleType(), request.getCharacterName());

        boolean exists = moviePersonRepository.existsByMovieAndPersonAndRoleType(movie, person, request.getRoleType());
        if (exists && !exists) {
            throw new MoviePersonAlreadyExistsException(
                    "Relationship already exists between movie id: " + request.getMovieId() +
                            " and person id: " + request.getPersonId() + " with role: " + request.getRoleType()
            );
        }

        moviePerson.setMovie(movie);
        moviePerson.setPerson(person);
        moviePerson.setRoleType(request.getRoleType());
        moviePerson.setCharacterName(request.getCharacterName());
        moviePerson.setCreditedAs(request.getCreditedAs());
        moviePerson.setBillingOrder(request.getBillingOrder());
        moviePersonRepository.save(moviePerson);

        return buildMoviePersonResponse(moviePerson);
    }

    @Override
    @Transactional
    public void deleteRelationship(Long id) {
        MoviePerson moviePerson = moviePersonRepository.findById(id)
                .orElseThrow(() -> new MoviePersonNotFoundException("Relationship not found with id: " + id));
        moviePersonRepository.delete(moviePerson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoviePersonSummaryResponse> getMovieCast(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Movie not found with id: " + movieId);
        }
        return moviePersonRepository.findByMovieIdAndRoleType(movieId, RoleType.ACTOR)
                .stream()
                .map(moviePersonMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MoviePersonSummaryResponse> getMovieCrew(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Movie not found with id: " + movieId);
        }
        return moviePersonRepository.findByMovieId(movieId)
                .stream()
                .filter(mp -> mp.getRoleType() != RoleType.ACTOR)
                .map(moviePersonMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MoviePersonSummaryResponse> getPersonFilmography(Long personId, Pageable pageable) {
        if (!personRepository.existsById(personId)) {
            throw new PersonNotFoundException("Person not found with id: " + personId);
        }
        List<MoviePerson> all = moviePersonRepository.findByPersonId(personId);
        List<MoviePersonSummaryResponse> summaries = all.stream()
                .map(moviePersonMapper::toSummary)
                .collect(Collectors.toList());

        if (pageable.isUnpaged()) {
            return new org.springframework.data.domain.PageImpl<>(summaries);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), summaries.size());
        List<MoviePersonSummaryResponse> pageContent = summaries.subList(start, end);

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, summaries.size());
    }

    private void validateActorRequiresCharacter(RoleType roleType, String characterName) {
        if (roleType == RoleType.ACTOR && (characterName == null || characterName.trim().isEmpty())) {
            throw new IllegalArgumentException("Character name is required for ACTOR role");
        }
    }

    private MoviePersonResponse buildMoviePersonResponse(MoviePerson moviePerson) {
        return MoviePersonResponse.builder()
                .relationshipId(moviePerson.getId())
                .movieId(moviePerson.getMovie().getId())
                .movieTitle(moviePerson.getMovie().getTitle())
                .personId(moviePerson.getPerson().getId())
                .personName(moviePerson.getPerson().getName())
                .department(moviePerson.getPerson().getDepartment().getDisplayName())
                .roleType(moviePerson.getRoleType())
                .characterName(moviePerson.getCharacterName())
                .creditedAs(moviePerson.getCreditedAs())
                .billingOrder(moviePerson.getBillingOrder())
                .build();
    }
}
