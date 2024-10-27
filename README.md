# Shopping list

## Getting Started

To get started with the project, follow the steps below:

### 1. Clone the Repository

Open your terminal and run the following command:

````git clone https://github.com/SubriaIs/shopping_list.git````


### 2. Run the Application Using Docker Compose
Ensure you have Docker and Docker Compose installed on your machine. Then, execute the following command to start the application:

````docker-compose up -d ````

# User APIs Documentation

## Overview
The User API provides a comprehensive set of endpoints for managing users, including user login, retrieval, creation, updating, and deletion.

## Token Requirements

### Token Overview
- Certain endpoints in the User API require a valid token for authentication.
- The token must be included in the request header as `xToken`.

### Token Generation
- Users can obtain a token by successfully logging in with their email and password.

### Token Usage
- Include the token in the header for all requests to endpoints marked as requiring a token.

## API Endpoints

| 🔄 **HTTP Method** | 🛣️ **Endpoint**                | 📝 **Description**                                                                                 | 📥 **Request Body**   | 📤 **Response**                                  | ❗ **Error Handling**                                                                                          | 🛠️ **cURL Command** | 🔑 **Token Required** |
|--------------------|--------------------------------|---------------------------------------------------------------------------------------------------|----------------------|------------------------------------------------|--------------------------------------------------------------------------------------------------------------|---------------------|-----------------------|
| **GET**            | `/v1/user`                     | Fetches all users from the database.                                                              | None                 | List of `User` objects in JSON format.          | Returns `404 Not Found` if no users are found. Throws `SLServiceException` with details.                     | `curl -H "xToken: <your-token>" -X GET http://localhost:8082/v1/user` | ✅ Yes                 |
| **GET**            | `/v1/user/id/{id}`             | Fetches a single user by their `id`.                                                              | None                 | `User` object in JSON format.                  | Returns `404 Not Found` if the user with the given `id` is not found. Throws `SLServiceException`.            | `curl -H "xToken: <your-token>" -X GET http://localhost:8082/v1/user/id/{id}` | ✅ Yes                 |
| **GET**            | `/v1/user/name/{name}`         | Fetches a single user by their `name`.                                                            | None                 | `User` object in JSON format.                  | Returns `404 Not Found` if the user with the given `name` is not found. Throws `SLServiceException`.          | `curl -H "xToken: <your-token>" -X GET http://localhost:8082/v1/user/name/{name}` | ✅ Yes                 |
| **POST**           | `/v1/user/login`               | Logs in a user using email and password and returns a token.                                      | `{"email": "...", "password": "..."}` | `TokenResponse` with token in JSON format.      | Returns `404 Not Found` if the email and password do not match any user. Throws `SLServiceException`.         | `curl -X POST -H "Content-Type: application/json" -d '{"email":"test@gmail.com","password":"password"}' http://localhost:8082/v1/user/login` | ❌ No                  |
| **POST**           | `/v1/user`                     | Adds a new user to the system.                                                                    | `User` JSON          | Status `201 Created` on successful creation.    | Returns `400 Bad Request` if a user with the same email already exists. Throws `SLServiceException` for duplicates. | `curl -X POST -H "Content-Type: application/json" -d '{"email":"test@gmail.com","password":"password"}' http://localhost:8082/v1/user` | ❌ No                  |
| **PATCH**          | `/v1/user/id/{id}`             | Updates an existing user by their `id`.                                                           | `User` JSON          | Updated `User` object in JSON format.           | Returns `404 Not Found` if the user with the given `id` is not found. Throws `SLServiceException`.            | `curl -H "xToken: <your-token>" -X PATCH -H "Content-Type: application/json" -d '{"password":"newpassword"}' http://localhost:8082/v1/user/id/{id}` | ✅ Yes                 |
| **DELETE**         | `/v1/user/id/{id}`             | Deletes an existing user by their `id`.                                                           | None                 | Status `204 No Content` on successful deletion. | Returns `404 Not Found` if the user with the given `id` is not found. Throws `SLServiceException`.            | `curl -H "xToken: <your-token>" -X DELETE http://localhost:8082/v1/user/id/{id}` | ✅ Yes                 |

## Notes
- Make sure to replace `<your-token>` with the actual token value for authenticated requests.
- Adjust the `id`, `name`, and user details as necessary when making requests.


### Error Handling

All endpoints return appropriate HTTP status codes and error messages:

- **400 Bad Request**: For invalid input, duplicate data (e.g., when trying to add a user with an existing email).
- **401 Unauthorized**: When an invalid or missing `xToken` is provided for protected endpoints.
- **404 Not Found**: When a user or resource is not found (e.g., when fetching by `id` or `name`).
- **SLServiceException**: Custom exception thrown with error details such as error message, status code, and additional information.
