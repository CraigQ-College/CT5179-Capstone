package com.vfc.vfc_backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendships")
public class Friendship {

    @EmbeddedId
    private FriendshipId id;

    @Column(name="created_at",nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default constructor
    public Friendship() {}

    // Constructor
    public Friendship(int userId1, int userId2) {
        this.id = new FriendshipId(userId1, userId2);
    }

    // Getters and setters
    public FriendshipId getId() {
        return id;
    }

    public void setId(FriendshipId id) {
        this.id = id;
    }

    public int getUserId1() {
        return id != null ? id.getUserId1() : null;
    }

    public void setUserId1(int userId1) {
        if (id == null) {
            id = new FriendshipId();
        }
        id.setUserId1(userId1);
    }

    public int getUserId2() {
        return id != null ? id.getUserId2() : null;
    }

    public void setUserId2(int userId2) {
        if (id == null) {
            id = new FriendshipId();
        }
        id.setUserId2(userId2);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
