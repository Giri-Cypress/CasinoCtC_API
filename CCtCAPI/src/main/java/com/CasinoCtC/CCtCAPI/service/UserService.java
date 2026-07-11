package com.CasinoCtC.CCtCAPI.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.CasinoCtC.CCtCAPI.entity.UserEntity;
import com.CasinoCtC.CCtCAPI.model.User;
import com.CasinoCtC.CCtCAPI.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {


	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;



    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    public User getUserByNumber(Integer userNumber) {
        return userRepository.findById(userNumber).map(this::toModel).orElse(null);
    }

    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName).map(this::toModel).orElse(null);
    }

    public User saveUser(User user) {

	        UserEntity entity;
	
	        // Existing user
	        if (user.getUserNumber() != null &&
	            userRepository.existsById(user.getUserNumber())) {
	
	            entity = userRepository
	                    .findById(user.getUserNumber())
	                    .orElseThrow();
	
	            entity.setUserName(user.getUserName());
	            entity.setLocationNumber(user.getLocationNumber());
	            entity.setStatus(
	                    user.getStatus() == null ? 1 : user.getStatus());
	
	            // Only update password when supplied

				if (user.getPasswordHash() != null &&
				    !user.getPasswordHash().isBlank()) {
				
				    entity.setPasswordHash(
				        passwordEncoder.encode(user.getPasswordHash())
				    );
				}
	
	        } else {
	
	            // New user
	            entity = new UserEntity();
	
	            entity.setUserNumber(user.getUserNumber());
	            entity.setUserName(user.getUserName());

	            entity.setPasswordHash(
	            			passwordEncoder.encode(user.getPasswordHash())
	            );

	            entity.setLocationNumber(user.getLocationNumber());
	            entity.setStatus(
	                    user.getStatus() == null ? 1 : user.getStatus());
	            entity.setLastLoginAt(user.getLastLoginAt());
	        }
	
	        return toModel(userRepository.save(entity));
	}

    public void deleteUser(Integer userNumber) {
        userRepository.deleteById(userNumber);
    }

    private User toModel(UserEntity entity) {
        User model = new User();
        model.setUserNumber(entity.getUserNumber());
        model.setUserName(entity.getUserName());
        model.setPasswordHash(entity.getPasswordHash());
        model.setLocationNumber(entity.getLocationNumber());
        model.setStatus(entity.getStatus());
        model.setLastLoginAt(entity.getLastLoginAt());
        return model;
    }

    private UserEntity toEntity(User model) {
        UserEntity entity = new UserEntity();
        entity.setUserNumber(model.getUserNumber());
        entity.setUserName(model.getUserName());
        entity.setPasswordHash(model.getPasswordHash());
        entity.setLocationNumber(model.getLocationNumber());
        entity.setStatus(model.getStatus() == null ? 1 : model.getStatus());
        entity.setLastLoginAt(model.getLastLoginAt());
        return entity;
    }
}
