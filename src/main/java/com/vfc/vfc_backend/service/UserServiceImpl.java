package com.vfc.vfc_backend.service;

import com.vfc.vfc_backend.model.User;
import com.vfc.vfc_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return userRepository.findAll();
    }

    //TO DO
    @Override
    public User findById(int theId) {

        Optional<User> result = userRepository.findById(theId);

        User theUser = null;



        if (result.isPresent()) {
            theUser = result.get();
        }
        else {
            // we didn't find the employee
            throw new RuntimeException("Did not find user id - " + theId);
        }


        return theUser;
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
}
