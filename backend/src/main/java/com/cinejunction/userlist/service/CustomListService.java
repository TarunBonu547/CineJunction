package com.cinejunction.userlist.service;

import com.cinejunction.userlist.dto.response.CustomListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomListService {
    CustomListResponse createList(Long userId, String name, String description, boolean isPublic, String coverImage);
    CustomListResponse updateList(Long userId, Long listId, String name, String description, boolean isPublic, String coverImage);
    void deleteList(Long userId, Long listId);
    CustomListResponse getListById(Long userId, Long listId);
    Page<CustomListResponse> getUserLists(Long userId, Pageable pageable);
    Page<CustomListResponse> getPublicLists(Pageable pageable);
    void addMovieToList(Long userId, Long listId, Long movieId);
    void removeMovieFromList(Long userId, Long listId, Long movieId);
    void reorderMovies(Long userId, Long listId, List<Long> movieIds);
}
