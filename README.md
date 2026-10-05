# Apex Horizon Bank — Full-Stack Java Online Banking Web Application

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)

---

## 1. Project Overview

**Apex Horizon Bank** is a complete, production-grade, full-stack **Enterprise Online Banking Web Application** engineered exclusively using **Java 17+ and Spring Boot 3**. 

Designed as a **major college capstone and enterprise viva project**, it replaces basic CRUD mockups with realistic fintech functionality: atomic `@Transactional` money transfers, dynamic fee & tax computation, OpenPDF statement generation, 3D card lifecycle controls, loan amortization & administrative loan disbursements, security audit logging, and role-based access control.

---

## 2. Technology Stack

| Layer | Technologies |
|---|---|
| **Core Language** | Java 17+ / Java 21 / Java 25 |
| **Backend Framework** | Spring Boot 3.3.4, Spring MVC, Spring Data JPA |
| **Security & Auth** | Spring Security 6 (BCrypt Salted Password Hashing, CSRF, Session Management) |
| **ORM / Persistence** | Hibernate 6.x with JPA Entities & Repositories |
| **Database** | MySQL 8.x (with H2 in-memory profile support for zero-config test runs) |
| **Presentation Layer** | Thymeleaf 3, HTML5, CSS3, Bootstrap 5.3, Bootstrap Icons |
| **Document Generation** | OpenPDF (iText LGPL fork) for PDF Statements, Apache Commons CSV |
| **Data Visualization** | Chart.js 4.4 for Cash Flow & Admin Volume Analytics |
| **Build Tool** | Apache Maven 3.9 (Includes cross-platform `mvnw` & `mvnw.cmd` wrapper) |

---

## 3. Demo Credentials

The platform initializes demo accounts automatically on initial startup:

### 👑 Executive Bank Administrator
- **Email:** `admin@bankdemo.com`
- **Password:** `Admin@123`
- **Role:** `ROLE_ADMIN`
- **Access URL:** `http://localhost:8080/admin/dashboard`

### 👤 Primary Retail Customer (Alexander Wright)
- **Email:** `customer@bankdemo.com`
- **Password:** `Customer@123`
- **Role:** `ROLE_CUSTOMER`
- **Account Number:** `ACC1000889901` (Balance: `$25,450.00`)
- **Access URL:** `http://localhost:8080/customer/dashboard`

### 👥 Additional Demo Counterparty Accounts (for transfer testing)
- **Sarah Jenkins:** `sarah.jenkins@bankdemo.com` / `Customer@123` (`ACC1000889902` - Balance: `$14,800.00`)
- **Elena Rostova:** `elena.rostova@bankdemo.com` / `Customer@123` (`ACC1000889903` - Balance: `$9,350.00`)

---

## 4. Key Functional Features

### A. Customer Portal
1. **Self-Service Registration & Instant Provisioning:**
   - Validates user input, hashes password via BCrypt, assigns unique Customer ID (`CUSTxxxxxx`), creates Account (`ACCxxxxxxxxxx`), assigns welcome funds, and logs security audit trail.
2. **Interactive Financial Dashboard:**
   - Real-time ledger balance, available spendable balance, inflow/outflow totals, doughnut cash flow proportion, and bar activity graphs.
3. **Atomic Money Transfers (`@Transactional`):**
   - Transfer funds to any account or saved beneficiary.
   - Dynamic real-time calculation of transfer fees and applicable service taxes.
   - Atomic debit and credit guarantee — full rollback on any failure.
4. **Deposit & Withdrawal Engine:**
   - Instant balance top-ups (Wire, UPI, CDM) and ATM/cash withdrawals with balance validation.
5. **Saved Beneficiaries:**
   - Save frequently used counterparty accounts for 1-click payments.
6. **Payment Cards Center:**
   - Virtual VISA/Mastercard 3D flip card visualizer with masked numbers, live card freeze/unfreeze toggles, and spending limit controls.
7. **Loans & Credit Financing:**
   - Real-time Loan EMI calculator.
   - Application submission for Personal, Auto, Education, and Home loans.
   - Live tracking of application status and repayment schedule.
8. **Statement Generation:**
   - Download official, letterheaded PDF Statements with custom date ranges via OpenPDF.
   - Download raw CSV transaction ledgers for spreadsheet accounting.
9. **Notifications & Security Settings:**
   - Notification bell with unread counter.
   - In-portal password update with current credential verification.

### B. Administrator Command Console
1. **Executive Dashboard:**
   - Total platform liquidity, active customer accounts, transaction volume charts, and pending review alerts.
2. **Customer & Account 360 Management:**
   - Search customers by name, email, ID, or phone.
   - Inspect linked cards, accounts, and loans.
   - Suspend/Activate customer profiles or Freeze/Unfreeze bank accounts.
3. **Transaction Surveillance:**
   - System-wide transaction filtering by type, status, date interval, or reference number.
4. **Loan Approvals & Capital Disbursement:**
   - Evaluate credit risk, adjust approved amount, interest rate (APR), and tenure.
   - Approving automatically **credits capital directly into the customer's account balance** and records a transaction.
5. **Dynamic Fee & Limit Settings:**
   - Adjust transfer fee percentage, service tax rates, savings APR, daily limits, and support hotlines in real-time.
6. **Security Audit Log:**
   - Full immutable log of logins, transfers, card freezes, status changes, and administrative actions with IP metadata.

---

## 5. Architectural Structure

