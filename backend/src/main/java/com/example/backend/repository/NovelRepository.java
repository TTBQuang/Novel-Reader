package com.example.backend.repository;

import com.example.backend.entity.Novel;
import com.example.backend.enums.NovelStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface NovelRepository extends JpaRepository<Novel, Long> {
    @Query("SELECT DISTINCT n FROM Novel n " +
            "JOIN n.genres g " +
            "WHERE (:keyword IS NULL OR n.name LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:genreIds IS NULL OR g.id IN :genreIds) " +
            "AND (:status IS NULL OR n.status IN :status)")
    Page<Novel> findNovelsByCriteria(@Param("keyword") String keyword,
                                     @Param("status") Set<NovelStatus> status,
                                     @Param("genreIds") Set<Long> genreIds,
                                     Pageable pageable);
}
