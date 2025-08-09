# CRM Project

## Table of Contents
1. [Introduction](#introduction)  
2. [Prerequisites](#prerequisites)  
3. [Project Setup](#project-setup)  
4. [Database Setup](#database-setup)  
5. [Configuring Database Connection](#configuring-database-connection)  
6. [Build and Run Instructions](#build-and-run-instructions)  
7. [Using the CRM System](#using-the-crm-system)  
8. [Troubleshooting](#troubleshooting)  

---

## Introduction
 CRM Project is a lightweight Customer Relationship Management system built with Java and MySQL. It enables users to:

- Manage customer records (add, edit, delete, view).  
- Track interactions like calls and meetings.  
- Log sales transactions and customer feedback.  
- Generate sales reports by customer and date range.  
- Provide role-based access control (Admin, Sales, Support).

---

## Prerequisites

Before starting, ensure you have the following installed and configured on your system:

- **Java Development Kit (JDK) 8 or later**  
  Download from: https://www.oracle.com/java/technologies/javase-jdk11-downloads.html  
  Ensure `JAVA_HOME` is set and added to system `PATH`.

- **MySQL Server 8.0 or later**  
  Download from: https://dev.mysql.com/downloads/mysql/  
  During setup, configure a root password.

- **MySQL Connector/J (JDBC Driver)**  
  Download from: https://dev.mysql.com/downloads/connector/j/  
  Extract and copy `mysql-connector-java-x.x.xx.jar` to the `lib/` folder of this project.

- **Java IDE (Eclipse, IntelliJ IDEA, NetBeans, or command line)**

---

## Project Setup

1. **Clone or Download the Project**  
   Download the CRM project folder to your local machine.

2. **Create Java Project**  
   Open your IDE and create a new Java project named `CRMProject`.

3. **Add External JAR (MySQL Connector/J)**  
   - Copy `mysql-connector-java-x.x.xx.jar` into the project’s `lib/` folder.  
   - Add this JAR to your project’s build path (IDE: Right-click project → Properties → Java Build Path → Libraries → Add JARs).

4. **Import Source Code**  
   Add all `.java` files from the `src/` directory into your project source folder.

---

## Database Setup

1. **Start MySQL Server**  
   Make sure MySQL server is running (check via command line or services panel).

2. **Create Database**  
   Log in to MySQL shell or Workbench and run:

   ```sql
   CREATE DATABASE crm_db;
   ```

3. **Create Tables**  
   Run the SQL script `crm.sql` included in the project root to create all required tables:

   ```bash
   mysql -u root -p crm_db < crm.sql
   ```

   This script creates tables for `users`, `customers`, `interactions`, `sales`, and `feedback`.

---

## Configuring Database Connection

- Open `src/db/DBConnection.java`.

- Update the following variables to your MySQL settings:

  ```java
  private static final String URL = "jdbc:mysql://localhost:3306/crm_db?useSSL=false&serverTimezone=UTC";
  private static final String USER = "root";             // Your MySQL username
  private static final String PASSWORD = "your_password"; // Your MySQL password
  ```

- Save changes.

---

## Build and Run Instructions

### Using IDE:

- **Build:** Use your IDE’s build or compile feature to compile the project.

- **Run:** Run the `App.java` class as a Java Application.

### Using Command Line:

- Navigate to your project root directory.

- Compile all Java source files, specifying the MySQL driver in the classpath:

  ```bash
  javac -cp "lib/mysql-connector-java-x.x.xx.jar" -d bin src/**/*.java
  ```

- Run the application with:

  ```bash
  java -cp "bin:lib/mysql-connector-java-x.x.xx.jar" Main
  ```

  *(On Windows replace `:` with `;` in classpath.)*

---

## Using the CRM System

After running, the console menu guides you through the following:

### Admin Role Capabilities:
- Create new user accounts.  
- Assign roles: Admin, Sales, Support.  
- View and manage all data.

### Customer Management:
- Add new customers with details: ID, Name, Email, Phone, Address, Company, Status (Active/Inactive).  
- Edit or delete existing customers.  
- View customer lists.

### Interaction, Sales, and Feedback Tracking:
- Log calls, meetings, or other interactions per customer.  
- Record sales transactions.  
- Add customer feedback entries.

### Sales Reports & Analytics:
- View total sales summaries.  
- Generate reports filtered by date range or customer.  
- Check interaction frequency statistics.

---
## Troubleshooting

- **MySQL Connection Errors:**  
  - Verify MySQL server is running.  
  - Double-check credentials and database URL in `DBConnection.java`.

- **Class Not Found for MySQL Driver:**  
  - Ensure `mysql-connector-java-x.x.xx.jar` is correctly added to the classpath/build path.

- **Compilation Issues:**  
  - Ensure all `.java` files are compiled.  
  - Use correct classpath for external libraries.

- **Permission Errors:**  
  - Confirm your MySQL user has required privileges on `crm_db`.

---

Thank you for using CRM Project!  
Happy managing your customers!
```