```text
src/main/java/com/bankingsystem/
├── BankingApplication.java           # Spring Boot Application Entrypoint
├── config/                           # SecurityConfig, WebMvcConfig, AppConfig
├── controller/                       # Spring MVC Controllers
│   ├── HomeController.java           # Public Pages & Fee API
│   ├── AuthController.java           # Login & Registration
│   ├── Customer*.java                # 9 Customer Portal Controllers
│   └── Admin*.java                   # 8 Admin Console Controllers
├── dto/                              # Jakarta Validation Form Data Transfer Objects
├── entity/                           # JPA Hibernate Entities & Enums (BigDecimal precision)
├── exception/                        # GlobalExceptionHandler & Domain Exceptions
├── repository/                       # Spring Data JPA Repositories
├── security/                         # UserDetailsService & Auth Success Handlers
├── service/                          # Service Interfaces & Implementations
└── util/                             # DataInitializer, PdfStatementGenerator, CsvStatementGenerator

src/main/resources/
├── application.properties            # Primary MySQL Configuration
├── application-dev.properties        # Zero-Config H2 In-Memory Fallback Profile
├── schema.sql / data.sql             # Relational DDL & Seed Scripts
├── static/
│   ├── css/banking-theme.css         # Modern Fintech Design System
│   └── js/ (main.js, charts.js, loan-calc.js)
└── templates/                        # Thymeleaf Templates (auth, customer, admin, fragments, errors)
```

---

## 6. Installation & Setup Guide

### Prerequisites
- **Java 17 or newer** (compatible with Java 17, 21, and 25)
- **MySQL 8.0+** (or use included H2 dev profile)
- **Maven** (or use included `./mvnw` wrapper)

### Step 1: Clone or Open the Project
Navigate to the project root directory:
```bash
cd "d:/java banking"
```

### Step 2: Configure MySQL Database
Start your MySQL server and create the database:
```sql
CREATE DATABASE online_banking;
```

Update your credentials in `src/main/resources/application.properties` (or set environment variables `DB_USERNAME` and `DB_PASSWORD`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/online_banking?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 3: Build the Project
Using the included Maven wrapper:
```bash
# Windows
.\mvnw.cmd clean package -DskipTests

# Linux / macOS
./mvnw clean package -DskipTests
```

### Step 4: Run the Application
Start the Spring Boot application:

**With MySQL (Default):**
```bash
.\mvnw.cmd spring-boot:run
```

**With In-Memory H2 (Instant Zero-Setup Mode without MySQL):**
```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### Step 5: Access the Web Application
Open your browser and navigate to:
```text
http://localhost:8080
```

---

## 7. Application URL Directory

### 🌐 Public Portal
- `GET /` — Landing Page ("Banking Made Simple, Secure & Smarter")
- `GET /about` — About Us & Architectural Principles
- `GET /services` — Digital Banking Solutions
- `GET /contact` — Contact Support Desk
- `GET /login` — Sign In (with 1-click Demo Account Fillers)
- `GET /register` — Open an Account Form

### 👤 Customer Portal (`ROLE_CUSTOMER`)
- `GET /customer/dashboard` — Personal Financial Dashboard
- `GET /customer/account` — Account Portfolio & Limits
- `GET /customer/deposit` — Deposit Funds Form
- `GET /customer/withdraw` — Cash Withdrawal Form
- `GET /customer/transfer` — Money Transfer (Live Fee Calculator)
- `GET /customer/transactions` — Historical Transaction Ledger
- `GET /customer/transactions/statement/pdf` — Download PDF Statement
- `GET /customer/transactions/statement/csv` — Download CSV Statement
- `GET /customer/beneficiaries` — Saved Beneficiaries Management
- `GET /customer/cards` — 3D Cards Center & Limit Controls
- `GET /customer/loans` — Loan Application & Live EMI Calculator
- `GET /customer/notifications` — Real-Time Alert Center
- `GET /customer/profile` — KYC & Profile Management
- `GET /customer/settings` — Security & Password Update

### 👑 Admin Console (`ROLE_ADMIN`)
- `GET /admin/dashboard` — Executive Metrics & Volume Trends
- `GET /admin/customers` — Customer Accounts Search & Control
- `GET /admin/customers/{id}` — Customer 360 Deep Profile
- `GET /admin/accounts` — Master Account Ledger & Freeze/Unfreeze
- `GET /admin/transactions` — Global Transaction Surveillance
- `GET /admin/loans` — Loan Approval & Disbursement Desk
- `GET /admin/cards` — Issued Cards Surveillance
- `GET /admin/audit-logs` — Security Event Audit Trail
- `GET /admin/reports` — Platform Financial Intelligence
- `GET /admin/settings` — Fee & Policy Configuration

---

## 8. Financial Transaction Safety Guarantees

1. **`BigDecimal` Monetary Precision:** All monetary amounts, interest calculations, fees, and ledger balances use `java.math.BigDecimal` with `RoundingMode.HALF_UP` to prevent floating-point precision leaks.
2. **ACID Transaction Boundaries:** All balance updates and transfers use Spring's `@Transactional(rollbackFor = Exception.class)`. If an error occurs between the sender debit and receiver credit, the entire operation is automatically rolled back.
3. **Defense Against Negative Balances:** Double-checked server-side balance verifications prevent over-drafting beyond permitted thresholds.
4. **Data Masking:** Card numbers are stored and transmitted in masked format (`4532 •••• •••• 8812`) to preserve payment security standards.

---

## 9. License & Project Viva Attribution

Developed as a modern Java Full-Stack Enterprise Capstone project. Free to use, demonstrate, and extend for academic and portfolio demonstration.
