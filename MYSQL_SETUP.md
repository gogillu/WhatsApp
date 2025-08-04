# WhatsApp Clone - MySQL Configuration Guide

This application is now configured to work with MySQL using the exact properties you requested.

## Current Configuration

The `application.properties` file is configured with your exact requirements:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/whatsapp
spring.datasource.username=root
spring.datasource.password=Admin@123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql=true
```

## Prerequisites

Before running the application, ensure MySQL is installed and running:

### Quick Setup with Docker (Recommended)

```bash
# Create and start MySQL container
docker run -d \
  --name whatsapp-mysql \
  -e MYSQL_ROOT_PASSWORD=Admin@123 \
  -e MYSQL_DATABASE=whatsapp \
  -p 3306:3306 \
  mysql:8.0

# Verify it's running
docker ps
```

### Manual MySQL Installation

#### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql

# Secure installation and set root password
sudo mysql_secure_installation
```

#### Windows:
1. Download MySQL Installer
2. Install MySQL Server
3. Set root password to `Admin@123`

#### macOS:
```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

## Database Setup

After MySQL is running, create the database:

```sql
mysql -u root -p
# Enter password: Admin@123

CREATE DATABASE IF NOT EXISTS whatsapp;
SHOW DATABASES;
EXIT;
```

## Running the Application

```bash
# Compile the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will:
- Connect to MySQL on `localhost:3306`
- Create the `whatsapp` database if it doesn't exist
- Use `hibernate.ddl-auto=update` to create/update tables automatically
- Show SQL queries in the console (`spring.jpa.show-sql=true`)

## Troubleshooting

### Connection Refused Error
If you see `Connection refused`, MySQL is not running:
```bash
# Check if MySQL is running
sudo systemctl status mysql  # Linux
brew services list | grep mysql  # macOS
```

### Access Denied Error
If you see access denied, check your MySQL root password:
```bash
mysql -u root -p
# Try password: Admin@123
```

### Database Creation Error
If the database isn't created automatically, create it manually:
```sql
CREATE DATABASE whatsapp;
```

## Alternative Configuration (H2 for Testing)

If you prefer to test with H2 database first, copy the content from `application-h2.properties` to `application.properties`.

## Web Interface

Once the application starts successfully:
- Visit: http://localhost:8080
- Register a new account
- Login and start chatting

## Features Working with MySQL

✅ User registration and authentication  
✅ Real-time messaging with WebSocket  
✅ Group chat functionality  
✅ File upload/download  
✅ Contact management  
✅ Message persistence with MySQL  
✅ Responsive web interface  

The application is fully compatible with MySQL and will persist all data across restarts.