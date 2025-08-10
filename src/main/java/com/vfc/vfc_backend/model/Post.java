package com.vfc.vfc_backend.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;

    @ManyToOne(fetch = FetchType.EAGER) // Eager loading to ensure User is fetched
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    private User user; // New field for the User relationship

    @Column(name = "user_id")
    private int userId;

    @Column(name = "post_message")
    private String postMessage;


    @Column(name = "post_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime postDate;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostReply> replies = new ArrayList<>();

    // Constructors
    public Post() {}

    public Post(int userId ,String postMessage, LocalDateTime timestamp) {
        this.userId = userId;
        this.postMessage = postMessage;
        this.postDate = timestamp;
    }

    public Post(int userId ,String postMessage) {
        this.userId = userId;
        this.postMessage = postMessage;

    }

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    public String getPostMessage() { return postMessage; }
    public void setPostMessage(String postMessage) { this.postMessage = postMessage; }

    public List<PostReply> getReplies() { return replies; }
    public void setReplies(List<PostReply> replies) { this.replies = replies; }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }

    public User getUser() { return user; } // New getter
    public void setUser(User user) { this.user = user; this.userId = user.getUserId();}
}
