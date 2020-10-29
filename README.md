Consistency-Checker
===================
**Consistency-Checker** is a Spring-Boot + Spring-Batch application to verify the consistency between the data in two databases; the RDBMS-type-row-column data in MySql and the NoSql-JSON-type data in Cassandra.

The results-of-comparison/discrepancies are persisted in a third database which is Postgres which also hosts the Spring-Batch tables.

Overall Workflow
-------------
The overall workflow of the Job is configured in the class CustomerConsistencyCheckerJobConfig.java.

The Job has been divided into two steps, each with its own Reader-Processor-Writer. 

In the first step, we read the MySql tables to check the CustomerIds that were updated in the given time-range, once we have that we store them in the Postgres table called CUSTOMER_CONSISTENCY_CHECKER_SELECTION.

In the second step, we read this data from CUSTOMER_CONSISTENCY_CHECKER_SELECTION table, use these CustomerIds to retrieve the complete Customer data from MySql and Cassandra to perform the comparison and then persist the discrepancy revealed in another table in Postgres which is CUSTOMER_CONSISTENCY_CHECKER_DISCREPANCY.



Features
-------------
#### Execution
There are two ways to execute the batch-job; one is via REST call and the other is as per the schedule.

You can provide "from" and "to" parameters to the REST call and the comparison will be performed for the data that was updated in MySql within the defined time range. If no parameters are provided, the beginning and end of century is assumed.

#### Continue/Re-execute with the same parameters
The Job works fine even if the same parameters are provided as input multiple times. It also continues from where it left if the previous execution resulted in failure. If the previous execution was successful, it will just re-execute the job again.

This works even if the application is shut-down and restarted.

#### ACL Layer
The data-model used to read data from MySql and the data-model used to read data from Cassandra are deliberately kept different and there is a third set of data-model which is used by the Consistency-Checker application internally for comparison. 

The advantage of this approach is if your data-model in MySQL or Cassandra changes, the impact on the batch-job will be minimal, the only thing that will have to be changed will be the mapping.

#### Multi-Read
To avoid latency & hitting MySql tables repeatedly for retrieving data for each CustomerId one at a time, we do a read of multiple CustomerIds in one shot in the reader of the second step (SecondaryReader.java).

And at the same time to further enhance the performance, we do a simultaneous read of all the tables in MySql (in this case, three tables) that we need the data from by using the Concurrency mechanism in Java with Executor, CompletableFuture and it's asynchronized call.  
