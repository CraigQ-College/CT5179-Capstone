package com.vfc.vfc_backend.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_reply")
public class PostReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_reply_id")
    private int replyId;

    @Column(name = "post_reply_message")
    private String replyMessage;

    @Column(name = "post_reply_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime replyDate;

    @Column(name = "user_id")
    private int userId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    // Constructors
    public PostReply() {}

    public PostReply(String replyMessage, LocalDateTime timestamp) {
        this.replyMessage = replyMessage;
        this.replyDate = timestamp;

    }

    public PostReply(String replyMessage, LocalDateTime replyDate, int userId) {
        this.replyMessage = replyMessage;
        this.replyDate = replyDate;
        this.userId = userId;
    }

    // Getters and Setters
    public int getReplyId() { return replyId; }
    public void setReplyId(int replyId) { this.replyId = replyId; }
    public String getReplyMessage() { return replyMessage; }
    public void setReplyMessage(String replyMessage) { this.replyMessage = replyMessage; }
    public LocalDateTime getReplyDate() { return replyDate; }
    public void setReplyDate(LocalDateTime timestamp) { this.replyDate = timestamp; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; this.userId = user.getUserId(); }
}
