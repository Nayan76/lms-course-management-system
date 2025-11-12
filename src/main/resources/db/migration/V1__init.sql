-- ===== V1__init.sql =====
-- Creates core LMS tables

CREATE TABLE course_offering (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    instructor  VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    min_employees INT NOT NULL,
    max_employees INT NOT NULL,
    allotted BOOLEAN DEFAULT FALSE,
    offering_id VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    offering_id VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    cancelled BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (offering_id) REFERENCES course_offering(offering_id)
);
