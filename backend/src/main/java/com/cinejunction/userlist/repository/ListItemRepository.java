package com.cinejunction.userlist.repository;

import com.cinejunction.movie.entity.Movie;
import com.cinejunction.userlist.entity.CustomList;
import com.cinejunction.userlist.entity.ListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListItemRepository extends JpaRepository<ListItem, Long> {
    boolean existsByListAndMovie(CustomList list, Movie movie);
    boolean existsByListIdAndMovieId(Long listId, Long movieId);
    Optional<ListItem> findByListIdAndMovieId(Long listId, Long movieId);
    List<ListItem> findByListId(Long listId);
    long countByListId(Long listId);
    void deleteByListIdAndMovieId(Long listId, Long movieId);
    void deleteByListId(Long listId);
}
