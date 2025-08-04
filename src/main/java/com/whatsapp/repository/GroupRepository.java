package com.whatsapp.repository;

import com.whatsapp.entity.Group;
import com.whatsapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    // Find groups where user is a member
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m = :user")
    List<Group> findGroupsByMember(@Param("user") User user);
    
    // Find groups where user is admin
    List<Group> findByAdmin(User admin);
    
    // Search groups by name
    @Query("SELECT g FROM Group g WHERE g.name LIKE %:keyword%")
    List<Group> searchGroupsByName(@Param("keyword") String keyword);
    
    // Check if user is member of group
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Group g JOIN g.members m WHERE g.id = :groupId AND m.id = :userId")
    boolean isUserMemberOfGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);
}