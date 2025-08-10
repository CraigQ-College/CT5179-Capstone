package com.vfc.vfc_backend.repository;

import com.vfc.vfc_backend.model.FriendRequestStatus;
import com.vfc.vfc_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Integer>{
    Optional<User> findByUserName(String userName);

    Optional<User> findByUserEmail(String email);

    Optional<User> findByUserId(int id);

    @Query("SELECT u FROM User u WHERE u.userId != :currentUserId " +
            "AND NOT EXISTS (SELECT f FROM Friendship f WHERE (f.id.userId1 = :currentUserId AND f.id.userId2 = u.userId) OR (f.id.userId2 = :currentUserId AND f.id.userId1 = u.userId)) " +
            "AND NOT EXISTS (SELECT fr FROM FriendRequest fr WHERE fr.status = :status AND " +
            "((fr.sender.userId = :currentUserId AND fr.receiver.userId = u.userId) OR (fr.receiver.userId = :currentUserId AND fr.sender.userId = u.userId)))")
    List<User> findNonFriendsWithoutPendingRequests(@Param("currentUserId") int currentUserId, @Param("status") FriendRequestStatus status);

    @Query("SELECT u FROM User u WHERE u.userId != :currentUserId " +
            "AND NOT EXISTS (SELECT f FROM Friendship f WHERE (f.id.userId1 = :currentUserId AND f.id.userId2 = u.userId) OR (f.id.userId2 = :currentUserId AND f.id.userId1 = u.userId)) " +
            "AND NOT EXISTS (SELECT fr FROM FriendRequest fr WHERE fr.status = :status AND " +
            "((fr.sender.userId = :currentUserId AND fr.receiver.userId = u.userId) OR (fr.receiver.userId = :currentUserId AND fr.sender.userId = u.userId)))")
    Page<User> findNonFriendsWithoutPendingRequests(@Param("currentUserId") int currentUserId, @Param("status") FriendRequestStatus status, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.userId != :currentUserId " +
            "AND NOT EXISTS (SELECT f FROM Friendship f WHERE (f.id.userId1 = :currentUserId AND f.id.userId2 = u.userId) OR (f.id.userId2 = :currentUserId AND f.id.userId1 = u.userId)) " +
            "AND NOT EXISTS (SELECT fr FROM FriendRequest fr WHERE fr.status = :status AND " +
            "((fr.sender.userId = :currentUserId AND fr.receiver.userId = u.userId) OR (fr.receiver.userId = :currentUserId AND fr.sender.userId = u.userId)))")
    long countNonFriendsWithoutPendingRequests(@Param("currentUserId") int currentUserId, @Param("status") FriendRequestStatus status);
}
