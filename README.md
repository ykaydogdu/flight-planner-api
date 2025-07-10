
https://flight-planner-frontend-323247003818.us-central1.run.app/

# Flight Planner API ✈️

A robust RESTful API implemented with Spring Boot for managing flights, bookings, airlines, airports, and user authentication within a comprehensive flight planning system. This application allows users to search for flights, make bookings, and provides administrative functionalities for managing airlines, airports, and flight schedules. (This is a project done at Turkcell as an internship project)

The backend is deployed to the URL https://flight-planner-backend-323247003818.us-central1.run.app/. But it only accepts requests from the frontend which can be found [here](https://github.com/ykaydogdu/flight-planner-frontend).

## Table of Contents

-   [Features](#features)
-   [Technologies Used](#technologies-used)
-   [Running the project](#running-the-project)
-   [Prerequisites](#prerequisites)
-   [Getting Started](#getting-started)
    -   [Clone the Repository](#1-clone-the-repository)
    -   [Database Setup](#2-database-setup)
    -   [Configuration](#3-configuration)
    -   [Build the Project](#4-build-the-project)
    -   [Run the Application](#5-run-the-application)
-   [API Documentation](#api-documentation)
-   [Authentication & Authorization](#authentication--authorization)
    -   [User Roles](#user-roles)
    -   [How to Obtain and Use a Token](#how-to-obtain-and-use-a-token)
    -   [Demo Credentials](#demo-credentials)
-   [Key Endpoints & Usage Examples](#key-endpoints--usage-examples)
-   [Configuration Properties](#configuration-properties)
-   [Error Handling](#error-handling)
-   [Project Structure](#project-structure)
-   [Contributing](#contributing)

---

## Features

*   **Flight Management:**
    *   Create, retrieve, update, and delete flights.
    *   Search flights by various criteria (airline, origin, destination, date, passenger capacity, include past flights).
    *   Get flight statistics (revenue, booking count) for specific airlines and date ranges.
    *   There must be daily at most 3 flights for an airline between 2 destinations.
*   **Booking System:**
    *   Book flights for multiple passengers.
    *   Retrieve individual bookings by ID.
    *   Retrieve all bookings made by the authenticated user.
    *   Retrieve bookings by flight ID (for staff/admin).
    *   Delete bookings.
*   **User Management:**
    *   User registration and login.
    *   JWT-based authentication and authorization.
    *   Retrieve details of the currently authenticated user.
    *   Assign roles (Admin, User, Airline Staff) to users (Admin only).
    *   Assign airlines to airline staff users (Admin only).
    *   Retrieve all users or specific user by username.
*   **Airport Management:**
    *   Create, retrieve, and delete airports (Admin only for creation/deletion).
    *   Get all airports.
*   **Airline Management:**
    *   Add, retrieve, and delete airlines (Admin only for creation/deletion).
    *   Get all airlines, including staff count.

---

## Technologies Used

*   **Java 17+**
*   **Spring Boot:** Framework for building robust, stand-alone, production-grade Spring applications.
*   **Spring Security:** Provides comprehensive security services for Java EE-based enterprise software applications.
*   **Spring Data JPA:** Simplifies data access layer with JPA and Hibernate.
*   **JWT (JSON Web Tokens):** For secure authentication and authorization.
*   **Maven:** Dependency management and build automation tool.
*   **MySQL:** Relational database for data persistence. (H2 is used for testing)
*   **Lombok:** Reduces boilerplate code for Java beans.
*   **Swagger/OpenAPI 3:** For interactive API documentation and testing.

---

## Running the project

You can directly run the project locally using docker compose.

```bash
git clone https://github.com/ykaydogdu/flight-planner-api
cd flight-planner-api
docker compose up -d
```

**Warning!** You need to create a .env file and initialize JWT_TOKEN in it.

## Prerequisites

Before you begin, ensure you have met the following requirements:

*   **Java Development Kit (JDK) 17 or higher** installed.
*   **Apache Maven 3.6.x or higher** installed.
*   **A running MySQL database instance**

---

## Getting Started

Follow these steps to get your development environment set up and run the Flight Planner API.

### 1. Clone the Repository

```bash
git clone https://github.com/ykaydogdu/flight-planner-api
cd flight-planner-api
```

2. Database Setup

Create a new MySQL database. For example:

```sql
CREATE DATABASE <your_db_name>;
```

The application will automatically create the necessary tables on startup.

3. Configuration

Configure your database connection and JWT secret in the src/main/resources/application.properties file.

```properties
# Server Port
server.port=8080

# Database Configuration (PostgreSQL Example)
spring.datasource.url=jdbc:mysql://localhost:3306/<your_db_name>
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update # Use 'create' to drop and recreate tables on each startup (dev only!)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=<YOUR_VERY_LONG_AND_SECURE_JWT_SECRET_KEY> # REPLACE THIS with a strong random string!
jwt.expiration=<expiration_time_in_milliseconds> # e.g., 86400000 for 24 hours
```

Important: Replace <YOUR_VERY_LONG_AND_SECURE_JWT_SECRET_KEY> with a long, random string. You can generate one online or use a tool.

4. Build the Project

Navigate to the project root directory and build the application using Maven:

```bash
mvn clean install
```

5. Run the Application

You can run the Spring Boot application from the command line:

```bash
mvn spring-boot:run
```

The API will start on http://localhost:8080 (or your configured port).

## API Documentation

Once the application is running, you can access the interactive API documentation using Swagger UI:
- Swagger UI: http://localhost:8080/swagger-ui/index.html#
- OpenAPI JSON: http://localhost:8080/v3/api-docs
This documentation provides a detailed overview of all available endpoints, request/response schemas, and allows you to test the API directly from your browser.

## Authentication & Authorization
The Flight Planner API uses JWT (JSON Web Tokens) for authenticating requests and managing user authorization.
### User Roles
The system defines three primary roles, each with specific permissions:
- ROLE_USER:
    - Search for flights.
    - Book flights.
    - View their own bookings (/api/v1/bookings/my-bookings).
    - Access personal user details (/api/v1/auth/me).
- ROLE_AIRLINE_STAFF:
    - Inherits all ROLE_USER permissions.
    - Can create, update, and delete flights for their assigned airline.
    - View bookings for specific flights (/api/v1/bookings?flightId=...).
    - Access flight statistics for their assigned airline.
- ROLE_ADMIN:
    - Has full access to all endpoints.
    - Can create, update, and delete flights for any airline.
    - Manage airports (create, get, delete).
    - Manage airlines (add, get, delete).
    - Manage users (get all, get by username, assign roles, assign airlines).
    - Access flight statistics for any airline.

### How to Obtain and Use a Token

Register a User (Optional, if you don't have one):

```bash
curl -X POST "http://localhost:8080/api/v1/auth/register" \
     -H "Content-Type: application/json" \
     -d '{
           "username": "testuser",
           "password": "password123",
           "firstName": "John",
           "lastName": "Doe",
           "email": "john.doe@example.com"
         }'
```

Login to obtain a JWT Token:

```bash
curl -X POST "http://localhost:8080/api/v1/auth/login" \
     -H "Content-Type: application/json" \
     -d '{
           "username": "testuser",
           "password": "password123"
         }'
```

The response will contain a token field (e.g., eyJhbGciOiJIUzI1Ni...).

Include the Token in Subsequent Requests:

For any protected endpoint, include the JWT token in the Authorization header with the Bearer prefix:

```bash
curl -X GET "http://localhost:8080/api/v1/auth/protected" \
     -H "Authorization: Bearer <YOUR_JWT_TOKEN_HERE>"
```

## Key Endpoints & Usage Examples

Here are some core API endpoints to get you started. For a full list and detailed schemas, refer to the API Documentation.
1. Authentication & User Management
- Register User: POST /api/v1/auth/register
- Login User: POST /api/v1/auth/login
- Get Current User Details: GET /api/v1/auth/me (Requires JWT)
- Assign Role to User: PATCH /api/v1/users/{username}/assign-role?role={roleName} (Requires Admin JWT)
- Assign Airline to User: PATCH /api/v1/users/{username}/assign-airline?airlineCode={code} (Requires Admin JWT)

2. Flight Operations
- Get All Flights (with filters): GET /api/v1/flights
```bash
# Example: Get flights from New York (JFK) to Los Angeles (LAX) on 2023-10-26
curl -X GET "http://localhost:8080/api/v1/flights?originAirportCode=JFK&destinationAirportCode=LAX&departureDate=2023-10-26"
```
- Create a New Flight: POST /api/v1/flights (Requires Airline Staff/Admin JWT)
- Get Flight by ID: GET /api/v1/flights/{id}
Update Flight by ID: PUT /api/v1/flights/{id} (Requires Airline Staff/Admin JWT)
- Delete Flight by ID: DELETE /api/v1/flights/{id} (Requires Airline Staff/Admin JWT)
- Get Flight Statistics: GET /api/v1/flights/stats?airlineCode={code} (Requires Airline Staff/Admin JWT)

3. Booking Operations

- Book a Flight: POST /api/v1/bookings/create (Requires User/Staff/Admin JWT)

```bash
curl -X POST "http://localhost:8080/api/v1/bookings/create" \
     -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{
           "flightId": 1,
           "passengers": [
             {
               "firstName": "Jane",
               "lastName": "Doe",
               "email": "jane.doe@example.com",
               "flightClass": "ECONOMY",
               "priceAtBooking": 150.00
             }
           ],
           "numberOfSeats": 1
         }'
```
- Get My Bookings: GET /api/v1/bookings/my-bookings (Requires User/Staff/Admin JWT)
- Get Booking by ID: GET /api/v1/bookings/{id}
- Delete Booking by ID: DELETE /api/v1/bookings/{id}

4. Airport & Airline Management

- Get All Airports: GET /api/v1/airports
- Create New Airport: POST /api/v1/airports (Requires Admin JWT)
- Delete Airport by Code: DELETE /api/v1/airports/{code} (Requires Admin JWT)
- Get All Airlines: GET /api/v1/airlines
- Add New Airline: POST /api/v1/airlines (Requires Admin JWT)
- Delete Airline by Code: DELETE /api/v1/airlines/{code} (Requires Admin JWT)

## Configuration Properties

The following properties can be configured in src/main/resources/application.properties:

- server.port: The port on which the API will run (default: 8080).
- spring.datasource.url: JDBC URL for the database connection.
- spring.datasource.username: Database username.
- spring.datasource.password: Database password.
- jwt.secret: Crucial - A strong secret key for JWT token generation and validation.
- jwt.expiration: The token expiration time in milliseconds.

## Error Handling

The API returns standard HTTP status codes and provides a descriptive error message in the response body for various scenarios:

- 2xx Success: Request was successfully received, understood, and accepted.
- 400 Bad Request: Invalid input data (e.g., malformed JSON, missing required fields).
- 401 Unauthorized: Authentication is required or has failed (e.g., missing or invalid JWT token).
- 403 Forbidden: The authenticated user does not have the necessary permissions to access the resource.
- 404 Not Found: The requested resource could not be found.
- 409 Conflict: The request could not be completed due to a conflict with the current state of the resource (e.g., username already exists, flight conflict, insufficient seats).
- 500 Internal Server Error: An unexpected error occurred on the server.

## Contributing

Contributions are welcome! If you find a bug or have a feature request, please open an issue. If you'd like to contribute code, please fork the repository and submit a pull request.