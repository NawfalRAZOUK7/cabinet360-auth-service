# Cabinet360 Auth Service

🔐 **Secure Authentication & Authorization Microservice** - JWT-based authentication system with role-based access control for healthcare professionals and patients.

## 🌟 Features

### 🛡️ Authentication
- **Secure registration** with email verification
- **JWT-based login** with configurable expiration
- **Password encryption** using BCrypt
- **Session management** with token refresh capabilities
- **Multi-factor authentication** support (planned)

### 👥 Role-Based Access Control (RBAC)
- **DOCTOR**: Full access to patient data and appointments
- **ASSISTANT**: Limited access for administrative support
- **PATIENT**: Access to personal data and appointments
- **ADMIN**: System administration and user management

### 🏥 Admin Management
- **Doctor approval workflow** for new registrations
- **User status management** (active, suspended, pending)
- **Bulk user operations** for organizational management
- **Audit logging** for compliance requirements

### 🔒 Security Features
- **Password policies** with complexity requirements
- **Account lockout** after failed attempts
- **JWT secret rotation** capabilities
- **CORS protection** for web applications
- **Input validation** and sanitization

## 🏗️ Architecture

~~~
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Client    │    │  Mobile App     │    │  Other Services │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
              ┌─────────────────────────────────┐
              │        Auth Service             │
              │         (Port 8091)             │
              └─────────────────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   PostgreSQL    │
                    │  auth_service_db│
                    └─────────────────┘
~~~

## 🚀 Quick Start

### Prerequisites
- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Docker** (for containerized deployment)

### 1. Environment Setup

Create `.env` file:
~~~env
# Server Configuration
PORT=8091

# Database Configuration
DB_HOST=localhost
DB_PORT=5441
DB_NAME=auth_service_db
DB_USERNAME=user
DB_PASSWORD=pass

# JWT Configuration
JWT_SECRET=cabinet360supersecurekeymustbeatleast32chars!
JWT_EXPIRATION=86400000

# Security Settings
BCRYPT_STRENGTH=12
MAX_LOGIN_ATTEMPTS=5
ACCOUNT_LOCKOUT_MINUTES=30

# Email Configuration (for verification)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password
~~~

### 2. Database Setup

~~~sql
-- Create database and user
CREATE DATABASE auth_service_db;
CREATE USER auth_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE auth_service_db TO auth_user;

-- Connect to database
\c auth_service_db;

-- Tables will be auto-created by JPA/Hibernate
~~~

### 3. Run the Service

~~~bash
# Development mode
mvn spring-boot:run

# With custom profile
mvn spring-boot:run -Dspring.profiles.active=dev

# Production build
mvn clean package
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
~~~

### 4. Docker Deployment

~~~bash
# Build Docker image
docker build -t cabinet360/auth-service .

# Run with Docker Compose
docker-compose up -d auth-service

# Or run standalone
docker run -p 8091:8091 --env-file .env cabinet360/auth-service
~~~

## 📚 API Documentation

### Base URL: `http://localhost:8091/api/v1/auth`

### 🔓 Public Endpoints

#### Register User
~~~http
POST /register
Content-Type: application/json

{
  "email": "doctor@hospital.com",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!",
  "role": "DOCTOR",
  "firstName": "Dr. John",
  "lastName": "Smith",
  "speciality": "Cardiology",
  "licenseNumber": "MD123456"
}
~~~

**Response:**
~~~json
{
  "success": true,
  "message": "Registration successful. Awaiting admin approval.",
  "userId": 123,
  "status": "PENDING_APPROVAL"
}
~~~

#### Login
~~~http
POST /login
Content-Type: application/json

{
  "email": "doctor@hospital.com",
  "password": "SecurePass123!"
}
~~~

**Response:**
~~~json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400000,
  "user": {
    "id": 123,
    "email": "doctor@hospital.com",
    "role": "DOCTOR",
    "firstName": "Dr. John",
    "lastName": "Smith",
    "isActive": true
  }
}
~~~

#### Password Reset Request
~~~http
POST /password/reset-request
Content-Type: application/json

{
  "email": "doctor@hospital.com"
}
~~~

#### Reset Password
~~~http
POST /password/reset
Content-Type: application/json

{
  "token": "reset_token_here",
  "newPassword": "NewSecurePass123!",
  "confirmPassword": "NewSecurePass123!"
}
~~~

### 🔒 Protected Endpoints

#### Validate Token
~~~http
GET /validate
Authorization: Bearer <jwt_token>
~~~

