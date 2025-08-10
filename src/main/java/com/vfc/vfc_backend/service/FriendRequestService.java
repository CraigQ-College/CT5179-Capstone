package com.vfc.vfc_backend.service;

import com.vfc.vfc_backend.model.FriendRequest;
import com.vfc.vfc_backend.model.FriendRequestStatus;
import com.vfc.vfc_backend.model.Friendship;
import com.vfc.vfc_backend.model.FriendshipId;
import com.vfc.vfc_backend.model.User;
import com.vfc.vfc_backend.repository.FriendRequestRepository;
import com.vfc.vfc_backend.repository.FriendshipRepository;
import com.vfc.vfc_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void sendFriendRequest(int senderId, int receiverId) {
        // Validate sender and receiver
        if (senderId==receiverId) {
            throw new IllegalArgumentException("Cannot send friend request to self");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // Check for existing request
        if (friendRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent()) {
            throw new IllegalStateException("Friend request already exists");
        }

        // Check if users are already friends
        if (isFriend(senderId, receiverId)) {
            throw new IllegalStateException("Users are already friends");
        }

        // Create and save friend request
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
       request.setReceiver(receiver);
        //request.setSenderId(senderId);
         //request.setReceiverId(receiverId);
        request.setStatus(FriendRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        friendRequestRepository.save(request);
    }

    @Transactional
    public void acceptFriendRequest(Integer requestId) {
        // Find the request
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        // Validate status
        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Friend request is not pending");
        }

        // Update request status
        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());
        friendRequestRepository.save(request);

        // Create friendship
        //int userId1 = Math.min(request.getSenderId(), request.getReceiverId());
        //int userId2 = Math.max(request.getSenderId(), request.getReceiverId());

        int userId1 = Math.min(request.getSender().getUserId(), request.getReceiver().getUserId());
        int userId2 = Math.max(request.getSender().getUserId(), request.getReceiver().getUserId());
        Friendship friendship = new Friendship(userId1, userId2);
        friendship.setCreatedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);
    }


    @Transactional
    public void rejectFriendRequest(Integer requestId) {
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

    public List<FriendRequest> getPendingRequests(int receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return friendRequestRepository.findByReceiverAndStatus(receiver, FriendRequestStatus.PENDING);
    }

    private boolean isFriend(int userId1, int userId2) {
       int id1 = Math.min(userId1, userId2);
        int id2 = Math.max(userId1, userId2);
        return friendshipRepository.findById(new FriendshipId(id1, id2)).isPresent();
    }
}
