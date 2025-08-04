package com.whatsapp.controller;

import com.whatsapp.entity.Chat;
import com.whatsapp.service.ChatService;
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
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }
    
    @GetMapping
    public ResponseEntity<?> getUserChats() {
        try {
            Long userId = getCurrentUserId();
            List<Chat> chats = chatService.getUserChats(userId);
            
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/unread")
    public ResponseEntity<?> getChatsWithUnreadMessages() {
        try {
            Long userId = getCurrentUserId();
            List<Chat> chats = chatService.getChatsWithUnreadMessages(userId);
            
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{chatId}")
    public ResponseEntity<?> getChatById(@PathVariable Long chatId) {
        try {
            Chat chat = chatService.getChatById(chatId);
            return ResponseEntity.ok(chat);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/with/{userId}")
    public ResponseEntity<?> getOrCreateChatWithUser(@PathVariable Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            Chat chat = chatService.getOrCreateChat(currentUserId, userId);
            
            return ResponseEntity.ok(chat);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{chatId}/mark-read")
    public ResponseEntity<?> markChatAsRead(@PathVariable Long chatId) {
        try {
            Long userId = getCurrentUserId();
            chatService.markChatAsRead(chatId, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Chat marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable Long chatId) {
        try {
            Long userId = getCurrentUserId();
            chatService.deleteChat(chatId, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Chat deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}