# SecureAuth API - Enterprise Authentication System

A production-ready Spring Boot authentication system featuring OAuth2 (Google & GitHub), email/password authentication with OTP-based 2FA, JWT token generation, and comprehensive security features.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Environment Setup](#environment-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Production Deployment](#production-deployment)
- [Security Considerations](#security-considerations)
- [Roadmap](#roadmap)
- [Troubleshooting](#troubleshooting)
- [Support](#support)
- [License](#license)

---

## Overview

SecureAuth API is an enterprise-grade authentication microservice that provides secure user authentication through multiple methods including traditional email/password with OTP verification and modern OAuth2 social authentication (Google & GitHub). Built with Spring Boot 3.1.5, it includes JWT token generation, email verification, and comprehensive security configurations.

---

## Features

### Current Features

**Authentication Methods**
- Email/Password authentication with secure password hashing
- One-Time Password (OTP) for 2FA, registration verification, and password reset
- Google OAuth2 seamless account integration
- GitHub OAuth2 direct authentication support

**Security Features**
- JWT token generation with configurable expiration
- BCrypt password hashing with salt
- Time-limited OTP codes (5-minute expiry) with single-use enforcement
- CORS configuration for frontend integration
- Input validation using Jakarta validation annotations
- Stateless authentication using JWT tokens

**User Management**
- User profile management including profile pictures
- Email verification workflow
- Support for multiple authentication providers (Local, Google, GitHub)
- Automatic user creation on OAuth2 first login

**Email System**
- Professional HTML email templates for OTP and welcome emails
- Gmail SMTP integration with TLS support
- Non-blocking email dispatch
- Thymeleaf-based dynamic email templates

**Developer Experience**
- Health monitoring endpoint
- Debug-level logging for troubleshooting
- Comprehensive exception handling with meaningful responses

### Planned Features (Roadmap)

**Phase 2 - Enhanced Security**
1. Refresh Token System - Implement refresh tokens for extended sessions with automatic token rotation
2. Account Linking - Enable users to link multiple OAuth2 accounts to a single profile
3. Rate Limiting - Implement API rate limiting to prevent brute force attacks
4. Audit Logs & Error Handling - Comprehensive audit logging and improved error tracking

---

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.1.5 |
| Security | Spring Security + OAuth2 | 3.1.5 |
| Database | MariaDB | 10.11+ |
| ORM | JPA/Hibernate | 3.1.5 |
| Token | JWT (JJWT) | 0.11.5 |
| Build | Maven | 3.8+ |
| Java | OpenJDK | 17+ |
| Email | Spring Mail + Thymeleaf | 3.1.5 |

---

## Project Structure

```
SecureAuth-Api/
├── src/main/java/com/SecureAuth/SecureAuth/Api/
│   ├── SecureAuthApiApplication.java          # Main application entry point
│   ├── Services/
│   │   ├── AuthService.java                   # Core authentication logic
│   │   ├── OTPService.java                    # OTP generation and verification
│   │   ├── EmailService.java                  # Email sending functionality
│   │   ├── JwtService.java                    # JWT token operations
│   │   └── CustomOAuth2UserService.java       # OAuth2 user processing
│   ├── controller/
│   │   ├── AuthController.java                # Authentication endpoints
│   │   └── OAuth2Controller.java              # OAuth2 configuration endpoints
│   ├── model/
│   │   ├── User.java                          # User entity
│   │   ├── OTP.java                           # OTP entity
│   │   └── OAuth2UserInfo.java                # OAuth2 user info DTO
│   ├── repository/
│   │   ├── UserRepository.java                # User data access
│   │   └── OTPRepository.java                 # OTP data access
│   ├── config/
│   │   └── SecurityConfig.java                # Spring Security configuration
│   ├── security/
│   │   └── OAuth2LoginSuccessHandler.java     # OAuth2 success handler
│   └── dto/
│       ├── RegisterRequest.java               # Registration DTO
│       ├── LoginRequest.java                  # Login DTO
│       ├── OTPRequest.java                    # OTP DTO
│       └── AuthResponse.java                  # Response DTO
├── src/main/resources/
│   ├── application.yml                        # Main configuration
│   └── templates/
│       ├── email-otp.html                     # OTP email template
│       └── welcome-email.html                 # Welcome email template
├── .env                                       # Environment variables
├── pom.xml                                    # Maven dependencies
└── target/                                    # Compiled artifacts
```

---

## Prerequisites

Before installation, ensure you have the following:

- Java 17 or higher (OpenJDK or Oracle JDK)
- Maven 3.8 or higher
- MariaDB 10.11 or higher
- Git version control system
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### External Accounts Required
- Google Cloud Console account for Google OAuth2
- GitHub developer account for GitHub OAuth2
- Gmail account for SMTP email configuration

---

## Installation

### Step 1: Clone Repository

```bash
git clone https://github.com/yourusername/SecureAuth-Api.git
cd SecureAuth-Api
```

### Step 2: Create Database

```bash
mariadb -u root -p

CREATE DATABASE auth_oauth2;
USE auth_oauth2;
```

Or use MySQL Workbench.

### Step 3: Configure OAuth2 Credentials

#### Google OAuth2 Setup

1. Go to Google Cloud Console (https://console.cloud.google.com)
2. Create a new project
3. Navigate to APIs & Services > Credentials
4. Click Create Credentials > OAuth 2.0 Client IDs
5. Choose Web application
6. Add authorized redirect URIs:
   - http://localhost:8083/oauth2/callback/google
   - https://yourdomain.com/oauth2/callback/google (production)
7. Copy Client ID and Client Secret

#### GitHub OAuth2 Setup

1. Go to GitHub Settings (https://github.com/settings/developers)
2. Click New OAuth App
3. Fill in:
   - Application Name: SecureAuth
   - Homepage URL: http://localhost:3000
   - Authorization callback URL: http://localhost:8083/oauth2/callback/github
4. Copy Client ID and Client Secret

#### Gmail SMTP Setup

1. Enable 2-Step Verification on Google Account (https://myaccount.google.com/security)
2. Generate App Password (https://myaccount.google.com/apppasswords):
   - Select Mail and Other (name: "SecureAuth")
   - Copy the 16-digit password

---

## Environment Setup

### Step 1: Create .env File

```bash
touch .env
```

### Step 2: Add Environment Variables

```env
# Database Configuration
DB_URL=jdbc:mariadb://localhost:3306/auth_oauth2
DB_USERNAME=root
DB_PASSWORD=your-mariadb-password

# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id-here
GOOGLE_CLIENT_SECRET=your-google-client-secret-here

# GitHub OAuth2
GITHUB_CLIENT_ID=your-github-client-id-here
GITHUB_CLIENT_SECRET=your-github-client-secret-here

# Email Configuration (Gmail)
MAIL_USERNAME=secureauth.protocol@gmail.com
MAIL_PASSWORD=your-16-digit-app-password

# JWT Configuration
JWT_SECRET=YourSuperSecretKeyForJWT1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ
JWT_EXPIRATION=86400000

# Frontend URL
FRONTEND_URL=http://localhost:3000
```

### Step 3: Load Environment Variables

**Linux/Mac:**
```bash
set -o allexport
source .env
set +o allexport
```

**Single Command (Linux/Mac):**
```bash
export $(cat .env | xargs) && mvn spring-boot:run
```

**Windows (PowerShell):**
```powershell
$envFile = Get-Content .env
foreach ($line in $envFile) {
    if ($line -and -not $line.StartsWith("#")) {
        $parts = $line -split "=", 2
        [System.Environment]::SetEnvironmentVariable($parts[0], $parts[1], "Process")
    }
}
mvn spring-boot:run
```

---

## Configuration

### application.yml

The application is configured to use environment variables:

```yaml
server:
  port: 8083
  servlet:
    context-path: /

spring:
  application:
    name: secureauth-oauth2

  # DATABASE CONFIGURATION
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.mariadb.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

  # JPA / HIBERNATE
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MariaDBDialect

  # OAUTH2 CONFIGURATION
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: profile,email
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope: user:email,read:user

  # EMAIL CONFIGURATION
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          debug: false

  # THYMELEAF
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    mode: HTML
    encoding: UTF-8
    cache: false

# JWT CONFIGURATION
jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION}

# FRONTEND URL
frontend:
  url: ${FRONTEND_URL}

# LOGGING
logging:
  level:
    com.auth: DEBUG
    org.springframework.security: INFO
```

---

## Running the Application

### Method 1: Using Environment Variables (Linux/Mac)

```bash
set -o allexport
source .env
set +o allexport
mvn spring-boot:run
```

### Method 2: Single Command (Linux/Mac)

```bash
export $(cat .env | xargs) && mvn spring-boot:run
```

### Method 3: Using IDE

1. Open project in IDE
2. Install EnvFile plugin (IntelliJ IDEA)
3. Configure run configuration to load .env
4. Click Run

### Verify Application Started

Once running, you should see:

```
============================================
SECUREAUTH OAUTH2 + OTP SYSTEM STARTED!
============================================
Local Server: http://localhost:8083
Email OTP: READY
Google OAuth2: READY
GitHub OAuth2: READY
============================================
```

---

## API Documentation

### Base URL

```
http://localhost:8083
```

### Authentication Endpoints

#### Register User

POST /api/auth/register

Request:
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123",
  "name": "John Doe"
}
```

Response (200 OK):
```json
{
  "success": true,
  "message": "OTP sent to your email. Please verify."
}
```

#### Verify Registration OTP

POST /api/auth/verify-registration

Request:
```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```

Response (200 OK):
```json
{
  "success": true,
  "message": "Registration successful!"
}
```

#### Login User

POST /api/auth/login

Request:
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

Response (200 OK):
```json
{
  "success": true,
  "message": "OTP sent to your email"
}
```

#### Verify Login OTP

POST /api/auth/verify-login

Request:
```json
{
  "email": "user@example.com",
  "otp": "654321"
}
```

Response (200 OK):
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Get User Profile

GET /api/auth/profile?email=user@example.com

Response (200 OK):
```json
{
  "success": true,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "profilePicture": null,
    "provider": "LOCAL",
    "emailVerified": true
  }
}
```

#### Health Check

GET /api/auth/health

Response (200 OK):
```json
{
  "status": "UP",
  "service": "Authentication Service",
  "timestamp": 1703799660000
}
```

### OAuth2 Endpoints

#### Get OAuth2 Configuration

GET /api/oauth2/config

Response (200 OK):
```json
{
  "google": {
    "authUrl": "http://localhost:8083/oauth2/authorization/google",
    "clientId": "your-google-client-id"
  },
  "github": {
    "authUrl": "http://localhost:8083/oauth2/authorization/github",
    "clientId": "your-github-client-id"
  },
  "frontendCallback": "http://localhost:3000/oauth2/callback"
}
```

#### Google OAuth2 Login

GET /oauth2/authorization/google

Redirects to Google login. After authentication, redirects to frontend callback with JWT token.

#### GitHub OAuth2 Login

GET /oauth2/authorization/github

Redirects to GitHub login. After authentication, redirects to frontend callback with JWT token.

---

## Testing

### Using cURL

**Register:**
```bash
curl -X POST http://localhost:8083/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123456",
    "name": "Test User"
  }'
```

**Verify OTP:**
```bash
curl -X POST http://localhost:8083/api/auth/verify-registration \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otp": "123456"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123456"
  }'
```

**Verify Login OTP:**
```bash
curl -X POST http://localhost:8083/api/auth/verify-login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otp": "654321"
  }'
```

**Get Profile:**
```bash
curl -X GET "http://localhost:8083/api/auth/profile?email=test@example.com"
```

**Health Check:**
```bash
curl -X GET http://localhost:8083/api/auth/health
```

### Using Postman

1. Download Postman (https://www.postman.com/downloads/)
2. Create new collection
3. Add requests:
   - POST http://localhost:8083/api/auth/register
   - POST http://localhost:8083/api/auth/verify-registration
   - POST http://localhost:8083/api/auth/login
   - POST http://localhost:8083/api/auth/verify-login
   - GET http://localhost:8083/api/auth/profile
4. Copy JSON bodies from API documentation
5. Send and check responses

---

## Production Deployment

### Method 1: Systemd Service (Linux)

**Step 1: Build Application**
```bash
mvn clean package
```

**Step 2: Copy to Server**
```bash
scp target/secureauth-api-1.0.0.jar user@yourserver.com:/home/app/
scp .env user@yourserver.com:/home/app/
```

**Step 3: Create Service File**

Create /etc/systemd/system/secureauth.service:

```ini
[Unit]
Description=SecureAuth API Service
After=network.target

[Service]
Type=simple
User=app
WorkingDirectory=/home/app
EnvironmentFile=/home/app/.env
ExecStart=/usr/bin/java -jar /home/app/secureauth-api-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**Step 4: Start Service**
```bash
sudo systemctl daemon-reload
sudo systemctl start secureauth
sudo systemctl enable secureauth
sudo systemctl status secureauth
```

### Method 2: Screen (Simple Deployment)

**Step 1: Install Screen**
```bash
sudo apt-get install screen
```

**Step 2: Create Session**
```bash
screen -S secureauth-api
cd /path/to/application
set -o allexport
source .env
set +o allexport
mvn spring-boot:run
```

**Step 3: Detach**
Press: Ctrl+A then D

**Step 4: Reattach**
```bash
screen -r secureauth-api
```

### Method 3: Manual Deployment

```bash
# SSH to server
ssh user@yourserver.com
cd /home/app

# Load environment and run
set -o allexport
source .env
set +o allexport
nohup java -jar secureauth-api-1.0.0.jar > app.log 2>&1 &

# Monitor logs
tail -f app.log
```

### Production Checklist

- [ ] Update JWT_SECRET with strong random key (32+ characters)
- [ ] Change all default database passwords
- [ ] Use strong Gmail app password
- [ ] Update FRONTEND_URL to production domain
- [ ] Enable HTTPS/SSL on domain
- [ ] Configure firewall to allow port 8083 only to reverse proxy
- [ ] Set up automated database backups
- [ ] Enable logging and monitoring
- [ ] Test OAuth2 redirect URIs with production domain
- [ ] Set ddl-auto to validate (not update)

### Nginx Reverse Proxy

Create /etc/nginx/sites-available/secureauth:

```nginx
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    location / {
        proxy_pass http://localhost:8083;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable:
```bash
sudo ln -s /etc/nginx/sites-available/secureauth /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

## Security Considerations

### Password Security
- Passwords hashed using BCrypt with random salt
- Passwords never stored in plain text
- Minimum length: 6 characters (recommend 12+ for production)

### OTP Security
- 6-digit random numbers generated using SecureRandom
- Expire after 5 minutes
- Single-use enforcement
- Old OTPs deleted on new generation

### JWT Security
- Signed using HS256 algorithm
- Secret key minimum 32 characters
- Token expiration: 24 hours (configurable)
- Includes user ID and provider information

### Email Security
- Emails sent over TLS
- Gmail app passwords used (not account passwords)
- OTP codes sent via encrypted email

### Best Practices
1. Change default secret keys before production
2. Use environment variables for all secrets
3. Never commit credentials to Git
4. Enable HTTPS/SSL in production
5. Implement rate limiting (planned feature)
6. Monitor audit logs (planned feature)
7. Validate OAuth2 redirect URIs strictly
8. Use strong database passwords
9. Enable database encryption
10. Set up automated backups

---

## Roadmap

### Phase 1 (Current) - Core Authentication
- Email/Password authentication with OTP
- Google and GitHub OAuth2
- JWT token generation
- User profile management

### Phase 2 (Planned)
1. **Refresh Token System** - Implement refresh tokens for extended sessions with automatic token rotation
2. **Account Linking** - Allow users to link multiple OAuth2 accounts to single profile
3. **Rate Limiting** - Prevent brute force attacks on login and OTP endpoints
4. **Audit Logs & Error Handling** - Comprehensive audit trail and improved error tracking

### Phase 3 (Future)
- Two-factor authentication with authenticator apps
- Password reset flow
- User deactivation and deletion
- Session management dashboard
- API key authentication for third-party integrations

---

## Troubleshooting

### Application Won't Start - Environment Variables Not Found

Solution:
```bash
# Load .env first
set -o allexport
source .env
set +o allexport
mvn spring-boot:run

# Or single command
export $(cat .env | xargs) && mvn spring-boot:run
```

### Email Not Sending

Solution:
- Verify Gmail credentials in .env
- Use 16-digit App Password (not regular password)
- Ensure 2-Step Verification enabled
- Check SMTP settings in application.yml
- Review email logs

### OAuth2 Redirect Loop

Solution:
- Verify redirect URIs match exactly in Google Cloud Console and GitHub
- Include http:// or https:// protocol
- Ensure port matches (8083)
- Clear browser cookies and cache
- Check logs for URL mismatches

### Database Connection Failed

Solution:
- Verify MariaDB is running
- Check credentials in .env
- Ensure database exists
- Test connection: mysql -u root -p -h localhost auth_oauth2

### OTP Verification Fails

Solution:
- Check OTP hasn't expired (5 minutes)
- Verify exact OTP code
- Ensure correct email
- Check database: SELECT * FROM otps WHERE email='user@example.com';

### JWT Token Invalid

Solution:
- Verify JWT_SECRET in .env
- Check token hasn't expired
- Ensure Bearer token in Authorization header
- Verify JWT_EXPIRATION value

### Enable Debug Logging

```bash
export LOGGING_LEVEL_COM_AUTH=DEBUG
mvn spring-boot:run
```

---

## Support

For support and questions:

Email: secureauth.protocol@gmail.com

GitHub Issues: Create an issue in repository

---

## License

This project is licensed under MIT License. See LICENSE file for details.

---

## Version

Version: 1.0.0
Last Updated: December 28, 2025
Status: Production Ready


                                     I don’t just write code — I orbit ideas until they ignite.   


Built with ❤️ by Aman Deep
