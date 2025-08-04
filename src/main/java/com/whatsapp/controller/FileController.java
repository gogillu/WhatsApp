package com.whatsapp.controller;

import com.whatsapp.dto.MessageDto;
import com.whatsapp.entity.Message;
import com.whatsapp.service.FileService;
import com.whatsapp.service.MessageService;
import com.whatsapp.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private MessageService messageService;
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam Long recipientId,
                                      @RequestParam(required = false) Long groupId) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please select a file to upload");
                return ResponseEntity.badRequest().body(error);
            }
            
            Long senderId = getCurrentUserId();
            
            // Save file and get file info
            Map<String, Object> fileInfo = fileService.saveFile(file);
            
            String fileName = (String) fileInfo.get("fileName");
            String fileUrl = (String) fileInfo.get("fileUrl");
            String fileType = (String) fileInfo.get("fileType");
            Long fileSize = (Long) fileInfo.get("fileSize");
            
            // Determine message type based on file type
            Message.MessageType messageType = determineMessageType(fileType);
            
            Message message;
            if (groupId != null) {
                // Send to group (need to implement this in MessageService)
                message = new Message();
                message.setFileName(fileName);
                message.setFileUrl(fileUrl);
                message.setFileType(fileType);
                message.setFileSize(fileSize);
                message.setMessageType(messageType);
                // For now, sending as private message
                message = messageService.sendFileMessage(senderId, recipientId, fileName, fileUrl, fileType, fileSize, messageType);
            } else {
                // Send as private message
                message = messageService.sendFileMessage(senderId, recipientId, fileName, fileUrl, fileType, fileSize, messageType);
            }
            
            return ResponseEntity.ok(new MessageDto(message));
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Resource resource = fileService.loadFileAsResource(fileName);
            
            String contentType = fileService.getContentType(fileName);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName) {
        try {
            Resource resource = fileService.loadFileAsResource(fileName);
            
            String contentType = fileService.getContentType(fileName);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    private Message.MessageType determineMessageType(String fileType) {
        if (fileType.startsWith("image/")) {
            return Message.MessageType.IMAGE;
        } else if (fileType.startsWith("video/")) {
            return Message.MessageType.VIDEO;
        } else if (fileType.startsWith("audio/")) {
            return Message.MessageType.AUDIO;
        } else {
            return Message.MessageType.DOCUMENT;
        }
    }
}