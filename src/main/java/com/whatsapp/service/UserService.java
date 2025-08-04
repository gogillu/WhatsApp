package com.whatsapp.service;

import com.whatsapp.dto.UserDto;
import com.whatsapp.dto.UserRegistrationDto;
import com.whatsapp.entity.User;
import com.whatsapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        
        if (registrationDto.getPhoneNumber() != null && 
            userRepository.existsByPhoneNumber(registrationDto.getPhoneNumber())) {
            throw new RuntimeException("User with this phone number already exists");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setFullName(registrationDto.getFullName());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmailOrPhoneNumber(String identifier) {
        return userRepository.findByEmailOrPhoneNumber(identifier);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<UserDto> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword).stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }
    
    public List<UserDto> getUserContacts(Long userId) {
        return userRepository.findContactsByUserId(userId).stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }
    
    public void addContact(Long userId, Long contactId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User contact = userRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        
        user.addContact(contact);
        userRepository.save(user);
    }
    
    public void removeContact(Long userId, Long contactId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User contact = userRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        
        user.removeContact(contact);
        userRepository.save(user);
    }
    
    public void blockUser(Long userId, Long blockedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new RuntimeException("User to block not found"));
        
        user.blockUser(blockedUser);
        userRepository.save(user);
    }
    
    public void unblockUser(Long userId, Long blockedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new RuntimeException("User to unblock not found"));
        
        user.unblockUser(blockedUser);
        userRepository.save(user);
    }
    
    public List<UserDto> getBlockedUsers(Long userId) {
        return userRepository.findBlockedUsersByUserId(userId).stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }
    
    public User updateUserStatus(Long userId, User.UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setUserStatus(status);
        user.setLastSeen(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User updateProfile(Long userId, String fullName, String status, String profilePicture) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (fullName != null) {
            user.setFullName(fullName);
        }
        if (status != null) {
            user.setStatus(status);
        }
        if (profilePicture != null) {
            user.setProfilePicture(profilePicture);
        }
        
        return userRepository.save(user);
    }
}