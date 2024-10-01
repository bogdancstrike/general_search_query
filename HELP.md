# Dynamic Database Search API

A Spring Boot application that dynamically searches across specified database tables and columns using flexible query parameters. This project supports wildcard searches across all columns as well as specific column-based filtering. It is designed to work with relational databases such as MySQL and PostgreSQL.

## Features

- **Dynamic Table Querying:** Query data from multiple tables dynamically based on user input.
- **Wildcard Searches:** Perform searches across all columns using wildcard matching.
- **Flexible Column Filtering:** Use specific column filters to retrieve precise results.
- **Supports Multiple Databases:** Compatible with MySQL and PostgreSQL databases.
- **Scalable Architecture:** Business logic separated into service layer (`DynamicSearchService`).

## Project Structure

- `api/`: Contains the `DynamicSearchController` class for handling API requests.
- `model/`: Contains the `SearchRequest` model that maps the request body JSON.
- `service/`: Contains the `DynamicSearchService` class, which executes the dynamic SQL queries.

## Requirements

- Java 17 or higher
- Maven
- MySQL or PostgreSQL database
- Spring Boot 3.x

## Setup Instructions

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/your-username/dynamic-database-search-api.git
   cd dynamic-database-search-api
    ```

2. **Configure the Database**

Open the `src/main/resources/application.properties` file and update the database connection details as follows:

   ```properties
    # Show SQL statements in the console (useful for debugging)
    spring.jpa.show-sql=true
      
    # Format SQL statements (makes them more readable in the console)
    spring.jpa.properties.hibernate.format_sql=true
   
    # ----------------------------------------
    # PostgreSQL Configuration
    # ----------------------------------------
   
    spring.datasource.url=jdbc:postgresql://192.168.1.140:5432/hera_data
    spring.datasource.username=postgres
    spring.datasource.password=postgres
    spring.datasource.driver-class-name=org.postgresql.Driver
   ```

3. **Usage**

Swagger URL:

```url
http://localhost:8080/swagger-ui/index.html
```

- Example 1:

```code
{
  "tables": [
    "employees"
  ],
  "filters": [
    {
      "employee_name": "Anna"
    }
  ]
}
```

- Example 2:

```code
{
    "tables": ["departments", "employees"],
    "filters": [
        {
            "ALL_COLUMNS": "Anna"
        }
    ]
}
```

- Example 3:

```code
{
    "tables": ["departments", "employees"],
    "filters": [
        {
            "id": 1
        }
    ]
}
```

- Example 4:

```code
{
    "tables": ["departments", "employees"],
    "filters": [
        {
            "ALL_COLUMNS": "Anna"
        },
        {
            "id": 1
        }
    ]
}
```

## Code Overview

### **DynamicSearchController**
- Handles the `/search` endpoint.
- Delegates the search logic to the `DynamicSearchService`.
- Uses `@RestController` to expose the endpoint for handling HTTP POST requests.

### **DynamicSearchService**
- Contains the `executeDynamicQuery` method, which dynamically constructs SQL queries based on the provided filters and table names.
- Uses `JdbcTemplate` to interact with the database directly.
- Retrieves the metadata of each table to identify column names and their data types.
- Builds a dynamic SQL query to support:
   - Wildcard searches across all columns using the `"ALL_COLUMNS"` filter.
   - Specific column-based filtering with appropriate operators (`LIKE` for string columns, `=` for numeric columns).
- Handles type conversion for columns to prevent SQL errors (e.g., casting strings to numbers for numeric columns).

### **SearchRequest Model**
- Represents the structure of the incoming JSON request.
- Contains:
   - `tables`: A list of table names to query.
   - `filters`: A list of conditions to apply on the columns, supporting both specific column filters and wildcard searches across all columns.

## Troubleshooting

- **SQL Errors:** Ensure that the table names and column names provided in the filters match those in the database schema. Also, ensure that the data type in filters (e.g., IDs) matches the column's data type in the database.
- **Database Connection Issues:** Double-check the database connection details in the `application.properties` file to ensure they point to the correct database.
- **Data Type Compatibility:** When using numeric filters, make sure the values are passed as numbers (e.g., `1` instead of `"1"`). The service automatically attempts to convert values based on column types.

## To Do

- Add support for other data types (e.g., `DATE`, `BOOLEAN`) to handle more complex queries.
- Implement pagination to manage large datasets efficiently.
- Add unit and integration tests to enhance code reliability and ensure correct functionality.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Contributing

Contributions are welcome! Please fork this repository and create a pull request for any feature additions, bug fixes, or improvements.

---