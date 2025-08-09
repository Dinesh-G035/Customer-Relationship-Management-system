package com.service;


import com.db.DBConnection;
import com.model.User;

import java.sql.*;
import java.util.Scanner;

public class CRMService {
    public void login(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                System.out.println("\u2705 Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
                showDashboard(scanner, user);
            } else {
                System.out.println("\u274C Invalid credentials.");
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    public void signup(Scanner scanner) {
        System.out.println("\n--- Sign Up ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement checkUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            checkUser.setString(1, username);
            ResultSet rs = checkUser.executeQuery();
            if (rs.next()) {
                System.out.println("\u26A0\uFE0F User already exists. Please log in.");
                return;
            }

            PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, 'user')");
            ps.setString(1, username);
            ps.setString(2, password);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("\u2705 Sign-up successful! You can now log in.");
            }
        } catch (SQLException e) {
            System.out.println("Sign-up error: " + e.getMessage());
        }
    }



    public void showDashboard(Scanner scanner, User user) {
        boolean inDashboard = true;
        while (inDashboard) {
            System.out.println("\n--- Dashboard ---");
           

            if ("admin".equals(user.getRole())) {
            	System.out.println("1. Manage Details");
                System.out.println("2. Enter Feedback");
                System.out.println("3. Assign Sales Task");
                System.out.println("4. View Sales Task Status");
                System.out.println("5. ManageInteractions");
                System.out.println("6. ManageSales");
                System.out.println("7. System configuration");  // New option for Admin
                System.out.println("8. Reporting and analytics");  // New option for Admin
                System.out.println("9. Add New Users");
                System.out.println("10. Logout");
            } else if ("manager".equals(user.getRole())) {
            	System.out.println("1. Manage Details");
                System.out.println("2. Enter Feedback");
                System.out.println("3. View Interactions");
                System.out.println("4. Reporting and analytics");  // New option for Manager
                System.out.println("5. Add New Users");
                System.out.println("6. Logout");
            } else {
                System.out.println("1. Enter Feedback");
                System.out.println("2. View Sales Task");
                System.out.println("3. Update task status");
                System.out.println("4. Logout");
            }

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (user.getRole()) {
            case "admin":
                switch (choice) {
                    case 1: manageCustomers(scanner, user); break;
                    case 2: manageFeedback(scanner); break;
                    case 3: assignSalesTask(scanner); break;
                    case 4: viewSalesTaskStatus(scanner); break;
                    case 5: manageInteractions(scanner); break;
                    case 6: manageSales(scanner); break;
                    case 7: systemConfiguration(scanner); break;
                    case 8: reportingAndAnalytics(scanner); break;
                    case 9: manageUsers(scanner, user); break;
                    case 10: inDashboard = false; break;
                    default: System.out.println("Invalid option."); break;
                }
                break;

            case "manager":
                switch (choice) {
                    case 1: manageCustomers(scanner, user); break;
                    case 2: manageFeedback(scanner); break;
                    case 3: viewInteractions(); break;
                    case 4: reportingAndAnalytics(scanner); break;
                    case 5: manageUsers(scanner, user); break;
                    case 6: inDashboard = false; break;
                    default: System.out.println("Invalid option."); break;
                }
                break;

            default:
                switch (choice) {
                    case 1: manageFeedback(scanner); break;
                    case 2: performSalesTask(scanner, user); break;
                    case 3: updateTaskStatus(scanner); break;
                    case 4: inDashboard = false; break;
                    default: System.out.println("Invalid option."); break;
                }
                break;
            }
        }
    }

    
    private void updateTaskStatus(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            // Step 1: Ask for the Sales Task ID
            System.out.print("Enter Sales Task ID to update: ");
            int taskId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            // Step 2: Check if the task ID exists in the database
            String checkQuery = "SELECT id FROM sales_tasks WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, taskId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("❌ No task found with the given ID.");
                return;  // Exit the method if no task found
            }

            // Step 3: Ask for the new status
            System.out.print("Enter new status (e.g., 'completed', 'in progress', 'pending'): ");
            String newStatus = scanner.nextLine();

