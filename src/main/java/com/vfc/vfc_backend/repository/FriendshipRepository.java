package com.vfc.vfc_backend.repository;

import com.vfc.vfc_backend.model.Friendship;
import com.vfc.vfc_backend.model.FriendshipId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
    @Query("SELECT f FROM Friendship f WHERE f.id.userId1 = :userId OR f.id.userId2 = :userId")
    List<Friendship> findByUserId(@Param("userId") int userId);

    @Query("SELECT f FROM Friendship f WHERE f.id.userId1 = :userId OR f.id.userId2 = :userId ORDER BY f.createdAt DESC")
    List<Friendship> findTop3ByUserIdOrderByCreatedAtDesc(@Param("userId") int userId, Pageable pageable);
}
