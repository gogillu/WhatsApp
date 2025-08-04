# WhatsApp Clone - Spring Boot Application

A fully functional WhatsApp-like messaging application built with Spring Boot, featuring real-time messaging, user management, group chats, and file sharing capabilities.

## Features

### Core Functionality
- ✅ User Registration & Authentication (email/phone-based)
- ✅ Real-time messaging between users (1:1 chats)
- ✅ Chat message persistence (with timestamps, read receipts, delivery status)
- ✅ Contact list management (add, block, search)
- ✅ Group chat support (create, add/remove members, group messages)
- ✅ Message notifications (via WebSockets/STOMP)
- ✅ Media/file upload and download support (images, documents)

### Additional Features
- ✅ JWT-based authentication
- ✅ Message search and filtering
- ✅ Message replies and forwarding
- ✅ Online/offline status tracking
- ✅ User profile management
- ✅ Real-time typing indicators
- ✅ File sharing with multiple formats support

## Technology Stack

- **Java 17+**
- **Spring Boot 3.2.0**
- **Maven** for build & dependency management
- **Spring Security** with JWT authentication
- **WebSocket/STOMP** for real-time messaging
- **JPA/Hibernate** with H2 database (configurable for MySQL/PostgreSQL)
- **RESTful APIs** for frontend integration

## Project Structure

```
src/
├── main/
│   ├── java/com/whatsapp/
│   │   ├── WhatsAppApplication.java         # Main Spring Boot application
│   │   ├── config/                          # Configuration classes
│   │   │   ├── AuthEntryPointJwt.java      # JWT authentication entry point
│   │   │   ├── AuthTokenFilter.java        # JWT authentication filter
│   │   │   ├── JwtUtils.java               # JWT utility methods
│   │   │   ├── WebSecurityConfig.java      # Security configuration
│   │   │   └── WebSocketConfig.java        # WebSocket configuration
│   │   ├── controller/                      # REST controllers
│   │   │   ├── AuthController.java         # Authentication endpoints
│   │   │   ├── ChatController.java         # Chat management
│   │   │   ├── FileController.java         # File upload/download
│   │   │   ├── GroupController.java        # Group management
│   │   │   ├── MessageController.java      # Messaging endpoints
│   │   │   ├── UserController.java         # User management
│   │   │   └── WebSocketController.java    # Real-time messaging
│   │   ├── dto/                            # Data Transfer Objects
│   │   │   ├── LoginDto.java
│   │   │   ├── MessageDto.java
│   │   │   ├── UserDto.java
│   │   │   └── UserRegistrationDto.java
│   │   ├── entity/                         # JPA entities
│   │   │   ├── Chat.java                   # Chat conversations
│   │   │   ├── Group.java                  # Group entities
│   │   │   ├── Message.java                # Message entities
│   │   │   └── User.java                   # User entities
│   │   ├── repository/                     # Data access layer
│   │   │   ├── ChatRepository.java
│   │   │   ├── GroupRepository.java
│   │   │   ├── MessageRepository.java
│   │   │   └── UserRepository.java
│   │   └── service/                        # Business logic
│   │       ├── ChatService.java
│   │       ├── FileService.java
│   │       ├── GroupService.java
│   │       ├── MessageService.java
│   │       ├── UserDetailsServiceImpl.java
│   │       ├── UserPrincipal.java
│   │       └── UserService.java
│   └── resources/
│       └── application.properties          # Application configuration
└── test/                                   # Test classes
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Installation & Running

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd WhatsApp
   ```

2. **Build the application**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Application runs on: `http://localhost:8080`
   - H2 Database Console: `http://localhost:8080/h2-console`
   - WebSocket endpoint: `ws://localhost:8080/ws`

### Database Configuration

**Default (H2 In-Memory):**
```properties
spring.datasource.url=jdbc:h2:mem:whatsapp
spring.datasource.username=sa
spring.datasource.password=
```

**For MySQL:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/whatsapp
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

## API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "phoneNumber": "+1234567890",
  "fullName": "John Doe",
  "password": "password123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "identifier": "user@example.com",
  "password": "password123"
}
```

### User Management

#### Get Current User
```http
GET /api/users/me
Authorization: Bearer <jwt_token>
```

#### Search Users
```http
GET /api/users/search?keyword=john
Authorization: Bearer <jwt_token>
```

#### Add Contact
```http
POST /api/users/contacts/{contactId}
Authorization: Bearer <jwt_token>
```

### Messaging

#### Send Private Message
```http
POST /api/messages/private?recipientId=2&content=Hello!
Authorization: Bearer <jwt_token>
```

#### Get Private Messages
```http
GET /api/messages/private/{userId}?page=0&size=20
Authorization: Bearer <jwt_token>
```

#### Send Group Message
```http
POST /api/messages/group?groupId=1&content=Hello group!
Authorization: Bearer <jwt_token>
```

### Group Management

#### Create Group
```http
POST /api/groups?name=My Group&description=Group description
Authorization: Bearer <jwt_token>
```

#### Add Member to Group
```http
POST /api/groups/{groupId}/members/{userId}
Authorization: Bearer <jwt_token>
```

### File Upload

#### Upload File
```http
POST /api/files/upload
Authorization: Bearer <jwt_token>
Content-Type: multipart/form-data

file: <file>
recipientId: <recipient_id>
```

## WebSocket Real-time Features

### Connection
Connect to WebSocket endpoint: `ws://localhost:8080/ws`

### Message Types

#### Send Message
```javascript
stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({
  content: "Hello!",
  recipientId: 2,
  type: "PRIVATE"
}));
```

#### Join Chat
```javascript
stompClient.send("/app/chat.addUser", {}, JSON.stringify({
  userId: 1
}));
```

#### Typing Indicator
```javascript
stompClient.send("/app/chat.typing", {}, JSON.stringify({
  recipientId: 2,
  isTyping: true
}));
```

### Subscription Topics

- `/user/queue/messages` - Private messages
- `/topic/group/{groupId}` - Group messages
- `/topic/user.status` - User status updates
- `/user/queue/typing` - Typing indicators

## Configuration

### JWT Configuration
```properties
jwt.secret=mySecretKey
jwt.expiration=86400000
```

### File Upload Configuration
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## Database Schema

The application automatically creates the following tables:
- `users` - User information
- `groups` - Group information
- `messages` - Chat messages
- `chats` - Private conversations
- `user_contacts` - User contact relationships
- `blocked_users` - Blocked user relationships
- `group_members` - Group membership

## Security

- JWT-based authentication
- Password encryption with BCrypt
- CORS configuration for cross-origin requests
- Secure file upload validation

## Development

### Adding New Features
1. Create entity in `entity` package
2. Add repository in `repository` package
3. Implement service in `service` package
4. Create controller in `controller` package
5. Add DTO if needed in `dto` package

### Testing
```bash
mvn test
```

## Deployment

### Production Configuration
1. Update database configuration for production database
2. Set proper JWT secret
3. Configure file storage location
4. Enable HTTPS
5. Set proper CORS origins

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/whatsapp-clone-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.