            // Step 4: Update the status of the task
            String updateQuery = "UPDATE sales_tasks SET status = ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, newStatus);
            updateStmt.setInt(2, taskId);
            
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Sales task status updated successfully.");
            } else {
                System.out.println("❌ Error updating task status.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating task status: " + e.getMessage());
        }
    }


    
    private void manageCustomers(Scanner scanner, User user) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Manage users ---");
            System.out.println("1. My Details");
            System.out.println("2. View My Details");
            System.out.println("3. Back to Dashboard");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addCustomer(scanner, user);break;
                case 2: viewCustomers(user);break;
                case 3: managing = false;break;
                default: System.out.println("Invalid option.");break;
            }
        }
    }
    
    private void manageFeedback(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Customer ID: ");
            int customerId = scanner.nextInt(); scanner.nextLine();
            System.out.print("Feedback: ");
            String feedback = scanner.nextLine();

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO feedback (customer_id, feedback_text, date_submitted) VALUES (?, ?, CURDATE())"
            );
            ps.setInt(1, customerId);
            ps.setString(2, feedback);
            ps.executeUpdate();
            System.out.println("\u2705 Feedback submitted.");
        } catch (SQLException e) {
            System.out.println("Error submitting feedback: " + e.getMessage());
        }
    }
    
    // Add the sales tasks to the system
    private void assignSalesTask(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("\n--- Assign Sales Task ---");
            System.out.print("User ID to assign task: ");
            int userId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.print("Customer ID for the sale: ");
            int customerId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            // Check if the customer exists
            if (!isCustomerExist(conn, customerId)) {
                System.out.println("Error: Customer ID does not exist.");
                return; // Exit the method if the customer does not exist
            }
            
            System.out.print("Product to sell: ");
            String product = scanner.nextLine();
            System.out.print("Task Description: ");
            String description = scanner.nextLine();
            System.out.print("Status: "); // Get status from the user
            String status = scanner.nextLine();
            
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO sales_tasks (user_id, customer_id, product, description, status) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setInt(1, userId);
            ps.setInt(2, customerId);
            ps.setString(3, product);
            ps.setString(4, description);
            ps.setString(5, status); // Bind the 'status' as a parameter
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("\u2705 Sales task assigned successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error assigning sales task: " + e.getMessage());
        }
    }

    // Helper method to check if customer exists
    private boolean isCustomerExist(Connection conn, int customerId) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM customers WHERE id = ?");
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // If a row is returned, the customer exists
        } catch (SQLException e) {
            System.out.println("Error checking customer existence: " + e.getMessage());
            return false;
        }
    }

    
 // View Sales Task Status for admintrative
    private void viewSalesTaskStatus(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("\n--- View Sales Task Status ---");
            PreparedStatement ps = conn.prepareStatement(
                "SELECT st.id, u.username, c.name, st.product, st.status " +
                "FROM sales_tasks st " +
                "JOIN users u ON st.user_id = u.id " +
                "JOIN customers c ON st.customer_id = c.id"
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("Task ID: " + rs.getInt("id"));
                System.out.println("Assigned to: " + rs.getString("username"));
                System.out.println("Customer: " + rs.getString("name"));
                System.out.println("Product: " + rs.getString("product"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing sales task status: " + e.getMessage());
        }
    }
    
    // Users perform assigned sales task (mark as completed)
    private void performSalesTask(Scanner scanner, User user) {
        try (Connection conn = DBConnection.getConnection()) {
            // Show the user their pending sales tasks
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM sales_tasks WHERE user_id = ? AND status = 'pending'"
            );
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Pending Sales Tasks ---");
            boolean hasTasks = false;
            while (rs.next()) {
                hasTasks = true;
                int taskId = rs.getInt("id");
                int customerId = rs.getInt("customer_id");
                String product = rs.getString("product");
                String description = rs.getString("description");

                System.out.println("Task ID: " + taskId);
                System.out.println("Customer ID: " + customerId);
                System.out.println("Product: " + product);
                System.out.println("Description: " + description);
                System.out.println("------------------------");
            }

            if (!hasTasks) {
                System.out.println("No pending tasks.");
                return;
            }

            // Allow the user to mark a task as completed
            System.out.print("Enter Task ID to mark as completed: ");
            int taskId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Mark the task as completed
            ps = conn.prepareStatement(
                "UPDATE sales_tasks SET status = 'completed' WHERE id = ? AND user_id = ?"
            );
            ps.setInt(1, taskId);
            ps.setInt(2, user.getId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("\u2705 Task completed successfully.");
            } else {
                System.out.println("Error completing task.");
            }
        } catch (SQLException e) {
            System.out.println("Error performing sales task: " + e.getMessage());
        }
    }

    
    private void manageInteractions(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Customer ID: ");
            int customerId = scanner.nextInt(); scanner.nextLine();
            System.out.print("Date (YYYY-MM-DD): ");
            String interaction_date = scanner.nextLine();
            System.out.print("Description: ");
            String description = scanner.nextLine();
            System.out.print("Notes: ");
            String notes = scanner.nextLine();
            
            PreparedStatement check = conn.prepareStatement("SELECT id FROM customers WHERE id = ?");
            check.setInt(1, customerId);
            ResultSet rs = check.executeQuery();
            if (!rs.next()) {
                System.out.println("❌ Customer ID not found. Please enter a valid one.");
                return;
            }
            
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO interactions (customer_id, interaction_date,description, notes) VALUES (?,?, ?, ?)"
            );
            ps.setInt(1, customerId);
            ps.setDate(2, Date.valueOf(interaction_date));
            ps.setString(3, description);
            ps.setString(4, notes);
            ps.executeUpdate();
            System.out.println("\u2705 Interaction recorded.");
        } catch (SQLException e) {
            System.out.println("Error recording interaction: " + e.getMessage());
        }
    }

    private void manageSales(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Customer ID: ");
            int customerId = scanner.nextInt(); scanner.nextLine();
            System.out.print("Product: ");
            String product = scanner.nextLine();
            System.out.print("Amount: ");
            double amount = scanner.nextDouble(); scanner.nextLine();

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO sales (customer_id, sale_date, amount, product) VALUES (?, CURDATE(), ?, ?)"
            );
            ps.setInt(1, customerId);
            ps.setDouble(2, amount);
            ps.setString(3, product);
            ps.executeUpdate();
            System.out.println("\u2705 Sale recorded.");
        } catch (SQLException e) {
            System.out.println("Error recording sale: " + e.getMessage());
        }
    }

   
    
    private void systemConfiguration(Scanner scanner) {
    	boolean manage =true; 
    	while(manage) {
	        System.out.println("\n--- System Configuration ---");
	        // Example: Configure system settings (for simplicity, we’ll just print an option)
	        System.out.println("1. Set Max Users");
	        System.out.println("2. Set Maintenance Mode");
	        System.out.println("3. Back to Dashboard");
	
	        System.out.print("Choose an option: ");
	        int choice = scanner.nextInt();
	        scanner.nextLine(); // Consume newline
	
	        switch (choice) {
	            case 1 : setMaxUsers(scanner);break; // Example method
	            case 2 :toggleMaintenanceMode(scanner);break; // Example method
	            case 3 : manage=false;break;
	            default : System.out.println("Invalid option.");break;
	        }
    	}
    }

    private void setMaxUsers(Scanner scanner) {
        System.out.print("Enter maximum number of users: ");
        int maxUsers = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        // Save to system configuration (Example)
        System.out.println("Max users set to: " + maxUsers);
    }

    private void toggleMaintenanceMode(Scanner scanner) {
        System.out.print("Enable maintenance mode? (yes/no): ");
        String response = scanner.nextLine();
        if ("yes".equalsIgnoreCase(response)) {
            // Set maintenance mode (Example)
            System.out.println("Maintenance mode enabled.");
        } else {
            System.out.println("Maintenance mode disabled.");
        }
    }

    private void reportingAndAnalytics(Scanner scanner) {
    	boolean ana=true;
    	while(ana) {
	        System.out.println("\n--- Reporting and Analytics ---");
	        // Example: Display sales or customer reports
	        System.out.println("1. View Sales Report");
	        System.out.println("2. View Customer Interaction Report");
	        System.out.println("3. Back to Dashboard");
	
	        System.out.print("Choose an option: ");
	        int choice = scanner.nextInt();
	        scanner.nextLine(); // Consume newline
	
	        switch (choice) {
	            case 1 : viewSalesReport();break;  // Existing method
	            case 2 : viewCustomerInteractionReport(scanner);break;  // New example method
	            case 3 : ana=false;break;
	            default : System.out.println("Invalid option.");break;
	        }
    	}
    }

    private void viewCustomerInteractionReport(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("\n--- Customer Interaction Report ---");

            // SQL query to get name, number of interactions, total sales, and last interaction date
            String sql = """
                SELECT 
                    c.name, 
                    c.email, 
                    COUNT(i.id) AS interactions, 
                    SUM(s.amount) AS total_sales, 
                    MAX(i.interaction_date) AS last_interaction
                FROM customers c
                LEFT JOIN interactions i ON i.customer_id = c.id
                LEFT JOIN sales s ON s.customer_id = c.id
                GROUP BY c.name, c.email
                ORDER BY interactions DESC
            """;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Process the results
            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                int interactions = rs.getInt("interactions");
                double totalSales = rs.getDouble("total_sales");
                String lastInteraction = rs.getString("last_interaction") != null ? rs.getString("last_interaction") : "No interactions yet";

                System.out.println("Customer: " + name);
                System.out.println("Email: " + email);
                System.out.println("Number of Interactions: " + interactions);
                System.out.println("Total Sales: $" + totalSales);
                System.out.println("Last Interaction Date: " + lastInteraction);
                System.out.println("---------------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error generating customer interaction report: " + e.getMessage());
        }
    }

    private void viewInteractions() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("""
                SELECT i.id, c.name, i.interaction_date, i.description, i.notes 
                FROM interactions i
                JOIN customers c ON i.customer_id = c.id
                ORDER BY i.interaction_date DESC
            """);

            System.out.println("\n--- Interactions ---");
            while (rs.next()) {
                System.out.println("Interaction ID: " + rs.getInt("id"));
                System.out.println("Customer: " + rs.getString("name"));
                System.out.println("Interaction Date: " + rs.getDate("interaction_date"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Notes: " + rs.getString("notes"));
                System.out.println("------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching interactions: " + e.getMessage());
        }
    }




    private void addCustomer(Scanner scanner, User user) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("\n--- Edit My Details ---");
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Phone: ");
            String phone = scanner.nextLine();
            System.out.print("Address: ");
            String address = scanner.nextLine();
            System.out.print("Company: ");
            String company = scanner.nextLine();
            System.out.print("Status (Active/Inactive): ");
            String status = scanner.nextLine();

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO customers (name, email, phone, address, company, status, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setString(5, company);
            ps.setString(6, status);
            ps.setInt(7, user.getId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("\u2705 Customer added successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }

    private void viewCustomers(User user) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps;
            if ("admin".equals(user.getRole())) {
                ps = conn.prepareStatement("SELECT * FROM customers");
            } else {
                ps = conn.prepareStatement("SELECT * FROM customers WHERE user_id = ?");
                ps.setInt(1, user.getId());
            }

            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- Customer List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Company: " + rs.getString("company"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customers: " + e.getMessage());
        }
    }

    private void manageUsers(Scanner scanner, User user) {
        try (Connection conn = DBConnection.getConnection()) {  // Ensure this is inside a try block
            System.out.print("\n--- Add New User ---\nUsername: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            String role = "user";  // Default role
            if ("manager".equals(user.getRole())) {
                System.out.print("Role (user/manager): ");
                role = scanner.nextLine();
            } else {
                System.out.print("Role (admin/user/manager): ");
                role = scanner.nextLine();
            }

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("\u2705 New user created.");
            }
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }



    private void viewSalesReport() {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("\n\uD83D\uDCCA --- Sales Report & Analytics ---");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SUM(amount) AS total_sales FROM sales");
            if (rs.next()) {
                System.out.println("\uD83D\uDCB0 Total Sales: ₹" + rs.getDouble("total_sales"));
            }

            rs = stmt.executeQuery("""
                SELECT c.name, SUM(s.amount) AS total
                FROM sales s
                JOIN customers c ON s.customer_id = c.id
                GROUP BY c.name
                ORDER BY total DESC
                LIMIT 3
            """);
            System.out.println("\n\uD83C\uDFC6 Top Customers:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("name") + ": ₹" + rs.getDouble("total"));
            }

            rs = stmt.executeQuery("""
                SELECT product, COUNT(*) AS count, SUM(amount) AS total
                FROM sales
                GROUP BY product
                ORDER BY count DESC
                LIMIT 3
            """);
            System.out.println("\n\uD83D\uDCE6 Top Products:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("product") + " (Sold: " + rs.getInt("count") + ", ₹" + rs.getDouble("total") + ")");
            }

            rs = stmt.executeQuery("""
                SELECT sale_date, COUNT(*) AS sales_count, SUM(amount) AS total
                FROM sales
                GROUP BY sale_date
                ORDER BY sale_date DESC
                LIMIT 5
            """);
            System.out.println("\n\uD83D\uDCC5 Recent Sales by Date:");
            while (rs.next()) {
                System.out.println("- " + rs.getDate("sale_date") + ": " + rs.getInt("sales_count") + " sales, ₹" + rs.getDouble("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error generating sales report: " + e.getMessage());
        }
    }
}
