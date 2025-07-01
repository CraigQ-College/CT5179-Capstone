package com.vfc.vfc_backend.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FriendshipId implements Serializable {
    private int userId1;
    private int userId2;

    // Default constructor
    public FriendshipId() {}

    // Constructor
    public FriendshipId(int userId1, int userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    // Getters and setters
    public int getUserId1() {
        return userId1;
    }

    public void setUserId1(int userId1) {
        this.userId1 = userId1;
    }

    public int getUserId2() {
        return userId2;
    }

    public void setUserId2(int userId2) {
        this.userId2 = userId2;
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipId that = (FriendshipId) o;
        return Objects.equals(userId1, that.userId1) &&
                Objects.equals(userId2, that.userId2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId1, userId2);
    }
}
