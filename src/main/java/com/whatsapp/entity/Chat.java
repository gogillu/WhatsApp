package com.whatsapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Unread message count for each user
    @Column(name = "unread_count_user1")
    private Integer unreadCountUser1 = 0;

    @Column(name = "unread_count_user2")
    private Integer unreadCountUser2 = 0;

    // Constructors
    public Chat() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Chat(User user1, User user2) {
        this();
        this.user1 = user1;
        this.user2 = user2;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUnreadCountUser1() {
        return unreadCountUser1;
    }

    public void setUnreadCountUser1(Integer unreadCountUser1) {
        this.unreadCountUser1 = unreadCountUser1;
    }

    public Integer getUnreadCountUser2() {
        return unreadCountUser2;
    }

    public void setUnreadCountUser2(Integer unreadCountUser2) {
        this.unreadCountUser2 = unreadCountUser2;
    }

    // Helper methods
    public User getOtherUser(User currentUser) {
        return currentUser.equals(user1) ? user2 : user1;
    }

    public Integer getUnreadCount(User user) {
        return user.equals(user1) ? unreadCountUser1 : unreadCountUser2;
    }

    public void incrementUnreadCount(User user) {
        if (user.equals(user1)) {
            this.unreadCountUser1++;
        } else {
            this.unreadCountUser2++;
        }
    }

    public void resetUnreadCount(User user) {
        if (user.equals(user1)) {
            this.unreadCountUser1 = 0;
        } else {
            this.unreadCountUser2 = 0;
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}