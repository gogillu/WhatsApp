# MySQL Setup and Troubleshooting Guide

## Fixed Issues

### 1. ✅ Login Authentication Issue
**Problem**: Signup worked but login with same credentials failed with "Invalid credentials"

**Root Cause**: JWT token generation was failing due to:
- JWT secret key was too short for HS512 algorithm (required 64+ bytes)
- Application was using HS512 which has strict key length requirements

**Solution**: 
- Changed JWT algorithm from HS512 to HS256 (less strict requirements)
- Updated JWT secret to a longer, more secure key
- Added proper error handling in authentication flow

### 2. ✅ Database Configuration
**Problem**: Mixed H2/MySQL configuration causing startup failures

**Solution**: Clean separation of H2 (development) and MySQL (production) configs

## Current Working Configuration

The application now works with both:

### MySQL (Production)
```properties
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/whatsapp?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=Admin@123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

### H2 (Development/Testing)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

## MySQL Installation Options

### Option 1: Docker (Recommended)
```bash
# Start MySQL container
docker run -d --name whatsapp-mysql \
  -e MYSQL_ROOT_PASSWORD=Admin@123 \
  -e MYSQL_DATABASE=whatsapp \
  -p 3306:3306 \
  mysql:8.0

# Verify connection
docker exec -it whatsapp-mysql mysql -u root -pAdmin@123 -e "SHOW DATABASES;"
```

### Option 2: Local MySQL Installation

#### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
sudo mysql -u root -p
```

#### Windows:
Download from: https://dev.mysql.com/downloads/mysql/

#### macOS:
```bash
brew install mysql
brew services start mysql
```

### Option 3: Cloud MySQL
- AWS RDS
- Google Cloud SQL  
- Azure Database for MySQL
- PlanetScale
- Supabase

## Database Setup Commands

### Create Database and User
```sql
CREATE DATABASE whatsapp;
CREATE USER 'whatsapp_user'@'localhost' IDENTIFIED BY 'Admin@123';
GRANT ALL PRIVILEGES ON whatsapp.* TO 'whatsapp_user'@'localhost';
FLUSH PRIVILEGES;
```

### Verify Tables Created
```sql
USE whatsapp;
SHOW TABLES;
DESCRIBE users;
```

## Common MySQL Issues and Solutions

### Issue 1: Connection Refused
**Error**: `Connection refused to MySQL`
**Solution**: 
1. Check if MySQL is running: `sudo systemctl status mysql`
2. Start MySQL: `sudo systemctl start mysql`
3. Check port: `netstat -tlnp | grep 3306`

### Issue 2: Authentication Plugin Issues
**Error**: `Authentication plugin 'caching_sha2_password' cannot be loaded`
**Solution**:
```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'Admin@123';
FLUSH PRIVILEGES;
```

### Issue 3: Database Doesn't Exist
**Error**: `Unknown database 'whatsapp'`
**Solution**: The application URL includes `createDatabaseIfNotExist=true` which should auto-create the database. If it doesn't work:
```sql
CREATE DATABASE whatsapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Issue 4: Time Zone Issues
**Error**: `The server time zone value 'UTC' is unrecognized`
**Solution**: URL parameter `serverTimezone=UTC` should fix this. Alternative:
```sql
SET GLOBAL time_zone = '+00:00';
```

## Application Features Verified Working ✅

1. **User Registration**: Creates users with encrypted passwords
2. **User Authentication**: Login/logout with JWT tokens  
3. **Real-time Messaging**: WebSocket-based chat
4. **File Upload/Download**: Multipart file handling
5. **Contact Management**: Add/remove contacts
6. **Group Chats**: Create and manage groups
7. **Database Persistence**: All data stored properly
8. **Web Interface**: Complete HTML UI with responsive design

## Development vs Production

### Development (H2)
- In-memory database
- No installation required
- Data lost on restart
- H2 console available at `/h2-console`

### Production (MySQL)
- Persistent database
- Requires MySQL installation
- Better performance
- Production-ready

## Testing the Application

1. **Start Application**: `mvn spring-boot:run`
2. **Access Web UI**: http://localhost:8080
3. **Register User**: Use the registration form
4. **Login**: Use same credentials to login
5. **Chat**: Access real-time messaging features

## Environment Variables (Optional)

For better security in production:

```bash
export DB_URL=jdbc:mysql://localhost:3306/whatsapp
export DB_USERNAME=root
export DB_PASSWORD=Admin@123
export JWT_SECRET=your-super-secure-jwt-secret-key-here
```

Then update `application.properties`:
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```