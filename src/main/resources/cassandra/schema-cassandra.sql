-- Describe existing keyspaces
-- DESCRIBE keyspaces;

-- Create keyspace
-- CREATE KEYSPACE customer_cassandra WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};

-- Use the newly created keyspace
-- USE customer_cassandra; 

-- Drop table (if exists): customer_json
-- DROP TABLE IF EXISTS customer_json;

-- Create table: customer_json
CREATE TABLE customer_json (
   customer_id TEXT,
   customer_json TEXT,
   PRIMARY KEY (customer_id)
   );

-- Describe the newly created table
-- DESCRIBE customer_json;