package com.whatsapp.service;

import com.whatsapp.entity.Chat;
import com.whatsapp.entity.User;
import com.whatsapp.repository.ChatRepository;
import com.whatsapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatService {
    
    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Chat getOrCreateChat(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User1 not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User2 not found"));
        
        Optional<Chat> existingChat = chatRepository.findChatBetweenUsers(user1, user2);
        
        if (existingChat.isPresent()) {
            return existingChat.get();
        } else {
            Chat newChat = new Chat(user1, user2);
            return chatRepository.save(newChat);
        }
    }
    
    public List<Chat> getUserChats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return chatRepository.findChatsByUser(user);
    }
    
    public List<Chat> getChatsWithUnreadMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return chatRepository.findChatsWithUnreadMessages(user);
    }
    
    public void markChatAsRead(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        chat.resetUnreadCount(user);
        chatRepository.save(chat);
    }
    
    public Chat getChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
    }
    
    public void deleteChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user is part of this chat
        if (!chat.getUser1().equals(user) && !chat.getUser2().equals(user)) {
            throw new RuntimeException("User is not part of this chat");
        }
        
        chatRepository.delete(chat);
    }
}