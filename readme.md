# Software Engineering Backend Project

## Overview
This project implements a multi-tier architecture web application with NestJS frontend, Spring Boot backend, and Aiven SQL database. The system provides file management, user authentication, and payment processing capabilities.

## Architecture

### System Components
- **Frontend**: NestJS application handling user interface and API communication
- **Backend**: Spring Boot application managing business logic and integrations
- **Database**: Aiven SQL for data persistence
- **Third-party Services**: 
  - Firebase for file storage
  - Momo test for payment processing

### Key Features
1. **User Authentication**
   - JWT-based authentication
   - Role-based access control (RBAC)
   - OAuth2 integration

2. **File Management**
   - Secure file upload and storage
   - File metadata management
   - Firebase integration

3. **Payment Processing**
   - Momo test integration
   - Transaction management
   - Payment status tracking

## Technical Stack

### Frontend
- **Framework**: NestJS
- **Authentication**: JWT Tokens
- **API Communication**: REST APIs

### Backend
- **Framework**: Spring Boot
- **Database**: Aiven SQL
- **ORM**: JPA/Hibernate
- **Security**: JWT, CORS

### Database Schema
- **Users Table**: User authentication and profile data
- **Files Table**: File metadata and storage information
- **Transactions Table**: Payment transaction records

## API Documentation

### Authentication
- `GET /custom-oauth-login`: OAuth2 login endpoint
- `GET /custom-oauth-callback`: OAuth2 callback endpoint

### File Management
- `POST /files/`: Upload files
- `GET /files/{file_id}`: Get file metadata
- `DELETE /files/{file_id}`: Delete file

### Payment Processing
- `POST /payments/create`: Create payment request
- `GET /payments/{transaction_id}`: Get transaction details

## Security Measures
- JWT token authentication
- CORS configuration
- Input validation
- Data encryption
- Role-based access control

## Deployment
- Frontend and Backend deployed on Render
- Cloud-based deployment model
- Auto-scaling support

## Architecture Diagrams
- [Multi-tier Architecture](https://drive.google.com/file/d/1RKadWfLchRcWDhUbfpGktcMqoh8UJwuM/view?usp=sharing)
- [Flow Architecture](https://drive.google.com/file/d/1bTRrhEyP4XNDCBBhW1rHKZknvYoVO837/view?usp=sharing)
- [ERD Architecture](https://drive.google.com/file/d/1UwDaG0DRqt9yUBRhU_92QiRoBIjZbkml/view?usp=sharing)
- [Security Architecture](https://drive.google.com/uc?id=1U5DL8Iv3ixVpn4HXAC0yFyncikaokQSN)