package com.vfc.vfc_backend.repository;

import com.vfc.vfc_backend.model.FriendRequest;
import com.vfc.vfc_backend.model.FriendRequestStatus;
import com.vfc.vfc_backend.model.User;
//import org.hibernate.query.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    //List<FriendRequest> findByReceiverAndStatus(int receiverId, FriendRequestStatus status);
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status);
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    //Optional<FriendRequest> findBySenderAndReceiver(int sender, int receiver);

    Page<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status, Pageable pageable);

    long countByReceiverAndStatus(User receiver, FriendRequestStatus status);
}
