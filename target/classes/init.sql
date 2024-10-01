-- Create the departments table
CREATE TABLE IF NOT EXISTS departments (
                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           department_name VARCHAR(255) NOT NULL,
    department_employees_number INT NOT NULL
    );

-- Create the employees table
CREATE TABLE IF NOT EXISTS employees (
                                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                         employee_name VARCHAR(255) NOT NULL,
    department_id BIGINT,
    FOREIGN KEY (department_id) REFERENCES departments(id)
    );

-- Insert mock data into departments table
INSERT INTO departments (department_name, department_employees_number) VALUES
                                                                           ('Human Resources', 5),
                                                                           ('Engineering', 20),
                                                                           ('Sales', 10),
                                                                           ('Marketing', 7);

-- Insert mock data into employees table
INSERT INTO employees (employee_name, department_id) VALUES
                                                         ('Anna Smith', 1),
                                                         ('John Doe', 2),
                                                         ('Maria Johnson', 3),
                                                         ('James Williams', 2),
                                                         ('Patricia Brown', 4),
                                                         ('Michael Garcia', 2),
                                                         ('Linda Martinez', 1);
