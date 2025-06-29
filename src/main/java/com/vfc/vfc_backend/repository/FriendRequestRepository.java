package com.vfc.vfc_backend.repository;

import com.vfc.vfc_backend.model.FriendRequest;
import com.vfc.vfc_backend.model.FriendRequestStatus;
import com.vfc.vfc_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status);
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
}
