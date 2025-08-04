## GitHub Copilot Prompt

Update this empty Spring Boot project into a fully functional **WhatsApp-like application** using **Java** and **Maven**.

### Functional Requirements

1. User Registration & Authentication (email/phone-based)  
2. Real-time messaging between users (1:1 chats)  
3. Chat message persistence (with timestamps, read receipts, delivery status)  
4. Contact list management (add, block, search)  
5. Group chat support (create, add/remove members, group messages)  
6. Message notifications (via WebSockets or SSE)  
7. Media/file upload and download support (images, documents)

### Technical Requirements

- Java 17+  
- Spring Boot (latest stable version)  
- Maven for build & dependency management  
- Spring Security for authentication  
- WebSocket or STOMP for real-time messaging  
- JPA/Hibernate + MySQL or PostgreSQL for persistence  
- RESTful APIs for frontend integration (mobile/web)  
- Clean, modular code structure (DTOs, services, controllers, repositories)

### Optional Enhancements

- End-to-end encryption  
- Message search/filtering  
- Message deletion & update  
- Online/offline status tracking  
