package com.vfc.vfc_backend.service;

import com.vfc.vfc_backend.model.*;
import com.vfc.vfc_backend.repository.FriendRequestRepository;
import com.vfc.vfc_backend.repository.FriendshipRepository;
import com.vfc.vfc_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private FriendRequestRepository friendRequestRepository;

    public List<Friendship> getLastThreeFriendshipsByUserId(int userId) {
        Pageable pageable = PageRequest.of(0, 3); // Get first page with 3 results
        return friendshipRepository.findTop3ByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional
    public void sendFriendRequest(int senderId, int receiverId) {
        if (senderId == receiverId) {
            throw new IllegalArgumentException("Cannot send friend request to self");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        if (friendRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent()) {
            throw new IllegalStateException("Friend request already exists");
        }

        if (isFriend(senderId, receiverId)) {
            throw new IllegalStateException("Users are already friends");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        friendRequestRepository.save(request);
    }

    @Transactional
    public void acceptFriendRequest(int requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());
        friendRequestRepository.save(request);

        // Add to friendships table
        int userId1 = Math.min(request.getSender().getUserId(), request.getReceiver().getUserId());
        int userId2 = Math.max(request.getSender().getUserId(), request.getReceiver().getUserId());
        Friendship friendship = new Friendship(userId1, userId2);
        friendshipRepository.save(friendship);
    }

    public List<User> getFriends(int userId) {
        List<Friendship> friendships = friendshipRepository.findByUserId(userId);
        List<Integer> friendIds = friendships.stream()
                .map(f -> f.getUserId1()==userId ? f.getUserId2() : f.getUserId1())
                .collect(Collectors.toList());
        return friendIds.isEmpty() ? Collections.emptyList() : userRepository.findAllById(friendIds);
    }

    public List<FriendRequest> getPendingRequests(int receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return friendRequestRepository.findByReceiverAndStatus(receiver, FriendRequestStatus.PENDING);
    }

    public boolean isFriend(int userId1, int userId2) {
       int id1 = Math.min(userId1, userId2);
        int id2 = Math.max(userId1, userId2);
        return friendshipRepository.findById(new FriendshipId(id1, id2)).isPresent();
    }
}
