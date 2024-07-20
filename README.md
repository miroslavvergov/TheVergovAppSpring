

# Vergov Application

Vergov is a robust platform for managing users, roles, and content. It provides user authentication, role-based access, and content management through articles and papers.

## Table of Contents

- [Technologies Used](#technologies-used)
- [Setup Instructions](#setup-instructions)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [Usage](#usage)
- [License](#license)

## Technologies Used

- **Java 18**: Core programming language for backend development.
- **Spring Boot 3.3.0**: Framework for building and running the application with embedded Tomcat server and various utilities.
- **Spring Data JPA**: Simplifies database operations and entity management.
- **MySQL 8.3.0**: Relational database management system used for storing application data.
- **ModelMapper 3.1.1**: Maps between objects to facilitate data transfer between layers.
- **Lombok 1.18.32**: Reduces boilerplate code with annotations for getters, setters, and builders.
- **Apache Commons Lang 3.14.0**: Provides utilities for common tasks such as string manipulation.
- **Commons IO 2.15.1**: Offers utilities for IO operations.
- **Guava 33.0.0-jre**: Google's core libraries for Java, offering utilities for collections, caching, and more.
- **JJWT 0.12.3**: JSON Web Token (JWT) library for authentication and authorization.
- **Spring Boot Starter Mail**: For sending emails.
- **Testcontainers**: Provides containers for testing purposes, including MySQL.
- **AssertJ**: Provides fluent assertions for testing.
- **Maven Shade Plugin 3.5.0**: For creating executable JARs with dependencies.
- **DevSamStevens TOTP 1.7.1**: For Two-Factor Authentication (2FA) support.

## Setup Instructions

### Prerequisites

- **Java 18** or higher
- **MySQL 8.0** or higher
- **Maven** (for building the project)

### Clone the Repository

```bash
git clone https://github.com/yourusername/vergov.git
cd vergov
```

### Configure the Application

1. **Database Setup**: Ensure MySQL is running and create the database:

   ```sql
   CREATE DATABASE thevergov_db;
   ```

2. **Configuration Files**:

   - Copy `application-dev.yml` to `application.yml` if you want to use development settings.
   - Update `application.yml` with production settings or specific environment configurations.

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

The `application.yml` file includes configurations for:

- **Database**: Connection details, JPA settings, and schema initialization.
- **Server**: Port and error handling.
- **Email**: SMTP settings for sending emails.
- **JWT**: JSON Web Token settings including expiration and secret.
- **Logging**: Optional logging settings (commented out).

### Environment-Specific Configuration (`application-dev.yml`)

This file contains environment-specific settings:

- **Database Credentials**: Username, password, and database details.
- **Server Port**: Application container port.
- **Email Settings**: SMTP host, port, and email credentials.
- **JWT Settings**: Expiration and secret for JWT tokens.

## Database Schema

### Key Tables

1. **`users`**:
   - Manages user profiles with attributes such as user ID, email, name, and account status.
   - Links to itself for record creation and updates.

2. **`confirmations`**:
   - Stores confirmation tokens for user actions, such as email verification.
   - Linked to `users` for associated user details.

3. **`credentials`**:
   - Contains user passwords and security information.
   - Linked to `users` to manage user credentials securely.

4. **`articles`**:
   - Handles content articles published in the application.
   - Links to `users` for tracking authorship and updates.

5. **`papers`**:
   - Stores documents similar to articles but used for different purposes.
   - Linked to `users` for creator and updater details.

6. **`roles`**:
   - Defines roles and permissions within the application.
   - Linked to `users` through `user_roles` to manage role assignments.

7. **`user_roles`**:
   - Manages associations between users and roles.
   - Links `users` to `roles` to define role assignments and permissions.

### Relationships

- **Self-Referential**: The `users` table tracks who created or updated each record.
- **Foreign Key Constraints**: Ensures referential integrity between related tables.

## Usage

After setting up and running the application, you can:

- **Access the Application**: Open `http://localhost:8080` (or the configured port).
- **Explore API Endpoints**: If Swagger is enabled, visit `http://localhost:8080/authentication-docs/swagger-ui-custom.html`.
- **Manage Users and Content**: Use the application’s web interfaces or APIs for user and content management.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

Feel free to tailor any sections further based on your specific needs or project details!
