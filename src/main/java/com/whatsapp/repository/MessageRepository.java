package com.whatsapp.repository;

import com.whatsapp.entity.Message;
import com.whatsapp.entity.User;
import com.whatsapp.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Find messages between two users (private chat)
    @Query("SELECT m FROM Message m WHERE " +
           "((m.sender = :user1 AND m.recipient = :user2) OR " +
           "(m.sender = :user2 AND m.recipient = :user1)) AND " +
           "m.isDeleted = false " +
           "ORDER BY m.sentAt DESC")
    Page<Message> findPrivateMessages(@Param("user1") User user1, 
                                     @Param("user2") User user2, 
                                     Pageable pageable);
    
    // Find group messages
    @Query("SELECT m FROM Message m WHERE m.group = :group AND m.isDeleted = false ORDER BY m.sentAt DESC")
    Page<Message> findGroupMessages(@Param("group") Group group, Pageable pageable);
    
    // Find unread messages for a user
    @Query("SELECT m FROM Message m WHERE m.recipient = :user AND m.status != 'READ' AND m.isDeleted = false")
    List<Message> findUnreadMessages(@Param("user") User user);
    
    // Count unread messages for a user
    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipient = :user AND m.status != 'read' AND m.isDeleted = false")
    Long countUnreadMessages(@Param("user") User user);
    
    // Find last message between two users
    @Query("SELECT m FROM Message m WHERE " +
           "((m.sender = :user1 AND m.recipient = :user2) OR " +
           "(m.sender = :user2 AND m.recipient = :user1)) AND " +
           "m.isDeleted = false " +
           "ORDER BY m.sentAt DESC")
    List<Message> findLastMessageBetweenUsers(@Param("user1") User user1, 
                                             @Param("user2") User user2, 
                                             Pageable pageable);
    
    // Find last message in a group
    @Query("SELECT m FROM Message m WHERE m.group = :group AND m.isDeleted = false ORDER BY m.sentAt DESC")
    List<Message> findLastGroupMessage(@Param("group") Group group, Pageable pageable);
    
    // Search messages by content
    @Query("SELECT m FROM Message m WHERE m.content LIKE %:keyword% AND m.isDeleted = false")
    List<Message> searchMessagesByContent(@Param("keyword") String keyword);
}