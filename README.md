Here is a comprehensive README file for your project, including a description of the application, setup instructions, technologies used, and how to contribute:

---

# Vergov

Vergov is a robust application designed for managing user accounts, authentication, and document storage. This application supports user registration, role management, multi-factor authentication (MFA), and document handling. It includes features for user profile management, password recovery, and secure file storage.

## Features

- **User Management**: Create, update, and manage user accounts.
- **Role Management**: Assign and update user roles.
- **Multi-Factor Authentication (MFA)**: Secure accounts with MFA using QR codes.
- **Password Management**: Reset and update passwords securely.
- **Document Storage**: Upload, update, and retrieve documents with associated metadata.
- **Profile Management**: Upload user photos and update profile information.

## Technologies Used

- **Java**: Primary programming language.
- **Spring Boot**: Framework for building and deploying the application.
- **Spring Security**: Provides authentication and authorization capabilities.
- **Spring Data JPA**: Handles database operations and object-relational mapping.
- **Jakarta EE**: Provides enterprise features such as transactions.
- **Lombok**: Reduces boilerplate code with annotations.
- **ModelMapper**: Simplifies mapping between objects.
- **Apache Commons**: Utilities for file and string operations.
- **dev.samstevens.totp**: Library for generating and verifying TOTP codes for MFA.
- **H2 Database**: In-memory database for development and testing.
- **MySQL**: Relational database for production.
- **Maven**: Build automation tool.
- **JUnit**: Framework for writing and running tests.

## Getting Started

### Prerequisites

- Java 17 or later
- Maven 3.8 or later
- PostgreSQL/MySQL (for production) or H2 Database (for development)

### Clone the Repository

```bash
git clone https://github.com/your-username/your-repository.git
cd your-repository
```

### Build the Project

```bash
mvn clean install
```

### Configure Application Properties

Update the `src/main/resources/application.properties` file with your database and application configurations. Example configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vergov
spring.datasource.username=your-username
spring.datasource.password=your-password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

file.storage.path=/path/to/your/file/storage
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start and be accessible at `http://localhost:8080`.

## API Endpoints

- **User Management**:
  - `POST /users`: Create a new user
  - `GET /users/{userId}`: Get user details by ID
  - `PUT /users/{userId}`: Update user details
  - `DELETE /users/{userId}`: Delete a user

- **Role Management**:
  - `GET /roles/{name}`: Get role details by name
  - `PUT /users/{userId}/role`: Update user role

- **Document Management**:
  - `POST /documents`: Upload a new document
  - `GET /documents/{filename}`: Get document by filename
  - `PUT /documents/{filename}`: Update document metadata
  - `DELETE /documents/{filename}`: Delete a document

- **Authentication**:
  - `POST /auth/login`: Login with email and password
  - `POST /auth/mfa/setup`: Setup MFA for a user
  - `POST /auth/mfa/verify`: Verify MFA code

## Contributing

We welcome contributions to improve the Vergov application. To contribute:

1. Fork the repository.
2. Create a new branch for your feature or fix.
3. Commit your changes and push them to your fork.
4. Open a pull request with a clear description of your changes.

Please ensure that your code adheres to the existing style and includes relevant tests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For any questions or support, please open an issue on GitHub or contact me on this address - vergovmiroslav@gmail.com).

---

This README provides a detailed overview of the Vergov back-end application, setup instructions, and information on how to contribute. Customize the placeholders (such as repository URL, email, and file paths) to fit your project specifics.
