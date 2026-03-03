package com.example.libraryManager.service;

import com.example.libraryManager.model.User;
import com.example.libraryManager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class UserService {
    public final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(Long id, User newUser){
        User user = userRepository.findById(id).orElse(null);
        if(user != null){
            user.setName(newUser.getName() != null ? newUser.getName() : user.getName());
            user.setPrenom(newUser.getPrenom() != null ? newUser.getPrenom() : user.getPrenom());
            user.setEmail(newUser.getEmail() != null ? newUser.getPrenom() : user.getEmail());
            user.setPassword(newUser.getPassword() != null ? newUser.getPassword() : user.getPassword());
            user.setRole(user.getRole());

            return userRepository.save(user);
        }
        return null;
    }

    public Boolean deleteUser(Long id){
        User user = userRepository.findById(id).orElse(null);
        if(user != null){
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User findUserById(Long id){
        return Objects.requireNonNull(userRepository.findById(id).orElse(null));
    }


    public long getUsersCount(){
        return userRepository.count();
    }
}
