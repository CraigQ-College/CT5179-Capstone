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

    @Autowired
    private  UserService userService;

    public FriendService(FriendRequestRepository friendRequestRepository,UserRepository userRepository,FriendshipRepository friendshipRepository,UserService userService) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
    }

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

        //int userId1 = Math.min(request.getSenderId(), request.getReceiverId());
        //int userId2 = Math.max(request.getSenderId(), request.getReceiverId());
        Friendship friendship = new Friendship(userId1, userId2);
        friendshipRepository.save(friendship);
    }

    @Transactional
    public void rejectFriendRequest(int requestId) {
        // Find the request
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        // Validate status
        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Friend request is not pending");
        }

        // Update request status
        request.setStatus(FriendRequestStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());
        friendRequestRepository.save(request);
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

    public List<User> getFriends(int userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return friendshipRepository.findFriendsByUserId(userId, pageable).getContent();
    }

    public long countFriends(int userId) {
        return friendshipRepository.countFriendsByUserId(userId);
    }


    public List<FriendRequest> getPendingRequests(int userId, int page, int pageSize) {
        User receiver = userService.findById(userId);
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return friendRequestRepository.findByReceiverAndStatus(receiver, FriendRequestStatus.PENDING, pageable).getContent();
    }

    public long countPendingRequests(int userId) {
        User receiver = userService.findById(userId);
        return friendRequestRepository.countByReceiverAndStatus(receiver, FriendRequestStatus.PENDING);
    }

    @Transactional
    public void deleteFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new IllegalArgumentException("Cannot remove yourself as a friend");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));
        int id1 = Math.min(userId, friendId);
        int id2 = Math.max(userId, friendId);
        FriendshipId friendshipId = new FriendshipId(id1, id2);
        if (!friendshipRepository.findById(friendshipId).isPresent()) {
            throw new IllegalStateException("Friendship does not exist");
        }
        friendshipRepository.deleteById(friendshipId);
    }

}
