### README for Vergov Application (GitHub Version)

# Vergov Application

Vergov is a comprehensive platform for managing users, roles, and content. This repository contains the server-side of the application, which is built using Spring Boot. It handles backend logic, user authentication, role-based access control, and content management (articles and papers). The front-end, developed in React, is hosted in a separate repository. You can find the front-end code here: [TheVergovAppReact](https://github.com/miroslavvergov/TheVergovAppReact).

## Table of Contents

- [Technologies Used](#technologies-used)
- [Setup Instructions](#setup-instructions)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [Usage](#usage)
- [License](#license)

## Technologies Used

- **Java 21**
- **Spring Boot 3.3.0**
- **Spring Data JPA**
- **PostgreSQL**
- **Docker**
- **PgAdmin**
- **ModelMapper 3.1.1**
- **Lombok 1.18.32**
- **Apache Commons Lang 3.14.0**
- **Commons IO 2.15.1**
- **Guava 33.0.0-jre**
- **JJWT 0.12.3**
- **Spring Boot Starter Mail**
- **Testcontainers**
- **AssertJ**
- **Maven Shade Plugin 3.5.0**
- **DevSamStevens TOTP 1.7.1**

## Setup Instructions

### Prerequisites

- **Java 21** or higher
- **Docker** and **Docker Compose**
- **Maven**

### Clone the Repository

```bash
git clone https://github.com/yourusername/vergov.git
cd vergov
```

### Configure the Application

1. **Database Setup with Docker**:
   - Navigate to the `docker` directory:

     ```bash
     cd docker
     ```

   - Start the PostgreSQL and PgAdmin containers:

     ```bash
     docker-compose up -d
     ```

2. **Configuration Files**:
   - Ensure your `application.yml` and `application-dev.yml` are configured correctly.
   - Update the `.env` file in the `docker` directory if necessary.

3. **Build the Project**:

   ```bash
   mvn clean install
   ```

4. **Run the Application**:

   ```bash
   mvn spring-boot:run
   ```

## Configuration

### Application Configuration (`application.yml`)

Contains settings for:

- **Database**
- **Server**
- **Email**
- **JWT**
- **Logging** (optional)

### Environment-Specific Configuration (`application-dev.yml`)

Contains environment-specific settings like:

- **Database Credentials**
- **Server Port**
- **Email Settings**
- **JWT Settings**

### Docker Configuration (`docker-compose.yml`)

This file configures:

- **PostgreSQL**
- **PgAdmin**

### Environment Variables (`.env`)

Contains sensitive data such as:

- **PostgreSQL** credentials
- **PgAdmin** credentials

## Database Schema

### Key Tables

- **`users`**: Manages user profiles.
- **`confirmations`**: Stores confirmation tokens.
- **`credentials`**: Contains user passwords.
- **`papers`**: Stores documents for different purposes.
- **`roles`**: Defines roles and permissions.
- **`user_roles`**: Manages user-role associations.

### Relationships

- **Self-Referential**: `users` table tracks record creation/updates.
- **Foreign Key Constraints**: Ensures referential integrity.

## Usage

After setup:

- **Access the Application**: `http://localhost:8080` (default port).
- **Explore API Endpoints**: Swagger UI (if enabled) at `http://localhost:8080/authentication-docs/swagger-ui-custom.html`.
- **Manage the Database**: PgAdmin at `http://localhost:7000`.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
