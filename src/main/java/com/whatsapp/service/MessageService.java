package com.whatsapp.service;

import com.whatsapp.dto.MessageDto;
import com.whatsapp.entity.Message;
import com.whatsapp.entity.User;
import com.whatsapp.entity.Group;
import com.whatsapp.entity.Chat;
import com.whatsapp.repository.MessageRepository;
import com.whatsapp.repository.UserRepository;
import com.whatsapp.repository.GroupRepository;
import com.whatsapp.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private ChatRepository chatRepository;
    
    public Message sendPrivateMessage(Long senderId, Long recipientId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        
        // Create message
        Message message = new Message(content, sender, recipient);
        message = messageRepository.save(message);
        
        // Update or create chat
        updateOrCreateChat(sender, recipient, message);
        
        return message;
    }
    
    public Message sendGroupMessage(Long senderId, Long groupId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if sender is member of the group
        if (!group.isMember(sender)) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        Message message = new Message(content, sender, group);
        return messageRepository.save(message);
    }
    
    public Message sendFileMessage(Long senderId, Long recipientId, String fileName, 
                                 String fileUrl, String fileType, Long fileSize, 
                                 Message.MessageType messageType) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setFileName(fileName);
        message.setFileUrl(fileUrl);
        message.setFileType(fileType);
        message.setFileSize(fileSize);
        message.setMessageType(messageType);
        
        message = messageRepository.save(message);
        
        // Update or create chat
        updateOrCreateChat(sender, recipient, message);
        
        return message;
    }
    
    public List<MessageDto> getPrivateMessages(Long user1Id, Long user2Id, int page, int size) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findPrivateMessages(user1, user2, pageable);
        
        return messages.getContent().stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }
    
    public List<MessageDto> getGroupMessages(Long groupId, int page, int size) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findGroupMessages(group, pageable);
        
        return messages.getContent().stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }
    
    public void markMessageAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // Only recipient can mark message as read
        if (message.getRecipient() != null && message.getRecipient().getId().equals(userId)) {
            message.markAsRead();
            messageRepository.save(message);
            
            // Update chat unread count
            Chat chat = chatRepository.findChatBetweenUsers(message.getSender(), message.getRecipient())
                    .orElse(null);
            if (chat != null) {
                chat.resetUnreadCount(message.getRecipient());
                chatRepository.save(chat);
            }
        }
    }
    
    public void markMessageAsDelivered(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        message.markAsDelivered();
        messageRepository.save(message);
    }
    
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // Only sender can delete message
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("Only sender can delete message");
        }
        
        message.setDeleted(true);
        messageRepository.save(message);
    }
    
    public Message replyToMessage(Long senderId, Long originalMessageId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Message originalMessage = messageRepository.findById(originalMessageId)
                .orElseThrow(() -> new RuntimeException("Original message not found"));
        
        Message reply = new Message();
        reply.setContent(content);
        reply.setSender(sender);
        reply.setReplyTo(originalMessage);
        
        if (originalMessage.isPrivateMessage()) {
            reply.setRecipient(originalMessage.getRecipient());
        } else if (originalMessage.isGroupMessage()) {
            reply.setGroup(originalMessage.getGroup());
        }
        
        return messageRepository.save(reply);
    }
    
    public List<MessageDto> searchMessages(String keyword) {
        return messageRepository.searchMessagesByContent(keyword).stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }
    
    public Long getUnreadMessageCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return messageRepository.countUnreadMessages(user);
    }
    
    private void updateOrCreateChat(User user1, User user2, Message lastMessage) {
        Optional<Chat> chatOpt = chatRepository.findChatBetweenUsers(user1, user2);
        Chat chat;
        
        if (chatOpt.isPresent()) {
            chat = chatOpt.get();
            chat.setLastMessage(lastMessage);
            // Increment unread count for recipient
            chat.incrementUnreadCount(user2);
        } else {
            chat = new Chat(user1, user2);
            chat.setLastMessage(lastMessage);
            chat.incrementUnreadCount(user2);
        }
        
        chatRepository.save(chat);
    }
}