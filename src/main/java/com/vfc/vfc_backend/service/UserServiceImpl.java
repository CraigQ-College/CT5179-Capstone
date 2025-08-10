package com.vfc.vfc_backend.service;

import com.vfc.vfc_backend.model.FriendRequestStatus;
import com.vfc.vfc_backend.model.User;
import com.vfc.vfc_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository theUserRepository) {
        this.userRepository = theUserRepository;
    }

    public List<User> findAll() {

        List<User> users = userRepository.findAll();
        return users != null ? users : Collections.emptyList();
        //return userRepository.findAll();
    }

    //TO DO
    @Override
    public User findById(int theId) {

        /*
        Optional<User> result = userRepository.findById(theId);

        User theUser = null;



        if (result.isPresent()) {
            theUser = result.get();
        }
        else {
            // we didn't find the employee
            throw new RuntimeException("Did not find user id - " + theId);
        }


        return theUser;*/
        return userRepository.findById(theId).orElse(null);
    }



    @Transactional
    public User save(User theUser) {
        return userRepository.save(theUser);
    }

    @Override
    public void deleteById(int theId) {
        userRepository.deleteById(theId);
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> result = userRepository.findByUserName(username);
        return result.orElse(null); // Return null if user not found
    }

    @Override
    public User findByUseremail(String email) {
        Optional<User> result = userRepository.findByUserEmail(email);
        return result.orElse(null); // Return null if user not found
    }

    @Override
    public List<User> findNonFriends(int userId, FriendRequestStatus status) {
        return userRepository.findNonFriendsWithoutPendingRequests(userId, status); // Return null if user not found
    }

    public List<User> findNonFriends(int userId, FriendRequestStatus status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return userRepository.findNonFriendsWithoutPendingRequests(userId, status, pageable).getContent();
    }

    public long countNonFriends(int userId, FriendRequestStatus status) {
        return userRepository.countNonFriendsWithoutPendingRequests(userId, status);
    }

    /*public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }*/





}
