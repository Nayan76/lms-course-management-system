-- ===== V2__add_offering_id_to_course_offering.sql =====
-- Add the missing offering_id column used by the application logic

ALTER TABLE course_offering
ADD COLUMN offering_id VARCHAR(255) NOT NULL UNIQUE AFTER allotted;

-- Optional: If you have existing data, populate offering_id for old rows
-- (Only safe if course_name + instructor is unique, which you enforce via index)
UPDATE course_offering
SET offering_id = CONCAT('OFFERING-', course_name, '-', instructor)
WHERE offering_id IS NULL OR offering_id = '';