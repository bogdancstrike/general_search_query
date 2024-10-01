-- Insert mock data into departments table
INSERT INTO departments (department_name, department_employees_number, created_date, is_active)
VALUES ('Human Resources', 5, '2020-01-01', TRUE),
       ('Engineering', 20, '2019-06-15', TRUE),
       ('Sales', 10, '2018-03-10', TRUE),
       ('Marketing', 7, '2021-09-05', FALSE);

-- Insert mock data into employees table
INSERT INTO employees (employee_name, department_id, hire_date, is_permanent)
VALUES ('Anna Smith', 1, '2020-02-01', TRUE),
       ('John Doe', 2, '2019-06-20', TRUE),
       ('Maria Johnson', 3, '2018-05-15', FALSE),
       ('James Williams', 2, '2019-08-10', TRUE),
       ('Patricia Brown', 4, '2021-10-01', FALSE),
       ('Michael Garcia', 2, '2020-01-25', TRUE),
       ('Linda Martinez', 1, '2020-03-12', TRUE);

-- Generate more mock data for employees
-- Assume we generate data with varying dates and boolean values
INSERT INTO employees (employee_name, department_id, hire_date, is_permanent)
VALUES ('Employee 1', 1, '2020-01-01', TRUE),
       ('Employee 2', 2, '2019-05-15', FALSE),
       ('Employee 3', 3, '2018-07-20', TRUE),
       ('Employee 4', 4, '2021-03-01', FALSE),
       ('Employee 5', 1, '2020-12-12', TRUE),
       ('Employee 6', 2, '2020-06-25', TRUE),
       ('Employee 7', 3, '2019-09-10', FALSE),
       ('Employee 8', 4, '2021-01-30', TRUE),
       ('Employee 9', 1, '2020-11-05', TRUE),
       ('Employee 10', 2, '2020-02-28', TRUE),
       ('Employee 11', 3, '2018-03-15', FALSE),
       ('Employee 12', 4, '2019-08-23', TRUE),
       ('Employee 13', 1, '2020-10-10', TRUE),
       ('Employee 14', 2, '2020-07-07', FALSE),
       ('Employee 15', 3, '2018-04-20', TRUE),
       ('Employee 16', 4, '2019-12-15', TRUE),
       ('Employee 17', 1, '2020-05-10', FALSE),
       ('Employee 18', 2, '2020-01-20', TRUE),
       ('Employee 19', 3, '2018-09-05', TRUE),
       ('Employee 20', 4, '2019-04-10', FALSE),
       ('Employee 21', 1, '2020-08-25', TRUE),
       ('Employee 22', 2, '2020-03-11', FALSE),
       ('Employee 23', 3, '2018-07-13', TRUE),
       ('Employee 24', 4, '2019-05-05', TRUE),
       ('Employee 25', 1, '2020-09-15', FALSE),
       ('Employee 26', 2, '2020-02-25', TRUE),
       ('Employee 27', 3, '2018-11-10', TRUE),
       ('Employee 28', 4, '2019-10-15', FALSE),
       ('Employee 29', 1, '2020-06-30', TRUE),
       ('Employee 30', 2, '2019-12-20', FALSE),
-- Add more employees as needed
-- Repeat the pattern to generate at least 100 employees
       ('Employee 100', 3, '2021-12-31', TRUE);
