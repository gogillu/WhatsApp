package com.whatsapp.controller;

import com.whatsapp.dto.MessageDto;
import com.whatsapp.entity.Message;
import com.whatsapp.service.MessageService;
import com.whatsapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class WebSocketController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Map<String, Object> messageData, 
                           SimpMessageHeaderAccessor headerAccessor,
                           Principal principal) {
        try {
            String content = (String) messageData.get("content");
            Long recipientId = Long.valueOf(messageData.get("recipientId").toString());
            String messageType = (String) messageData.get("type");
            
            // Get sender ID from principal (authenticated user)
            Long senderId = Long.valueOf(principal.getName());
            
            Message message;
            if ("GROUP".equals(messageType)) {
                Long groupId = Long.valueOf(messageData.get("groupId").toString());
                message = messageService.sendGroupMessage(senderId, groupId, content);
                
                // Send to group topic
                messagingTemplate.convertAndSend("/topic/group/" + groupId, new MessageDto(message));
            } else {
                message = messageService.sendPrivateMessage(senderId, recipientId, content);
                
                // Send to specific user
                messagingTemplate.convertAndSendToUser(
                    recipientId.toString(), 
                    "/queue/messages", 
                    new MessageDto(message)
                );
                
                // Send confirmation to sender
                messagingTemplate.convertAndSendToUser(
                    senderId.toString(), 
                    "/queue/messages", 
                    new MessageDto(message)
                );
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload Map<String, Object> userData, 
                       SimpMessageHeaderAccessor headerAccessor,
                       Principal principal) {
        try {
            Long userId = Long.valueOf(principal.getName());
            
            // Update user status to online
            userService.updateUserStatus(userId, com.whatsapp.entity.User.UserStatus.ONLINE);
            
            // Store user in websocket session
            headerAccessor.getSessionAttributes().put("userId", userId);
            
            // Notify contacts that user is online
            messagingTemplate.convertAndSend("/topic/user.status", 
                Map.of("userId", userId, "status", "ONLINE"));
                
        } catch (Exception e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }
    
    @MessageMapping("/chat.removeUser")
    public void removeUser(@Payload Map<String, Object> userData, 
                          SimpMessageHeaderAccessor headerAccessor,
                          Principal principal) {
        try {
            Long userId = Long.valueOf(principal.getName());
            
            // Update user status to offline
            userService.updateUserStatus(userId, com.whatsapp.entity.User.UserStatus.OFFLINE);
            
            // Notify contacts that user is offline
            messagingTemplate.convertAndSend("/topic/user.status", 
                Map.of("userId", userId, "status", "OFFLINE"));
                
        } catch (Exception e) {
            System.err.println("Error removing user: " + e.getMessage());
        }
    }
    
    @MessageMapping("/chat.typing")
    public void userTyping(@Payload Map<String, Object> typingData,
                          Principal principal) {
        try {
            Long senderId = Long.valueOf(principal.getName());
            Long recipientId = Long.valueOf(typingData.get("recipientId").toString());
            boolean isTyping = (Boolean) typingData.get("isTyping");
            
            // Send typing indicator to recipient
            messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/typing",
                Map.of("senderId", senderId, "isTyping", isTyping)
            );
            
        } catch (Exception e) {
            System.err.println("Error handling typing: " + e.getMessage());
        }
    }
    
    @MessageMapping("/chat.messageRead")
    public void markMessageAsRead(@Payload Map<String, Object> readData,
                                 Principal principal) {
        try {
            Long messageId = Long.valueOf(readData.get("messageId").toString());
            Long userId = Long.valueOf(principal.getName());
            
            messageService.markMessageAsRead(messageId, userId);
            
            // Notify sender that message was read
            // You would need to get the sender ID from the message
            messagingTemplate.convertAndSend("/topic/message.read",
                Map.of("messageId", messageId, "readBy", userId));
                
        } catch (Exception e) {
            System.err.println("Error marking message as read: " + e.getMessage());
        }
    }
}