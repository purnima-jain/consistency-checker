package com.purnima.jain.consistency.checker;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.javafaker.Faker;

import lombok.AllArgsConstructor;
import lombok.Data;

@SpringBootApplication
@EnableScheduling
public class ConsistencyCheckerApplication //implements CommandLineRunner 
{

	private static final Logger logger = LoggerFactory.getLogger(ConsistencyCheckerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsistencyCheckerApplication.class, args);
		logger.info("ConsistencyCheckerApplication Started........");
	}
	
	//@Override
	public void run(String... args) throws Exception {
		Faker faker = new Faker();
		List<Customer> customers = generateFakeCustomersWithPhonesAndEmails(faker);
		String insertStatements = generateInsertStatements(customers);
		
		logger.info("POSTGRES:: Generating Customer data :: customer list :: {}", insertStatements);
		
		String insertStatementsForCassandra = generateInsertStatementsForCassandra(customers);
		logger.info("CASSANDRA:: Generating Customer data :: customer list :: {}", insertStatementsForCassandra);
	}
	
	private String generateInsertStatementsForCassandra(List<Customer> customers) {
		StringBuilder stringBuilder = new StringBuilder();
		for(Customer customer : customers) {
			String customerJson = getJson(customer);
			stringBuilder.append("INSERT INTO CUSTOMER_JSON(CUSTOMER_ID, CUSTOMER_JSON) VALUES('" + customer.getCustomerId() + "', '" + customerJson + "');\n");
		}
		return stringBuilder.toString();
	}
	
	private String getJson(Customer customer) {	
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = mapper.writeValueAsString(customer);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	private String generateInsertStatements(List<Customer> customers) {
		StringBuilder stringBuilder = new StringBuilder();
		for(Customer customer : customers) {
			stringBuilder.append(customer.toString() + "\n");
			for(Phone phone : customer.getPhones()) {
				stringBuilder.append(phone.toString() + "\n");
			}
			for(Email email : customer.getEmails()) {
				stringBuilder.append(email.toString() + "\n");
			}
		}
		return stringBuilder.toString();
	}
	
	private List<Customer> generateFakeCustomersWithPhonesAndEmails(Faker faker) {
		List<Customer> customers = new ArrayList<>();
		for (int i = 0; i < 50; i++) {			
			LocalDateTime lastUpdatedLocalDateTime = getSomeEligibleLastUpdatedLocalDateTime(faker);
			
			Customer customer = new Customer("" + faker.random().nextInt(1000, 9999), faker.name().firstName().replaceAll("'", ""), faker.name().lastName().replaceAll("'", ""), lastUpdatedLocalDateTime);	
			
			List<Phone> phones = fakeListOfPhones(faker, customer.getCustomerId());
			customer.getPhones().addAll(phones);
			
			List<Email> emails = fakeListOfEmails(faker, customer.getCustomerId());
			customer.getEmails().addAll(emails);
			
			customers.add(customer);
		}
		return customers;
	}
	
	private LocalDateTime getSomeEligibleLastUpdatedLocalDateTime(Faker faker) {
		Date now = new Date();
		Integer dayDiff = faker.random().nextInt(1, 10);
		Date lastUpdateDated = faker.date().past(dayDiff, TimeUnit.DAYS, now);
		LocalDateTime lastUpdatedLocalDateTime = lastUpdateDated.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
		return lastUpdatedLocalDateTime;
	}
	
	private static final String[] PHONE_TYPES = new String[] { "Landline", "Mobile", "Fax", "Neighbour landline", "Guardian Phone" };
	private static final String[] EMAIL_TYPES = new String[] { "Personal Email", "Business Email", "Official Email", "Guardian Email" };
	
	private List<Phone> fakeListOfPhones(Faker faker, String customerId) {
		List<Phone> phones = new ArrayList<>();
		
		Integer noOfPhones = faker.random().nextInt(1, 5);
		for(int i=0; i < noOfPhones; i++) {
			LocalDateTime lastUpdatedLocalDateTime = getSomeEligibleLastUpdatedLocalDateTime(faker);
			Phone phone = new Phone(customerId, faker.options().option(PHONE_TYPES), faker.phoneNumber().phoneNumber(), lastUpdatedLocalDateTime);
			phones.add(phone);
		}
		return phones;
	}
	
	private List<Email> fakeListOfEmails(Faker faker, String customerId) {
		List<Email> emails = new ArrayList<>();
		
		Integer noOfEmails = faker.random().nextInt(1, 5);
		for(int i=0; i < noOfEmails; i++) {
			LocalDateTime lastUpdatedLocalDateTime = getSomeEligibleLastUpdatedLocalDateTime(faker);
			Email email = new Email(customerId, faker.options().option(EMAIL_TYPES), faker.internet().emailAddress(), lastUpdatedLocalDateTime);
			emails.add(email);
		}
		return emails;
	}
	
	
}

@Data
@AllArgsConstructor
class Customer {
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	private String customerId;
	private String firstName;
	private String lastName;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime lastUpdated;
	
	private List<Phone> phones = new ArrayList<>();
	
	private List<Email> emails = new ArrayList<>();
	
	public Customer(String customerId, String firstName, String lastName, LocalDateTime lastUpdated) {
		this.customerId = customerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		return "INSERT INTO CUSTOMER_INFO(CUSTOMER_ID, FIRST_NAME, LAST_NAME, LAST_UPDATED) VALUES ('" + customerId + "', '" + firstName + "', '" + lastName + "', '" + dateTimeFormatter.format(lastUpdated) + "'); ";		
	}
	
	
}

@Data
@AllArgsConstructor
class Phone {
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@JsonIgnore
	private String customerId;
	private String phoneType;
	private String phoneNumber;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime lastUpdated;
	
	@Override
	public String toString() {
		return "INSERT INTO PHONE_INFO(CUSTOMER_ID, PHONE_TYPE, PHONE_NUMBER, LAST_UPDATED) VALUES ('" + customerId + "', '" + phoneType + "', '" + phoneNumber + "', '" + dateTimeFormatter.format(lastUpdated) + "'); ";		
	}
}

@Data
@AllArgsConstructor
class Email {
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@JsonIgnore
	private String customerId;
	private String emailType;
	private String emailAddress;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime lastUpdated;
	
	@Override
	public String toString() {
		return "INSERT INTO EMAIL_INFO(CUSTOMER_ID, EMAIL_TYPE, EMAIL_ADDRESS, LAST_UPDATED) VALUES ('" + customerId + "', '" + emailType + "', '" + emailAddress + "', '" + dateTimeFormatter.format(lastUpdated) + "'); ";		
	}
}
