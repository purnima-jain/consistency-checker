-- Creating the database: customer_mysql
-- CREATE DATABASE customer_mysql;
-- USE customer_mysql;    
-- show tables;

-- Drop the table: customer_info
DROP TABLE IF EXISTS customer_info;

-- Creating the table: customer_info
CREATE TABLE customer_info
(
	customer_id VARCHAR(10) NOT NULL,
	first_name VARCHAR(100), 
	last_name VARCHAR(100),
	last_updated DATETIME,
    PRIMARY KEY ( customer_id )
);

-- Drop the table: phone_info
DROP TABLE IF EXISTS phone_info;

-- Creating the table: phone_info
CREATE TABLE phone_info
(
	customer_id VARCHAR(10) NOT NULL REFERENCES customer_info(customer_id),
	phone_type VARCHAR(100), 
	phone_number VARCHAR(100),
	last_updated DATETIME,
    PRIMARY KEY ( customer_id, phone_type, phone_number )
);

-- Drop the table: email_info
DROP TABLE IF EXISTS email_info;

-- Creating the table: email_info
CREATE TABLE email_info
(
	customer_id VARCHAR(10) NOT NULL REFERENCES customer_info(customer_id),
	email_type VARCHAR(100), 
	email_address VARCHAR(100),
	last_updated DATETIME,
    PRIMARY KEY ( customer_id, email_type, email_address )
);