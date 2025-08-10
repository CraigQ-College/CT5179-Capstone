package com.vfc.vfc_backend.repository;

import com.vfc.vfc_backend.model.Meal;
import com.vfc.vfc_backend.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT w FROM Post w WHERE w.userId = :userId ORDER BY w.postDate DESC")
    List<Post> findByUserId(@Param("userId") int userId);

    Page<Post> findByUserId(int userId, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.user u WHERE u.userId IN :friendIds ORDER BY p.postDate DESC")
    Page<Post> findByUserIdInOrderByPostDateDesc(List<Integer> friendIds, Pageable pageable);
    /*
    @Query("SELECT p FROM Post p JOIN FETCH p.user u WHERE p.userId IN :friendIds ORDER BY p.postDate DESC")
    Page<Post> findByUserIdInOrderByPostDateDesc(List<Integer> friendIds, Pageable pageable);*/
    long countByUserId(int userId);

    //List<Post> findByUserIdInOrderByPostDateDesc(List<Integer> userIds, Pageable pageable);
    long countByUserIdIn(List<Integer> userIds);

    void deleteById(int theId) ;

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.replies WHERE p.postId = :postId")
    Post findByIdWithReplies(int postId);

    @Query("SELECT p FROM Post p WHERE p.userId = :userId AND p.postDate BETWEEN :startDate AND :endDate")
    List<Post> findByUserIdAndDateRange(@Param("userId") int userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
