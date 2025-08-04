package com.whatsapp.dto;

import com.whatsapp.entity.Message;
import java.time.LocalDateTime;

public class MessageDto {
    private Long id;
    private String content;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private String recipientName;
    private Long groupId;
    private String groupName;
    private Message.MessageType messageType;
    private Message.MessageStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Long replyToId;
    private boolean isDeleted;
    
    // Constructors
    public MessageDto() {}
    
    public MessageDto(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.senderId = message.getSender().getId();
        this.senderName = message.getSender().getFullName();
        
        if (message.getRecipient() != null) {
            this.recipientId = message.getRecipient().getId();
            this.recipientName = message.getRecipient().getFullName();
        }
        
        if (message.getGroup() != null) {
            this.groupId = message.getGroup().getId();
            this.groupName = message.getGroup().getName();
        }
        
        this.messageType = message.getMessageType();
        this.status = message.getStatus();
        this.sentAt = message.getSentAt();
        this.deliveredAt = message.getDeliveredAt();
        this.readAt = message.getReadAt();
        this.fileName = message.getFileName();
        this.fileUrl = message.getFileUrl();
        this.fileType = message.getFileType();
        this.fileSize = message.getFileSize();
        
        if (message.getReplyTo() != null) {
            this.replyToId = message.getReplyTo().getId();
        }
        
        this.isDeleted = message.isDeleted();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public Long getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    public Long getGroupId() {
        return groupId;
    }
    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public Message.MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(Message.MessageType messageType) {
        this.messageType = messageType;
    }
    
    public Message.MessageStatus getStatus() {
        return status;
    }
    
    public void setStatus(Message.MessageStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileUrl() {
        return fileUrl;
    }
    
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public Long getReplyToId() {
        return replyToId;
    }
    
    public void setReplyToId(Long replyToId) {
        this.replyToId = replyToId;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}