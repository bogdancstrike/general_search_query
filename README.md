# Dynamic Database Search API

A Spring Boot application that dynamically searches across specified database tables and columns using flexible query parameters. This project supports wildcard searches across all columns as well as specific column-based filtering. It is designed to work with relational databases such as MySQL and PostgreSQL.

## Features

- **Dynamic Table Querying:** Query data from multiple tables dynamically based on user input.
- **Wildcard Searches:** Perform searches across all columns using wildcard matching.
- **Flexible Column Filtering:** Use specific column filters to retrieve precise results.
- **Supports Multiple Databases:** Compatible with MySQL and PostgreSQL databases.
- **Scalable Architecture:** Business logic separated into service layer (`DynamicSearchService`).
- **Pagination:** Supports pagination to manage large datasets efficiently.


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
   git clone https://github.com/bogdancstrike/general_search_query
   cd general_search_query
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

URL:
```http://localhost:8080/search```

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

URL:
```http://localhost:8080/search?page=0&size=3```

- Example 1:

```code
{
    "tables": ["departments"],
    "filters": [
        {
            "created_date": "2020-01-01"
        }
    ]
}
```

- Example 2:

```code
{
    "tables": ["employees"],
    "filters": [
        {
            "is_permanent": true
        }
    ]
}
```


- Example 3:

```code
{
    "tables": ["employees"],
    "filters": [
        {
            "ALL_COLUMNS": "John"
        },
        {
            "is_permanent": true
        }
    ]
}
```


- Example 4:

```code
{
    "tables": ["employees"],
    "filters": [
        {
            "hire_date": "2020-01-01"
        },
        {
            "employee_name": "Employee"
        },
        {
            "is_permanent": true
        },
        {
            "department_id": 1
        }
    ]
}
```


## Code Overview

### **DynamicSearchController**
- Handles the `/search` endpoint.
- Delegates the search logic to the `DynamicSearchService`.
- Uses `@RestController` to expose the endpoint for handling HTTP POST requests.
- Supports pagination using `page` and `size` query parameters.
- Combines the results from multiple tables and applies pagination in-memory on the combined result set.

### **DynamicSearchService**
- Contains the `executeDynamicQuery` method, which dynamically constructs SQL queries based on the provided filters and table names.
- Uses `JdbcTemplate` to interact with the database directly.
- Retrieves the metadata of each table to identify column names and their data types.
- Builds a dynamic SQL query to support:
    - Wildcard searches across all columns using the `"ALL_COLUMNS"` filter.
    - Specific column-based filtering with appropriate operators (`LIKE` for string columns, `=` for numeric columns, etc.).
- Handles type conversion for columns to prevent SQL errors (e.g., casting strings to numbers for numeric columns).
- Supports various data types (`STRING`, `INTEGER`, `DATE`, `BOOLEAN`) in filters.

### **SearchRequest Model**
- Represents the structure of the incoming JSON request.
- Contains:
    - `tables`: A list of table names to query.
    - `filters`: A list of conditions to apply on the columns, supporting both specific column filters and wildcard searches across all columns.