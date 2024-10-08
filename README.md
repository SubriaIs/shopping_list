# Shopping list

## Getting Started

To get started with the project, follow the steps below:

### 1. Clone the Repository

Open your terminal and run the following command:

````git clone https://github.com/SubriaIs/shopping_list.git````


### 2. Run the Application Using Docker Compose
Ensure you have Docker and Docker Compose installed on your machine. Then, execute the following command to start the application:

````docker-compose up -d ````

## CategoryAPIs
| 🔄 **HTTP Method** | 🛣️ **Endpoint**               | 📝 **Description**                                                                                 | 📥 **Request Body**  | 📤 **Response**                                     | ❗ **Error Handling**                                                                                          | 🛠️ **cURL Command** |
|-------------------|------------------------------|---------------------------------------------------------------------------------------------------|---------------------|----------------------------------------------------|--------------------------------------------------------------------------------------------------------------|---------------------|
| **GET**           | `/v1/category`               | Fetches all categories from the database.                                                          | None                | List of `Category` objects in JSON format.         | Returns a `404 Not Found` if no categories are found. Throws `SLServiceException` with details.              | `curl -X GET http://localhost:8082/v1/category` |
| **GET**           | `/v1/category/id/{id}`       | Fetches a single category by its `id`.                                                             | None                | `Category` object in JSON format.                  | Returns a `404 Not Found` if the category with the given `id` is not found. Throws `SLServiceException`.      | `curl -X GET http://localhost:8082/v1/category/id/{id}` |
| **GET**           | `/v1/category/name/{name}`   | Fetches a single category by its `name`.                                                           | None                | `Category` object in JSON format.                  | Returns a `404 Not Found` if the category with the given `name` is not found. Throws `SLServiceException`.    | `curl -X GET http://localhost:8082/v1/category/name/{name}` |
| **POST**          | `/v1/category`               | Adds a new category.                                                                               | `Category` JSON     | Status `200 OK` or `201 Created`.                  | Returns `400 Bad Request` if the category name already exists. Throws `SLServiceException` for duplicates.    | `curl -X POST -H "Content-Type: application/json" -d '{"categoryName":"Electronics"}' http://localhost:8082/v1/category` |
| **PATCH**         | `/v1/category/id/{id}`       | Updates an existing category by its `id`.                                                          | `Category` JSON     | Updated `Category` object in JSON format.          | Returns `404 Not Found` if the category with the given `id` is not found. Throws `SLServiceException`.        | `curl -X PATCH -H "Content-Type: application/json" -d '{"categoryName":"NewCategoryName"}' http://localhost:8082/v1/category/id/{id}` |
| **DELETE**        | `/v1/category/id/{id}`       | Deletes a category by its `id`.                                                                    | None                | Status `204 No Content`.                           | Returns `404 Not Found` if the category with the given `id` is not found. Throws `SLServiceException`.        | `curl -X DELETE http://localhost:8082/v1/category/id/{id}` |


## ProductAPIs
| 🔄 **HTTP Method** | 🛣️ **Endpoint**                    | 📝 **Description**                                                                                 | 📥 **Request Body**  | 📤 **Response**                                     | ❗ **Error Handling**                                                                                          | 🛠️ **cURL Command** |
|-------------------|------------------------------------|---------------------------------------------------------------------------------------------------|---------------------|----------------------------------------------------|--------------------------------------------------------------------------------------------------------------|---------------------|
| **GET**           | `/v1/product`                     | Fetches all products from the database.                                                           | None                | List of `Product` objects in JSON format.          | Returns a `404 Not Found` if no products are found. Throws `SLServiceException` with details.                 | `curl -X GET http://localhost:8082/v1/product` |
| **GET**           | `/v1/product/id/{id}`             | Fetches a single product by its `id`.                                                             | None                | `Product` object in JSON format.                   | Returns a `404 Not Found` if the product with the given `id` is not found. Throws `SLServiceException`.       | `curl -X GET http://localhost:8082/v1/product/id/{id}` |
| **GET**           | `/v1/product/category/{categoryName}` | Fetches products by their category name.                                                        | None                | List of `Product` objects in JSON format.          | Returns a `404 Not Found` if no products are found for the given category. Throws `SLServiceException`.       | `curl -X GET http://localhost:8082/v1/product/category/{categoryName}` |
| **GET**           | `/v1/product/name/{name}`         | Fetches a single product by its `name`.                                                           | None                | `Product` object in JSON format.                   | Returns a `404 Not Found` if the product with the given `name` is not found. Throws `SLServiceException`.     | `curl -X GET http://localhost:8082/v1/product/name/{name}` |
| **POST**          | `/v1/product`                     | Adds a new product to the database.                                                               | `Product` JSON      | Status `200 OK` or `201 Created`.                  | Returns `400 Bad Request` if the product name already exists. Throws `SLServiceException` for duplicates.     | `curl -X POST -H "Content-Type: application/json" -d '{"productName":"Laptop", "category":{"categoryId":1}}' http://localhost:8082/v1/product` |
| **PATCH**         | `/v1/product/id/{id}`             | Updates an existing product by its `id`.                                                          | `Product` JSON      | Updated `Product` object in JSON format.           | Returns `404 Not Found` if the product with the given `id` is not found. Throws `SLServiceException`.         | `curl -X PATCH -H "Content-Type: application/json" -d '{"productName":"Updated Laptop", "category":{"categoryId":1}}' http://localhost:8082/v1/product/id/{id}` |
| **DELETE**        | `/v1/product/id/{id}`             | Deletes a product by its `id`.                                                                    | None                | Status `204 No Content`.                           | Returns `404 Not Found` if the product with the given `id` is not found. Throws `SLServiceException`.         | `curl -X DELETE http://localhost:8082/v1/product/id/{id}` |


