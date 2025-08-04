# MySQL Setup Guide for WhatsApp Application

## Option 1: Install MySQL Locally

### On Ubuntu/Debian:
```bash
sudo apt update
sudo apt install mysql-server mysql-client
sudo systemctl start mysql
sudo systemctl enable mysql

# Set up root password and create database
sudo mysql -u root -p
```

### On Windows:
1. Download MySQL Installer from https://dev.mysql.com/downloads/installer/
2. Install MySQL Server and Workbench
3. Set root password to "Admin@123" during installation

### On macOS:
```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

## Option 2: Use Docker (Recommended for Development)

Create a `docker-compose.yml` file:

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: whatsapp-mysql
    environment:
      MYSQL_ROOT_PASSWORD: Admin@123
      MYSQL_DATABASE: whatsapp
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

Run the container:
```bash
docker-compose up -d
```

## Create Database

Once MySQL is running, create the database:

```sql
CREATE DATABASE IF NOT EXISTS whatsapp;
USE whatsapp;
```

## Verify Connection

Test the connection:
```bash
mysql -u root -p -h localhost -P 3306 whatsapp
```

Enter password: `Admin@123`

If successful, you can now run the WhatsApp application with MySQL configuration.