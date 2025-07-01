package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.FriendRequest;
import com.vfc.vfc_backend.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/friend-requests")
public class FriendRequestController {

    @Autowired
    private FriendRequestService friendRequestService;
/*
    @PostMapping("/send")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendRequest dto) {
        friendRequestService.sendFriendRequest(dto.getSenderId(), dto.getReceiverId());
        return ResponseEntity.ok("Friend request sent");
    }*/

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Integer requestId) {
        friendRequestService.acceptFriendRequest(requestId);
        return ResponseEntity.ok("Friend request accepted");
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectFriendRequest(@PathVariable Integer requestId) {
        friendRequestService.rejectFriendRequest(requestId);
        return ResponseEntity.ok("Friend request rejected");
    }
/*
    @GetMapping("/pending/{receiverId}")
    public ResponseEntity<List<FriendRequest>> getPendingRequests(@PathVariable Long receiverId) {
        return ResponseEntity.ok(friendRequestService.getPendingRequests(receiverId));
    }*/
}


