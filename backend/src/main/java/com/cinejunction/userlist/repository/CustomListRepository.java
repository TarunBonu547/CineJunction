package com.cinejunction.userlist.repository;

import com.cinejunction.entity.User;
import com.cinejunction.userlist.entity.CustomList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomListRepository extends JpaRepository<CustomList, Long> {
    Page<CustomList> findByUserId(Long userId, Pageable pageable);
    Page<CustomList> findByIsPublicTrue(Pageable pageable);
    Optional<CustomList> findByIdAndUserId(Long id, Long userId);
    Optional<CustomList> findByIdAndIsPublicTrue(Long id);
    boolean existsByUserIdAndName(Long userId, String name);
    long countByUserId(Long userId);
    List<CustomList> findByUserId(Long userId);
}
