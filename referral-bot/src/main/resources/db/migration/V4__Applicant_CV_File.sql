-- V3__update_applicant_cv_columns.sql

-- Drop the old cvPath column
ALTER TABLE applicants
DROP COLUMN IF EXISTS cv_path;

-- Add the new cv_file_id column
ALTER TABLE applicants
    ADD COLUMN cv_file_id VARCHAR;

-- Add the new cv_file_unique_id column
ALTER TABLE applicants
    ADD COLUMN cv_file_unique_id VARCHAR;
