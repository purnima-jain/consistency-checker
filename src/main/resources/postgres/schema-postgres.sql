-- Database: comparison_result_postgres
-- DROP DATABASE comparison_result_postgres;
-- Creating the database: comparison_result_postgres
-- CREATE DATABASE comparison_result_postgres
--     WITH 
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'English_India.1252'
--     LC_CTYPE = 'English_India.1252'
--     TABLESPACE = pg_default
--     CONNECTION LIMIT = -1;

-- Drop the table: customer_consistency_checker_selection
DROP TABLE IF EXISTS customer_consistency_checker_selection;

-- Creating the table: customer_consistency_checker_selection
CREATE TABLE customer_consistency_checker_selection
(
	selection_id UUID NOT NULL,
	job_instance_id INT,
	job_execution_id INT,
	step_execution_id INT,
	customer_id VARCHAR(10),
	processing_status VARCHAR(50),
	PRIMARY KEY (selection_id)
);

-- Drop the table: customer_consistency_checker_discrepancy
DROP TABLE IF EXISTS customer_consistency_checker_discrepancy;

-- Creating the table: customer_consistency_checker_discrepancy
CREATE TABLE customer_consistency_checker_discrepancy
(
	id UUID NOT NULL,
	customer_id VARCHAR(10),
	inconsistency_type VARCHAR(50),
	mysql_contents TEXT,
	cassandra_contents TEXT,
	reconciliation_status VARCHAR(50),
	job_instance_id INT,
	job_execution_id INT,
	step_execution_id INT,
	PRIMARY KEY (id)
);

-- Cleaning up BATCH tables
-- DELETE FROM BATCH_STEP_EXECUTION_CONTEXT;
-- DELETE FROM BATCH_STEP_EXECUTION;
----------------
-- DELETE FROM BATCH_JOB_EXECUTION_PARAMS;
-- DELETE FROM BATCH_JOB_EXECUTION_CONTEXT;
-- DELETE FROM BATCH_JOB_EXECUTION;
----------------
-- DELETE FROM BATCH_JOB_INSTANCE;


