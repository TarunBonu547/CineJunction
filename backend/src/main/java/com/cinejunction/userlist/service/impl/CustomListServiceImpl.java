package com.cinejunction.userlist.service.impl;

import com.cinejunction.entity.User;
import com.cinejunction.exception.UserNotFoundException;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.repository.UserRepository;
import com.cinejunction.userlist.dto.response.CustomListResponse;
import com.cinejunction.userlist.entity.CustomList;
import com.cinejunction.userlist.entity.ListItem;
import com.cinejunction.userlist.exception.CustomListNotFoundException;
import com.cinejunction.userlist.exception.ListMovieAlreadyExistsException;
import com.cinejunction.userlist.exception.ListMovieNotFoundException;
import com.cinejunction.userlist.exception.UnauthorizedListAccessException;
import com.cinejunction.userlist.mapper.CustomListMapper;
import com.cinejunction.userlist.repository.CustomListRepository;
import com.cinejunction.userlist.repository.ListItemRepository;
import com.cinejunction.userlist.service.CustomListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomListServiceImpl implements CustomListService {

    private final UserRepository userRepository;

    private final CustomListRepository customListRepository;
    private final ListItemRepository listItemRepository;
    private final MovieRepository movieRepository;
    private final CustomListMapper customListMapper;

    @Override
    @Transactional
    public CustomListResponse createList(Long userId, String name, String description,
                                     boolean isPublic, String coverImage) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    CustomList list = CustomList.builder()
            .user(user)
            .name(name)
            .description(description)
            .isPublic(isPublic)
            .coverImage(coverImage)
            .build();

    CustomList saved = customListRepository.save(list);
    return customListMapper.toResponse(saved);
}

    @Override
    @Transactional
    public CustomListResponse updateList(Long userId, Long listId, String name, String description, boolean isPublic, String coverImage) {
        CustomList list = customListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new CustomListNotFoundException("List not found with id: " + listId));
        list.setName(name);
        list.setDescription(description);
        list.setPublic(isPublic);
        list.setCoverImage(coverImage);
        CustomList saved = customListRepository.save(list);
        return customListMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteList(Long userId, Long listId) {
        CustomList list = customListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new CustomListNotFoundException("List not found with id: " + listId));
        customListRepository.delete(list);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomListResponse getListById(Long userId, Long listId) {
        CustomList list = customListRepository.findById(listId)
                .orElseThrow(() -> new CustomListNotFoundException("List not found with id: " + listId));
        if (!list.isPublic() && !list.getUser().getId().equals(userId)) {
            throw new UnauthorizedListAccessException("You do not have access to this list");
        }
        return customListMapper.toResponse(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomListResponse> getUserLists(Long userId, Pageable pageable) {
        return customListRepository.findByUserId(userId, pageable).map(customListMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomListResponse> getPublicLists(Pageable pageable) {
        return customListRepository.findByIsPublicTrue(pageable).map(customListMapper::toResponse);
    }

    @Override
    @Transactional
    public void addMovieToList(Long userId, Long listId, Long movieId) {
        CustomList list = customListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new CustomListNotFoundException("List not found with id: " + listId));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomListNotFoundException("Movie not found with id: " + movieId));
        if (listItemRepository.existsByListIdAndMovieId(listId, movieId)) {
            throw new ListMovieAlreadyExistsException("Movie already exists in this list");
        }
        int nextSortOrder = listItemRepository.findByListId(listId).size();
        ListItem item = ListItem.builder()
                .list(list)
                .movie(movie)
                .sortOrder(nextSortOrder)
                .build();
        listItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeMovieFromList(Long userId, Long listId, Long movieId) {
        CustomList list = customListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new CustomListNotFoundException("List not found with id: " + listId));
        if (!listItemRepository.existsByListIdAndMovieId(listId, movieId)) {
            throw new ListMovieNotFoundException("Movie not found in this list");
        }
        listItemRepository.deleteByListIdAndMovieId(listId, movieId);
    }

    @Override
    @Transactional
    public void reorderMovies(Long userId, Long listId, List<Long> movieIds) {
        CustomList list = customListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new CustomListNotFoundException("List not found with id: " + listId));
        List<ListItem> items = listItemRepository.findByListId(listId);
        for (int i = 0; i < movieIds.size(); i++) {
            Long movieId = movieIds.get(i);
            ListItem item = items.stream()
                    .filter(li -> li.getMovie().getId().equals(movieId))
                    .findFirst()
                    .orElseThrow(() -> new ListMovieNotFoundException("Movie not found in this list: " + movieId));
            item.setSortOrder(i);
            listItemRepository.save(item);
        }
    }
}