**Response:**
~~~json
{
  "valid": true,
  "user": {
    "id": 123,
    "email": "doctor@hospital.com",
    "role": "DOCTOR",
    "permissions": ["READ_PATIENTS", "WRITE_APPOINTMENTS"]
  }
}
~~~

#### Refresh Token
~~~http
POST /refresh
Authorization: Bearer <jwt_token>
~~~

#### Update Profile
~~~http
PUT /profile
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "firstName": "Dr. John",
  "lastName": "Smith",
  "speciality": "Cardiology",
  "phone": "+1-555-0123"
}
~~~

#### Change Password
~~~http
PUT /password/change
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "currentPassword": "CurrentPass123!",
  "newPassword": "NewSecurePass123!",
  "confirmPassword": "NewSecurePass123!"
}
~~~

### 👑 Admin Endpoints

#### Get Pending Approvals
~~~http
GET /admin/pending-approvals
Authorization: Bearer <admin_jwt_token>
~~~

**Response:**
~~~json
{
  "users": [
    {
      "id": 124,
      "email": "newdoctor@hospital.com",
      "role": "DOCTOR",
      "firstName": "Jane",
      "lastName": "Doe",
      "speciality": "Pediatrics",
      "licenseNumber": "MD789012",
      "registrationDate": "2024-12-01T10:00:00Z",
      "status": "PENDING_APPROVAL"
    }
  ],
  "totalCount": 1
}
~~~

#### Approve/Reject User
~~~http
POST /admin/approve-user
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json

{
  "userId": 124,
  "approved": true,
  "reason": "Valid credentials verified"
}
~~~

#### Get User Statistics
~~~http
GET /admin/user-stats
Authorization: Bearer <admin_jwt_token>
~~~

**Response:**
~~~json
{
  "totalUsers": 150,
  "activeUsers": 142,
  "pendingApprovals": 3,
  "suspendedUsers": 5,
  "usersByRole": {
    "DOCTOR": 45,
    "ASSISTANT": 28,
    "PATIENT": 75,
    "ADMIN": 2
  },
  "registrationsToday": 3,
  "registrationsThisMonth": 28
}
~~~

#### Suspend/Activate User
~~~http
PUT /admin/user/{userId}/status
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json

{
  "status": "SUSPENDED",
  "reason": "Violation of terms of service"
}
~~~

### 🏥 Health & Monitoring

#### Health Check
~~~http
GET /health
~~~

**Response:**
~~~json
{
  "status": "UP",
  "service": "auth-service",
  "timestamp": "2024-12-21T10:00:00Z",
  "version": "1.0.0",
  "database": "UP",
  "dependencies": {
    "database": "CONNECTED",
    "smtp": "AVAILABLE"
  }
}
~~~

## 🗄️ Database Schema

### User Tables

~~~sql
-- Main user table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    is_email_verified BOOLEAN DEFAULT false,
    status VARCHAR(20) DEFAULT 'PENDING_APPROVAL',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    last_login TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP
);

-- Doctor-specific information
CREATE TABLE doctor_users (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    speciality VARCHAR(100),
    license_number VARCHAR(50) UNIQUE,
    years_of_experience INTEGER,
    hospital_affiliation VARCHAR(200),
    consultation_fee DECIMAL(10,2),
    bio TEXT
);

-- Assistant-specific information  
CREATE TABLE assistant_users (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    assigned_doctor_id BIGINT REFERENCES doctor_users(id),
    department VARCHAR(100),
    hire_date DATE
);

-- Patient-specific information
CREATE TABLE patient_users (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    date_of_birth DATE,
    gender VARCHAR(10),
    phone VARCHAR(20),
    address TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    insurance_number VARCHAR(50),
    blood_type VARCHAR(5)
);

-- Admin-specific information
CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    admin_level INTEGER DEFAULT 1,
    department VARCHAR(100)
);

-- Password reset tokens
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Email verification tokens
CREATE TABLE email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Audit log for security events
CREATE TABLE auth_audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(50) NOT NULL,
    ip_address INET,
    user_agent TEXT,
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(255),
    timestamp TIMESTAMP DEFAULT NOW()
);
~~~

### Indexes for Performance

~~~sql
-- User lookup indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_active ON users(is_active);

-- Doctor lookup indexes
CREATE INDEX idx_doctor_users_user_id ON doctor_users(user_id);
CREATE INDEX idx_doctor_users_license ON doctor_users(license_number);
CREATE INDEX idx_doctor_users_speciality ON doctor_users(speciality);

-- Patient lookup indexes
CREATE INDEX idx_patient_users_user_id ON patient_users(user_id);

