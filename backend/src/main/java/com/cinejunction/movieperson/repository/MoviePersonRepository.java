package com.cinejunction.movieperson.repository;

import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movieperson.entity.MoviePerson;
import com.cinejunction.movieperson.enums.RoleType;
import com.cinejunction.person.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoviePersonRepository extends JpaRepository<MoviePerson, Long> {

    boolean existsByMovieAndPersonAndRoleType(Movie movie, Person person, RoleType roleType);

    List<MoviePerson> findByMovieId(Long movieId);

    List<MoviePerson> findByMovieIdAndRoleType(Long movieId, RoleType roleType);

    List<MoviePerson> findByPersonId(Long personId);

    void deleteByMovieId(Long movieId);
}
