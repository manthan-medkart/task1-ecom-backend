package com.task.e_commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@SpringBootApplication
public class ECommerceApplication {

	public static void main(String[] args) {
		try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ecommerce", "postgres", "M@nthan2104");
			 Statement stmt = conn.createStatement()) {
			stmt.execute("ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_order_status_check");
			System.out.println("Successfully dropped orders_order_status_check check constraint");
			
			// Print users
			try (var rs = stmt.executeQuery("SELECT id, name, email FROM users")) {
				System.out.println("=== USERS IN DB ===");
				while (rs.next()) {
					System.out.printf("User ID: %d, Name: %s, Email: %s%n", rs.getLong("id"), rs.getString("name"), rs.getString("email"));
				}
			}
			// Print orders
			try (var rs = stmt.executeQuery("SELECT id, user_entity_id, order_status, total_price, order_date FROM orders")) {
				System.out.println("=== ORDERS IN DB ===");
				while (rs.next()) {
					System.out.printf("Order ID: %d, User ID: %d, Status: %s, Price: %.2f, Date: %s%n",
							rs.getLong("id"), rs.getLong("user_entity_id"), rs.getString("order_status"), rs.getDouble("total_price"), rs.getString("order_date"));
				}
			}
		} catch (Exception e) {
			System.out.println("Check constraint check/drop bypassed: " + e.getMessage());
		}
		SpringApplication.run(ECommerceApplication.class, args);
	}

}