-- Token lookup indexes
CREATE INDEX idx_password_reset_token ON password_reset_tokens(token);
CREATE INDEX idx_email_verification_token ON email_verification_tokens(token);

-- Audit log indexes
CREATE INDEX idx_audit_log_user_id ON auth_audit_log(user_id);
CREATE INDEX idx_audit_log_timestamp ON auth_audit_log(timestamp);
CREATE INDEX idx_audit_log_action ON auth_audit_log(action);
~~~

## 🔧 Configuration

### Application Properties

~~~properties
# Server Configuration
server.port=${PORT:8091}

# Database Configuration
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5441}/${DB_NAME:auth_service_db}
spring.datasource.username=${DB_USERNAME:user}
spring.datasource.password=${DB_PASSWORD:pass}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=${DEV_MODE:false}
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=${JWT_SECRET:cabinet360supersecurekeymustbeatleast32chars!}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Security Configuration
security.bcrypt.strength=${BCRYPT_STRENGTH:12}
security.max-login-attempts=${MAX_LOGIN_ATTEMPTS:5}
security.lockout-duration-minutes=${ACCOUNT_LOCKOUT_MINUTES:30}

# Email Configuration
spring.mail.host=${SMTP_HOST:smtp.gmail.com}
spring.mail.port=${SMTP_PORT:587}
spring.mail.username=${SMTP_USERNAME:your_email@gmail.com}
spring.mail.password=${SMTP_PASSWORD:your_app_password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Logging Configuration
logging.level.com.cabinet360.auth=DEBUG
logging.level.org.springframework.security=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
~~~

## 🧪 Testing

### Unit Tests
~~~bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run with coverage
mvn test jacoco:report
~~~

### Integration Tests
~~~bash
# Run integration tests
mvn test -Dtest=**/*IntegrationTest

# Test with testcontainers
mvn test -Dspring.profiles.active=integration
~~~

### API Testing with Postman
~~~bash
# Import Postman collection
curl -o auth-service.postman_collection.json \
  https://raw.githubusercontent.com/YOUR_USERNAME/cabinet360-auth-service/main/postman/collection.json

# Run automated tests
newman run auth-service.postman_collection.json -e environment.json
~~~

### Load Testing
~~~bash
# Install Artillery
npm install -g artillery

# Run load tests
artillery run tests/load/auth-load-test.yml
~~~

## 🚀 Production Deployment

### Docker Production Build
~~~dockerfile
# Multi-stage build for production
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8091
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8091/api/v1/auth/health || exit 1
ENTRYPOINT ["java","-jar","/app.jar"]
~~~

### Kubernetes Deployment
~~~yaml
# auth-service-deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
  labels:
    app: auth-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
    spec:
      containers:
      - name: auth-service
        image: cabinet360/auth-service:latest
        ports:
        - containerPort: 8091
        env:
        - name: DB_HOST
          value: "postgres-service"
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: auth-secrets
              key: jwt-secret
        livenessProbe:
          httpGet:
            path: /api/v1/auth/health
            port: 8091
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /api/v1/auth/health
            port: 8091
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: auth-service
spec:
  selector:
    app: auth-service
  ports:
    - protocol: TCP
      port: 8091
      targetPort: 8091
  type: ClusterIP
~~~

## 🔐 Security Best Practices

### Password Security
~~~java
// Password validation rules
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
         message = "Password must contain at least 8 characters, including uppercase, lowercase, number and special character")
~~~

### JWT Security
~~~properties
# Secure JWT configuration
jwt.secret=${JWT_SECRET:CHANGE_THIS_IN_PRODUCTION}
jwt.expiration=86400000
jwt.refresh-expiration=604800000
jwt.issuer=cabinet360-auth-service
jwt.audience=cabinet360-services
~~~

### Rate Limiting
~~~java
// Implement rate limiting for login attempts
@RateLimiter(name = "login", fallbackMethod = "loginFallback")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // Login logic
}
~~~

## 📊 Monitoring & Observability

### Health Checks
~~~bash
# Service health
curl http://localhost:8091/api/v1/auth/health

# Detailed health with authentication
curl -H "Authorization: Bearer <token>" \
     http://localhost:8091/actuator/health

# Database connectivity
curl http://localhost:8091/actuator/health/db
~~~

### Metrics Collection
~~~properties
# Enable metrics
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
management.metrics.distribution.percentiles-histogram.http.server.requests=true
~~~

### Logging Configuration
~~~xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="!prod">
        <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
    
    <springProfile name="prod">
        <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <timestamp/>
                    <logLevel/>
                    <loggerName/>
                    <message/>
                    <mdc/>
                    <stackTrace/>
                </providers>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="JSON"/>
        </root>
    </springProfile>
</configuration>
~~~

## 🚨 Troubleshooting

### Common Issues

#### Database Connection Problems
~~~bash
# Check database connectivity
docker-compose exec auth-service-db psql -U user -d auth_service_db -c "SELECT 1;"

# View database logs
docker-compose logs auth-service-db

# Reset database
docker-compose down -v
docker-compose up -d auth-service-db
~~~

#### JWT Token Issues
~~~bash
# Verify JWT secret consistency
echo $JWT_SECRET | wc -c  # Should be at least 32 characters

# Test token generation
curl -X POST http://localhost:8091/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password"}'

# Decode JWT token (for debugging)
echo "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." | \
  cut -d'.' -f2 | base64 -d | jq
~~~

#### Memory/Performance Issues
~~~bash
# Monitor memory usage
docker stats auth-service

# Check for memory leaks
curl http://localhost:8091/actuator/metrics/jvm.memory.used

# Garbage collection stats
curl http://localhost:8091/actuator/metrics/jvm.gc.pause
~~~

#### Email Verification Problems
~~~bash
# Test SMTP configuration
curl -X POST http://localhost:8091/api/v1/auth/test-email \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'

# Check email logs
docker-compose logs auth-service | grep -i "email\|smtp"
~~~

### Debug Mode
~~~bash
# Run with debug logging
java -jar target/auth-service.jar \
  --logging.level.com.cabinet360.auth=DEBUG \
  --logging.level.org.springframework.security=DEBUG
~~~

## 📈 Performance Optimization

### Database Optimization
~~~sql
-- Add performance indexes
CREATE INDEX CONCURRENTLY idx_users_email_active ON users(email, is_active);
CREATE INDEX CONCURRENTLY idx_audit_log_timestamp_desc ON auth_audit_log(timestamp DESC);

-- Analyze query performance
EXPLAIN ANALYZE SELECT * FROM users WHERE email = 'doctor@hospital.com';
~~~

### Caching Strategy
~~~java
// Cache frequently accessed data
@Cacheable(value = "users", key = "#email")
public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
}

@CacheEvict(value = "users", key = "#user.email")
public User updateUser(User user) {
    return userRepository.save(user);
}
~~~

### Connection Pool Tuning
~~~properties
# HikariCP configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000
~~~

## 🤝 Contributing

### Development Setup
~~~bash
# Clone repository
git clone https://github.com/YOUR_USERNAME/cabinet360-auth-service.git
cd cabinet360-auth-service

# Setup development environment
cp .env.example .env
docker-compose up -d auth-service-db

# Run in development mode
mvn spring-boot:run -Dspring.profiles.active=dev
~~~

### Code Standards
- **Java 17+** with modern features
- **Spring Boot 3.x** best practices
- **Clean Architecture** principles
- **Test-Driven Development** (TDD)
- **Security-first** approach

### Pull Request Checklist
- [ ] Unit tests with >80% coverage
- [ ] Integration tests for new endpoints
- [ ] Security review for authentication changes
- [ ] Performance impact assessment
- [ ] API documentation updates
- [ ] Database migration scripts (if applicable)

## 📋 Changelog

### Version 1.0.0 (Current)
- ✅ JWT-based authentication
- ✅ Role-based access control
- ✅ User registration and approval workflow
- ✅ Password reset functionality
- ✅ Admin management interface
- ✅ Audit logging
- ✅ Email verification

### Version 1.1.0 (Planned)
- 🔄 OAuth2 social login (Google, Microsoft)
- 🔄 Multi-factor authentication (TOTP)
- 🔄 Session management improvements
- 🔄 Advanced password policies
- 🔄 API rate limiting

### Version 2.0.0 (Future)
- 📅 SSO integration (SAML, LDAP)
- 📅 Biometric authentication support
- 📅 Advanced audit reporting
- 📅 Compliance features (HIPAA, GDPR)

## 📄 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) file for details.

## 📞 Support

### Documentation
- **API Reference**: Available at `/swagger-ui.html` when running
- **Postman Collection**: See `postman/` directory
- **Architecture Docs**: See `docs/` directory

### Getting Help
- **Issues**: Report bugs via GitHub Issues
- **Discussions**: Use GitHub Discussions for questions
- **Email**: [nawfalrazouk7@gmail.com](mailto:nawfalrazouk7@gmail.com)

### Security Issues
- **Security vulnerabilities**: Email security@cabinet360.com
- **Response time**: 24-48 hours for critical issues

---

🔐 **Cabinet360 Auth Service** - Secure Foundation for Healthcare Technology
