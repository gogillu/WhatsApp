package com.whatsapp.service;

import com.whatsapp.entity.Group;
import com.whatsapp.entity.User;
import com.whatsapp.repository.GroupRepository;
import com.whatsapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GroupService {
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Group createGroup(String name, String description, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
        
        Group group = new Group(name, admin);
        group.setDescription(description);
        
        return groupRepository.save(group);
    }
    
    public Group addMemberToGroup(Long groupId, Long userId, Long requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        
        // Only admin can add members
        if (!group.isAdmin(requester)) {
            throw new RuntimeException("Only group admin can add members");
        }
        
        // Check if user is already a member
        if (group.isMember(user)) {
            throw new RuntimeException("User is already a member of this group");
        }
        
        group.addMember(user);
        return groupRepository.save(group);
    }
    
    public Group removeMemberFromGroup(Long groupId, Long userId, Long requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        
        // Admin can remove any member, members can only remove themselves
        if (!group.isAdmin(requester) && !userId.equals(requesterId)) {
            throw new RuntimeException("You can only remove yourself from the group or admin can remove members");
        }
        
        // Cannot remove admin
        if (group.isAdmin(user)) {
            throw new RuntimeException("Cannot remove admin from group");
        }
        
        group.removeMember(user);
        return groupRepository.save(group);
    }
    
    public Group updateGroup(Long groupId, String name, String description, Long requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        
        // Only admin can update group
        if (!group.isAdmin(requester)) {
            throw new RuntimeException("Only group admin can update group");
        }
        
        if (name != null) {
            group.setName(name);
        }
        if (description != null) {
            group.setDescription(description);
        }
        
        return groupRepository.save(group);
    }
    
    public void deleteGroup(Long groupId, Long requesterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        
        // Only admin can delete group
        if (!group.isAdmin(requester)) {
            throw new RuntimeException("Only group admin can delete group");
        }
        
        groupRepository.delete(group);
    }
    
    public List<Group> getUserGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return groupRepository.findGroupsByMember(user);
    }
    
    public List<Group> getAdminGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return groupRepository.findByAdmin(user);
    }
    
    public List<Group> searchGroups(String keyword) {
        return groupRepository.searchGroupsByName(keyword);
    }
    
    public Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }
    
    public boolean isUserMemberOfGroup(Long groupId, Long userId) {
        return groupRepository.isUserMemberOfGroup(groupId, userId);
    }
}