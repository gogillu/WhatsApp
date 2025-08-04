package com.whatsapp.controller;

import com.whatsapp.dto.MessageDto;
import com.whatsapp.entity.Message;
import com.whatsapp.service.MessageService;
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
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }
    
    @PostMapping("/private")
    public ResponseEntity<?> sendPrivateMessage(@RequestParam Long recipientId, 
                                              @RequestParam String content) {
        try {
            Long senderId = getCurrentUserId();
            Message message = messageService.sendPrivateMessage(senderId, recipientId, content);
            
            return ResponseEntity.ok(new MessageDto(message));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/group")
    public ResponseEntity<?> sendGroupMessage(@RequestParam Long groupId, 
                                            @RequestParam String content) {
        try {
            Long senderId = getCurrentUserId();
            Message message = messageService.sendGroupMessage(senderId, groupId, content);
            
            return ResponseEntity.ok(new MessageDto(message));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/private/{userId}")
    public ResponseEntity<?> getPrivateMessages(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        try {
            Long currentUserId = getCurrentUserId();
            List<MessageDto> messages = messageService.getPrivateMessages(currentUserId, userId, page, size);
            
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupMessages(@PathVariable Long groupId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        try {
            List<MessageDto> messages = messageService.getGroupMessages(groupId, page, size);
            
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{messageId}/read")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long messageId) {
        try {
            Long userId = getCurrentUserId();
            messageService.markMessageAsRead(messageId, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Message marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{messageId}/delivered")
    public ResponseEntity<?> markMessageAsDelivered(@PathVariable Long messageId) {
        try {
            messageService.markMessageAsDelivered(messageId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Message marked as delivered");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
        try {
            Long userId = getCurrentUserId();
            messageService.deleteMessage(messageId, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Message deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/{messageId}/reply")
    public ResponseEntity<?> replyToMessage(@PathVariable Long messageId, 
                                          @RequestParam String content) {
        try {
            Long senderId = getCurrentUserId();
            Message reply = messageService.replyToMessage(senderId, messageId, content);
            
            return ResponseEntity.ok(new MessageDto(reply));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchMessages(@RequestParam String keyword) {
        try {
            List<MessageDto> messages = messageService.searchMessages(keyword);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadMessageCount() {
        try {
            Long userId = getCurrentUserId();
            Long count = messageService.getUnreadMessageCount(userId);
            
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}