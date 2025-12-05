-- ===== V1__init.sql =====
-- Creates core LMS tables (correct schema for your Java code)

-- Drop old tables if they exist
DROP TABLE IF EXISTS registration;
DROP TABLE IF EXISTS course_offering;

-- COURSE OFFERING TABLE
CREATE TABLE course_offering (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    instructor  VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    min_employees INT NOT NULL,
    max_employees INT NOT NULL,
    allotted BOOLEAN DEFAULT FALSE
);

-- Ensure each instructor teaches each course only once
CREATE UNIQUE INDEX idx_course_instructor
    ON course_offering (course_name, instructor);


-- REGISTRATION TABLE
CREATE TABLE registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    offering_id VARCHAR(100) NOT NULL,   -- this is OK
    status VARCHAR(50) NOT NULL,
    cancelled BOOLEAN DEFAULT FALSE
);
