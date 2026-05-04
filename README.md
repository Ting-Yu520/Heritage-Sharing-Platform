# 🏛️ Heritage Sharing Platform

![License](https://img.shields.io/badge/license-MIT-green)
![Java](https://img.shields.io/badge/Java-21-blue)
![Vue](https://img.shields.io/badge/Vue-3.x-brightgreen)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)

A full-stack interactive community platform designed for the preservation and inheritance of intangible cultural heritage. It features commercial-grade functionalities, including **permission control, content creation, interactive comments, an audit workflow, message notifications, and system audit logs**.

---

## 📖 Table of Contents

- [Project Introduction](#project-introduction)
- [Core Features](#core-features)
- [Technical Architecture](#technical-architecture)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
   - [Prerequisites](#prerequisites)
   - [Database Configuration](#database-configuration)
   - [Backend Startup](#backend-startup)
   - [Frontend Startup](#frontend-startup)
   - [Email Configuration (Optional)](#email-configuration-optional)
- [Test Accounts](#test-accounts)
- [Key Implementations](#key-implementations)
- [FAQ](#faq)
- [Future Optimization Plans](#future-optimization-plans)
- [Contributing](#contributing)
- [License](#license)

---

## Project Introduction

The **Heritage Sharing Platform** is a full-stack application with a decoupled frontend and backend architecture, designed to allow users to share, discover, and interact with intangible cultural resources. The system implements a complete workflow from visitor browsing, user registration, resource submission, and administrator review, to role permission assignment and message notification. It serves as an excellent scaffold for full-stack learning, course design, or small-to-medium content communities.

> ✅ All 9 core modules have been fully implemented, covering a total of 45 Product Backlog Items (PBIs).

---

## Core Features

- 🔐 **Permission Engine**: Dynamic role-based access control (RBAC) and unauthorized access interception (VIEWER / CONTRIBUTOR / ADMIN).
- ✍️ **Creator Center**: Supports automatic saving to drafts, withdrawal and editing of submissions, and a publishing workflow with mandatory validation for rich text and images.
- 💬 **Interactive Discussions**: Infinite nested "threaded" comments, soft deletion preserving the comment hierarchy, and upvoting/downvoting (limited to once per user).
- 🛡️ **Risk Control & Auditing**: Administrators can approve or reject resource publications, handle reported comments, and automatically send warning notifications.
- 🔔 **Aggregated Notification System**: One-click "mark all as read" and granular notification preference settings.
- 📊 **System Audit**: All critical operations are automatically recorded. Administrators can view logs and export them as CSV files.
- 🗂️ **Master Data Management**: Secure deletion mechanism with real-time detection of resource usage.
- 🤖 **Auto-Archiving Bot**: A background scheduled task that automatically archives resources that have not been updated for a specified period.
- 🎂 **Birthday Registration**: Users can input their birthday during registration and update it in their profile.
- 📧 **Real Email Password Recovery**: Supports sending verification codes via QQ Mail SMTP, with a console fallback mechanism preserved.

---

## Technical Architecture

| Layer | Technology Stack |
|------|--------|
| Frontend Framework | Vue 3 + Element Plus + Axios |
| Backend Framework | Spring Boot 3.2.4 + MyBatis-Plus 3.5.5 |
| Database | MySQL 8.0 (Charset: utf8mb4) |
| Development Tools | IntelliJ IDEA Community / VS Code |
| Build Tools | Maven / Vite |
| Cross-Origin Handling | @CrossOrigin + Frontend Proxy |
| Email Service | Spring Boot Mail (Default: QQ Mail SMTP) |

**Architecture Highlights**: The frontend and backend are completely decoupled. The frontend uses Axios to call RESTful APIs, and the backend returns JSON data.

---

## Project Structure

```text
Heritage-Sharing-Platform/
├── heritage-ui/                   # Frontend Vue 3 Project
│   ├── public/
│   └── src/
│       ├── assets/                # Static assets
│       ├── components/            # Reusable components
│       ├── router/                # Route configurations
│       ├── views/                 # Page components
│       ├── App.vue                # Root component
│       └── main.js                # Entry file
├── platform/                      # Backend Spring Boot Project
│   ├── src/main/java/com/heritage/platform/
│   │   ├── config/                # Configurations (Interceptors, CORS, Email, etc.)
│   │   ├── controller/            # Controllers (API layer)
│   │   ├── entity/                # Entity classes
│   │   ├── mapper/                # MyBatis-Plus Mappers
│   │   ├── service/               # Service layer (Business logic, Email)
│   │   └── task/                  # Scheduled tasks
│   ├── src/main/resources/
│   │   ├── application-sample.properties  # Sample configuration (Public)
│   │   └── application.properties         # Real configuration (Ignored by Git)
│   └── db/                        # Database backups (Optional)
└── README.md
```

---

## Quick Start

> 🧩 Even if you are a beginner, you can run this project locally by following these steps!

### Prerequisites

Ensure the following tools are installed on your computer:

1. **Node.js** (v16.x or higher): [Download](https://nodejs.org/)
2. **Java Development Kit 21**: [Download Corretto 21](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
3. **MySQL 8.0**: [Download Community Server](https://dev.mysql.com/downloads/mysql/8.0.html)
4. **Database Management Tool** (DBeaver recommended, [Download](https://dbeaver.io/))
5. **Development IDE**: IntelliJ IDEA (Community Edition is free) or VS Code

> 🔰 Beginner Tip: When installing MySQL, remember the **root password** you set, as you will need it later.

---

### Database Configuration

1. Open your MySQL management tool (e.g., DBeaver) and connect to your MySQL service.
2. Create a new database named **`heritage_db`** (Charset: `utf8mb4`, Collation: `utf8mb4_general_ci`).
3. Import the latest SQL file located in the project root (e.g., `heritage_db.sql`):
   - In DBeaver, right-click the newly created `heritage_db` → Select **Execute Script** → Choose the `.sql` file to run.
   - This will automatically create all tables and insert some test data.

---

### Backend Startup

1. Open the `platform` folder with IntelliJ IDEA.
2. **Configure Database Connection**:
   - Copy `platform/application-sample.properties` and rename it to `application.properties`.
   - Open `application.properties` and replace the database password and email authorization code with your actual credentials (Email configuration is optional, see [Email Configuration](#email-configuration-optional)).
   - If you prefer not to configure a real email service, you can use the current `application.properties` (leave the password blank). The system will print the verification code in the console.
3. Wait for IDEA to automatically download Maven dependencies (until the progress bar in the bottom right corner disappears).
4. Locate `PlatformApplication.java` and click the green triangle arrow on the left to run it.
5. When the console displays `Started PlatformApplication in X.XXX seconds`, the backend has started successfully!

---

### Frontend Startup

1. Open the `heritage-ui` folder with VS Code.
2. In VS Code, press `` Ctrl + ` `` to open the terminal and execute:
   ```bash
   npm install
   ```
   (This step is only required the first time to download dependencies.)
3. Once the installation is complete, execute:
   ```bash
   npm run dev
   ```
4. The terminal will display an address (usually `http://localhost:5173/`). Hold Ctrl and click the link to open it automatically in your browser.

🎉 **Congratulations, the entire project is now running locally on your machine!**

---

### Email Configuration (Optional)

The system integrates a real email service for sending verification codes during the "Forgot Password" process. It defaults to using QQ Mail SMTP, but other email providers can be configured similarly.

**Steps:**
1. Log in to your QQ Mail → Settings → Accounts → Enable **POP3/SMTP service**, and generate an authorization code.
2. Open `platform/src/main/resources/application.properties` and configure the following properties:
   ```properties
   spring.mail.host=smtp.qq.com
   spring.mail.port=587
   spring.mail.username=YourQQEmail@qq.com
   spring.mail.password=YourAuthorizationCode
   ```
3. Restart the backend to send verification codes via a real email address.

**Fallback Mechanism**: If you choose not to configure the email settings, the verification code for password recovery will be directly printed in the **backend console** (search for 💌). Both methods will not affect the functionality demonstration.

> ⚠️ Do not commit the `application.properties` file containing real passwords to Git. The repository is already configured with `.gitignore` to ignore this file.

---

## Test Accounts

The database is pre-populated with three roles for a quick experience of all features:

| Role | Username | Password | Description |
|------|--------|------|------|
| 👑 Super Admin | `admin` | `123456` | Has full access to review resources, manage users, and view logs. |
| ✍️ Contributor | `guest01` | `123456` | Can submit posts, manage their own resources, and access some backend features. |
| 👀 Visitor | *Self-register* | *Self-set* | Can browse public resources, upvote, collect, and comment. |

> 💡 Visitors can browse public resources on the homepage without logging in, but interaction (commenting, etc.) requires an account. A birthday field is now available during registration.

---

## Key Implementations

### Real Email & Fallback Strategy

The "Forgot Password" feature is connected to a real email service (Spring Boot Mail + QQ SMTP). Upon correct configuration, users will receive a 6-digit verification code.
A **fallback strategy** is also implemented: when there are network issues or misconfigured email settings, the verification code will be printed in the backend console, ensuring the feature can be verified in any evaluation environment.

✅ This design fully complies with business logic and security validation standards, balancing commercial user experience with the stability required for course demonstrations.

### Permission Isolation & Auditing

- Strict three-tier role system (VIEWER / CONTRIBUTOR / ADMIN) protected by frontend route guards, conditional menu rendering, and backend interceptors.
- All sensitive operations (resource status changes, role modifications, etc.) are recorded in the `audit_log` table. The `Operator` field accurately records the actual user performing the action.

### Database Collation

To ensure compatibility across different MySQL versions, the collation in the SQL files has been unified to `utf8mb4_general_ci`. If previously downloaded backups mixed `utf8mb4_0900_ai_ci`, please manually replace or re-import the latest SQL file.

---

## FAQ

### 1. Database connection error when starting the backend?
- Verify that the username and password in `application.properties` are correct.
- Check if the MySQL service is running.
- Ensure the `heritage_db` database has been created and the SQL file is imported.

### 2. Frontend page loads, but backend API calls fail?
- Ensure the backend has started successfully (look for `Tomcat started on port 8080`).
- Check if the API base URL in `main.js` (or `.env` configuration) points to the correct backend address (e.g., `http://116.62.165.182:8080`).
- If blocked by CORS, ensure the backend `@CrossOrigin` annotations are correctly configured.

### 3. Email sending failed?
- Confirm the QQ Mail SMTP service is enabled and the authorization code contains no spaces.
- Campus networks may block port 587. Try using `spring.mail.port=465` and adding `spring.mail.properties.mail.smtp.ssl.enable=true`.
- If it still fails, the system will gracefully fall back to printing the verification code in the console without affecting core functionality.

### 4. Can a user infinitely upvote/downvote comments?
- Fixed in the latest version: Upvoting/downvoting acts as a "toggle". A user can only upvote or downvote once, and the icon will be highlighted based on the state.

### 5. How can I view the system audit logs?
- Log in with the admin account and navigate to the "System Audit Log Dashboard" in the sidebar. It supports filtering by Resource ID and Operation Type, recording the real username in the Operator field.

### 6. How do I stop frequent notifications?
- Go to your Profile page → Notification Preferences. You can toggle off review notifications, comment notifications, and system notifications.

---

## Future Optimization Plans

- [ ] Implement Spring Security or JWT for more secure authentication.
- [ ] Support local image uploads for resources (currently relies on network URLs).
- [ ] Add unit tests and automated API tests.
- [ ] Optimize frontend responsive design for mobile devices.
- [ ] Improve email templates (HTML styling).

---

## Contributing

Issues and Pull Requests are welcome.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

Distributed under the MIT License. You are free to use, modify, and distribute this project.

---

*Developed with ❤️ for CPT202 XJTLU.*
``