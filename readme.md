# ARCHITECTURE

## 1. Architecture Design

The project is built on a **Multi-tier Architecture**, a popular layered architecture in modern web systems. This architecture clearly separates functionalities into distinct layers, including the **Presentation Layer (Frontend)**, **Business Logic Layer (Backend)**, and **Data Storage Layer (Database)**. This approach optimizes scalability, security, and ease of system maintenance.

[Multi-tier Architecture](https://drive.google.com/file/d/1RKadWfLchRcWDhUbfpGktcMqoh8UJwuM/view?usp=sharing)

At the **Presentation Layer**, the system utilizes **NestJS**, a modern and powerful framework, ensuring rapid and efficient user interface development. NestJS acts as the primary communication gateway between users and the system, sending HTTP requests through REST APIs to the Backend Layer. These requests include user authentication, file uploads, and payment processing. All data from Backend responses is displayed intuitively on the user interface.

The **Business Logic Layer** is implemented using **Java Spring Boot**, which handles the core business logic of the system. Spring Boot ensures the execution of complex operations such as user management, file storage on **Firebase**, and integration with the **Momo test** payment gateway. Additionally, this layer acts as an intermediary in communicating with the database, where user information, file metadata, and transaction history are stored. A key highlight of the system is the use of **JWT Tokens** to manage user sessions and ensure secure communication between layers.

Data is stored in the **Data Storage Layer** on **Aiven SQL**, a reliable relational database deployed on a cloud platform. Aiven SQL not only supports efficient data storage but also provides features like automated backups and high-level security. In this layer, data is organized into three main tables: the **users** table for storing user information, and the **files** table for storing file metadata. Data querying and manipulation are performed through **JPA/Hibernate**, minimizing the risk of errors and ensuring data integrity.

The system adopts a **Cloud-based deployment model** to ensure performance and scalability. The Frontend and Backend are deployed on **Render**, a platform that supports auto-scaling, ensuring system stability even during high traffic. Firebase is integrated for file storage, while payment transactions are handled via **Momo test**. These third-party integrations minimize development effort while leveraging the specialized features of these platforms.

Requests from the Frontend to the Backend are secured using **JWT Tokens** to authenticate and authorize users. Furthermore, interactions between the Backend and Firebase, as well as Momo, are conducted through secure APIs. The data flow is designed with a centralized approach, with the Backend serving as the hub for communication between all system components.

This architecture ensures that the system layers are loosely coupled, facilitating easier maintenance and scalability when needed. Moreover, deploying components on the cloud not only optimizes performance but also reduces operational costs.

## 2. Architecture Components

The system is designed with multiple tightly integrated components to ensure efficient and secure business processing. The main components include the **Frontend (NestJS)**, **Backend (Spring Boot)**, **Database (Aiven SQL)**, and third-party services such as **Firebase** and **Momo test**. Each component plays a crucial role and collaborates to form a complete system.

### Frontend

The frontend is built using the **NestJS** framework, acting as the communication gateway between users and the system. NestJS provides a modern interactive interface that allows users to send requests to the backend via REST APIs. The primary functions of the frontend include:

-   Managing user sessions using **JWT Tokens**.
-   Sending requests such as login, file upload, and payment transactions.
-   Displaying responses from the backend, including user information, file lists, and transaction statuses.

NestJS is chosen for its strong compatibility with modern web standards and high performance in handling complex user requests.

### Backend

The system’s backend is implemented using **Java Spring Boot**, responsible for processing core business logic and interacting with other components. It acts as the central hub for orchestrating all system activities. Spring Boot was selected for its flexible scalability, powerful library integration, and large developer community.

Key modules of the backend include:

1.  **Authentication Service**:
    
    -   Handles user authentication through **JWT Tokens**.
    -   Manages role-based access control (**RBAC**).
    -   Secures endpoints by verifying token validity.
2.  **File Management Service**:
    
    -   Integrates with **Firebase** to upload and store files.
    -   Stores file metadata (e.g., path, upload date, uploader) in the database for management and retrieval.
3.  **Payment Service**:
    
    -   Integrates with **Momo test API** to handle payment transactions.
    -   Processes responses from Momo and saves transaction results in the database.
4.  **Database Service**:
    
    -   Interacts with **Aiven SQL** for data storage and querying.
    -   Utilizes **JPA (Hibernate)** to perform CRUD operations efficiently and securely.
5.  **Middleware**:
    
    -   Manages **CORS** to allow requests from the frontend.
    -   Logs requests and responses for monitoring and troubleshooting purposes.

### Database

Data is stored on **Aiven SQL**, a cloud-based relational database. Aiven SQL provides strong security features, supports automated data backups, and offers flexible scalability.

The system organizes data into three main tables:

1.  **users**:
    
    -   Stores user information (**user_id**, email, password hash, role).
    -   Includes fields for session management.
2.  **files**:
    
    -   Stores file metadata (**file_id**, file_url, user_id, upload_date).
    -   Ensures efficient management of files stored on Firebase.

### Third-party Services

The system integrates two critical third-party services:

1.  **Firebase**:
    
    -   A file storage service provided by Google, integrated to store user-uploaded files.
    -   Each file is linked to metadata stored in the database, facilitating easy management and retrieval when needed.
2.  **Momo test**:
    
    -   An online payment gateway integrated to process transactions.
    -   The backend sends payment requests via API and receives transaction results from Momo, which are then saved in the database.

#### **Communication Flow**

The entire system operates through a well-defined communication flow:

1.  The user sends requests from the frontend interface to the backend.
2.  The backend processes the requests, interacts with the database or third-party services (Firebase, Momo).
3.  Results are returned to the frontend for user display.

The data flow is secured using **JWT Tokens** to ensure safety.

[Flow Architecture](https://drive.google.com/file/d/1bTRrhEyP4XNDCBBhW1rHKZknvYoVO837/view?usp=sharing)

## 3. Data Management

In the system, data management plays a core role in ensuring data integrity, security, and efficient access. All data is organized and stored on the **Aiven SQL** platform, a robust relational database deployed in the cloud. Using Aiven SQL enables the system to balance performance and reliability with features such as automated backups and integrated data security.

### Database Architecture

The system organizes data in a relational model with three main tables:

1.  **Users Table**:
    
    -   Stores user information, including:
        -   `user_id`: A unique identifier for each user.
        -   `email`: Email address used for login.
        -   `password_hash`: A hashed password to ensure security.
        -   `role`: The user’s role (e.g., admin, user).
    -   This table is central to authentication and authorization within the system.
2.  **Files Table**:
    
    -   Stores metadata for files uploaded by users:
        -   `file_id`: A unique identifier for each file.
        -   `file_url`: The file’s URL on Firebase.
        -   `user_id`: Links the file to the uploading user.
        -   `upload_date`: The date and time the file was uploaded.
    -   Organizing data in these tables ensures efficient **CRUD** (Create, Read, Update, Delete) operations while minimizing the risk of data errors.

### Data Flow

The data flow in the system is clearly designed to ensure that user requests are processed and stored efficiently:

1.  **User Authentication and Management**:
    
    -   When a user logs in or registers, the backend sends queries to the **users** table to authenticate information or save new data.
    -   A **JWT token** is generated after successful authentication and returned to the frontend.
2.  **File Management**:
    
    -   Users upload files through the interface, and the files are sent to Firebase via the backend.
    -   File metadata (e.g., `file_url`, `user_id`) is saved in the **files** table in the database.
3.  **Transaction Processing**:
    
    -   When a user initiates a payment, the backend sends a request to **Momo test** for processing.
    -   After receiving a response from Momo, transaction details (e.g., `transaction_id`, `status`) are saved in the **transactions** table.

### Data Backup and Recovery

Aiven SQL provides an automatic data backup mechanism, protecting the system from unexpected incidents. Backups are performed periodically and stored securely, enabling quick data recovery in case of failure.

### Data Security

To ensure data safety, the system applies strict security measures:

1.  **Data Encryption**:
    
    -   Sensitive data, such as user passwords, is encrypted using strong hashing algorithms (e.g., **BCrypt**).
2.  **Access Control**:
    
    -   Only authorized components can access the database.
    -   User role-based permissions are implemented to control access to resources.

The data management system is built robustly and efficiently, ensuring data integrity and security. With the support of Aiven SQL and integrated security mechanisms, the system can handle large-scale data processing demands while maintaining high performance and stability. This data organization approach not only meets current requirements but is also easily scalable to accommodate future needs.

[ERD Architecture](https://drive.google.com/file/d/1UwDaG0DRqt9yUBRhU_92QiRoBIjZbkml/view?usp=sharing)

## 4. API Management

In the system, APIs serve as the communication bridge between the Frontend and Backend, as well as the medium for the Backend to interact with third-party services like Firebase and Momo test. The API management is designed based on modern standards to ensure security, performance, and scalability.

###  API Design

The system employs **RESTful APIs** using HTTP methods such as **GET**, **POST**, **PUT**, and **DELETE** to perform CRUD operations. All APIs are designed to return data in **JSON format**, enabling easy processing and display on the Frontend.

Key APIs in the system include:

1.  **Authentication and User Management API**:
    
    -   **GET /custom-oauth-login**: Logs in and redirects to OAuth2 for user authentication.
    -   **GET /custom-oauth-callback**: Returns a **JWT Token** to the user after successful authentication.
2.  **File Management API**:
    
    -   **POST /files/**: Uploads files to Firebase via the Backend.
    -   **GET /files/{file_id}**: Retrieves metadata of a file by its ID.
    -   **DELETE /files/{file_id}**: Deletes a file on Firebase and its corresponding metadata in the database.
3.  **Transaction Processing API**:
    
    -   **POST /payments/create**: Creates a payment request with Momo.
    -   **GET /payments/{transaction_id}**: Retrieves detailed information about a transaction.

#### **API Security**

To ensure secure communication, the system employs strict security measures:

1.  **JWT Token**:
    
    -   All endpoints requiring authentication are protected by **JWT Tokens**.
    -   Tokens contain critical information such as `user_id`, `role`, and expiration time.
    -   Middleware on the Backend validates token authenticity before processing requests.
2.  **CORS**:
    
    -   The system configures **CORS** to only allow requests from the Frontend domain (NestJS).
    -   This prevents Cross-Origin Resource Sharing (CORS) attacks.
3.  **Input Validation**:
    
    -   API parameters are thoroughly validated to prevent vulnerabilities like **SQL Injection** or **Buffer Overflow**.

#### **Documentation**

-   **Postman Collection**:  
    A comprehensive Postman collection containing all system APIs facilitates testing and integration.

----------

#### **API Versioning**

The system supports API versioning to maintain stability while deploying new features:

-   **Versioning**:   APIs are versioned, for example, `/v1/auth/login` and `/v2/auth/login`.  
    This allows the system to support older versions for existing clients without affecting new clients.

#### **Integration with Third-party Services**

APIs also act as intermediaries for connecting to external services:

1.  **Firebase**:
    
    -   The Backend uses the Firebase SDK to upload files.
    -   File metadata (such as URL) is returned to the Frontend via APIs.
2.  **Momo test**:
    
    -   The Backend sends transaction requests to the Momo test API, processes responses, and saves results in the database.

The API management system is designed and implemented with high standards, ensuring not only security but also stable performance. Comprehensive documentation and testing minimize the risk of errors and optimize the experience for both the development team and end users.

[Security Architecture](https://drive.google.com/uc?id=1U5DL8Iv3ixVpn4HXAC0yFyncikaokQSN)