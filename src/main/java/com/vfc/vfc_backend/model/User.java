package com.vfc.vfc_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name="app_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private int userId;

    @Column(name="user_name")
    private String userName;

    @Column(name="user_pass")
    private String userPassword;

    @Column(name="user_email")
    private String userEmail;

    public User() {
    }

    public User(int id, String userName, String userPassword) {
        this.userId = id;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public User(String userPassword, String userName) {
        this.userPassword = userPassword;
        this.userName = userName;
    }

    public User(String username, String email, String password) {
        this.userName = username;
        this.userEmail = email;
        this.userPassword = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
