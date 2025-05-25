package com.vfc.vfc_backend.service;

import com.vfc.vfc_backend.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(int theId);

    User save(User theUser);

    void deleteById(int theId);

    User findByUsername(String username);
}
