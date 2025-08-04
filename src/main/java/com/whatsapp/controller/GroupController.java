package com.whatsapp.controller;

import com.whatsapp.entity.Group;
import com.whatsapp.service.GroupService;
import com.whatsapp.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GroupController {
    
    @Autowired
    private GroupService groupService;
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }
    
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestParam String name,
                                       @RequestParam(required = false) String description) {
        try {
            Long adminId = getCurrentUserId();
            Group group = groupService.createGroup(name, description, adminId);
            
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getUserGroups() {
        try {
            Long userId = getCurrentUserId();
            List<Group> groups = groupService.getUserGroups(userId);
            
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/admin")
    public ResponseEntity<?> getAdminGroups() {
        try {
            Long userId = getCurrentUserId();
            List<Group> groups = groupService.getAdminGroups(userId);
            
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@PathVariable Long groupId) {
        try {
            Group group = groupService.getGroupById(groupId);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> addMemberToGroup(@PathVariable Long groupId, 
                                            @PathVariable Long userId) {
        try {
            Long requesterId = getCurrentUserId();
            Group group = groupService.addMemberToGroup(groupId, userId, requesterId);
            
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> removeMemberFromGroup(@PathVariable Long groupId, 
                                                 @PathVariable Long userId) {
        try {
            Long requesterId = getCurrentUserId();
            Group group = groupService.removeMemberFromGroup(groupId, userId, requesterId);
            
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable Long groupId,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String description) {
        try {
            Long requesterId = getCurrentUserId();
            Group group = groupService.updateGroup(groupId, name, description, requesterId);
            
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId) {
        try {
            Long requesterId = getCurrentUserId();
            groupService.deleteGroup(groupId, requesterId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Group deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchGroups(@RequestParam String keyword) {
        try {
            List<Group> groups = groupService.searchGroups(keyword);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{groupId}/is-member/{userId}")
    public ResponseEntity<?> isUserMemberOfGroup(@PathVariable Long groupId, 
                                                @PathVariable Long userId) {
        try {
            boolean isMember = groupService.isUserMemberOfGroup(groupId, userId);
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("isMember", isMember);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}