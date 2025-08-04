package com.whatsapp.controller;

import com.whatsapp.dto.UserDto;
import com.whatsapp.entity.User;
import com.whatsapp.service.UserPrincipal;
import com.whatsapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    @Autowired
    private UserService userService;
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Long userId = getCurrentUserId();
            User user = userService.findById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(new UserDto(user));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String keyword) {
        try {
            List<UserDto> users = userService.searchUsers(keyword);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/contacts")
    public ResponseEntity<?> getContacts() {
        try {
            Long userId = getCurrentUserId();
            List<UserDto> contacts = userService.getUserContacts(userId);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/contacts/{contactId}")
    public ResponseEntity<?> addContact(@PathVariable Long contactId) {
        try {
            Long userId = getCurrentUserId();
            userService.addContact(userId, contactId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Contact added successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/contacts/{contactId}")
    public ResponseEntity<?> removeContact(@PathVariable Long contactId) {
        try {
            Long userId = getCurrentUserId();
            userService.removeContact(userId, contactId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Contact removed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/block/{blockedUserId}")
    public ResponseEntity<?> blockUser(@PathVariable Long blockedUserId) {
        try {
            Long userId = getCurrentUserId();
            userService.blockUser(userId, blockedUserId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User blocked successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/block/{blockedUserId}")
    public ResponseEntity<?> unblockUser(@PathVariable Long blockedUserId) {
        try {
            Long userId = getCurrentUserId();
            userService.unblockUser(userId, blockedUserId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User unblocked successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/blocked")
    public ResponseEntity<?> getBlockedUsers() {
        try {
            Long userId = getCurrentUserId();
            List<UserDto> blockedUsers = userService.getBlockedUsers(userId);
            return ResponseEntity.ok(blockedUsers);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/status")
    public ResponseEntity<?> updateUserStatus(@RequestParam User.UserStatus status) {
        try {
            Long userId = getCurrentUserId();
            User user = userService.updateUserStatus(userId, status);
            
            return ResponseEntity.ok(new UserDto(user));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestParam(required = false) String fullName,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String profilePicture) {
        try {
            Long userId = getCurrentUserId();
            User user = userService.updateProfile(userId, fullName, status, profilePicture);
            
            return ResponseEntity.ok(new UserDto(user));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}