package com.vfc.vfc_backend.repository;

import com.vfc.vfc_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Integer>{
    Optional<User> findByUserName(String userName);
}
