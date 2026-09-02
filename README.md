# 🚀 Task Management System

> A secure full-stack task and issue management platform built with Java and Spring Boot for organizing work, users, workflows, sprints, and reporting.

## 📌 Overview

The **Task Management System** is a full-stack application designed to support software-development-style project coordination. It brings authentication, role-based access, issue and task management, workflow tracking, sprint organization, attachments, and reporting into one application.

The project uses a layered backend architecture to keep responsibilities separated and make the application easier to maintain and extend.

## ✨ Highlights

| Area | Capabilities |
|---|---|
| 🔐 Authentication | Registration, login, JWT-based authentication, protected APIs |
| 👥 Authorization | Role-based access control and endpoint protection |
| 📋 Task & Issues | Create, update, assign, prioritize, and track work items |
| 🔄 Workflow | Status management and workflow transition tracking |
| 🏃 Sprints | Sprint creation, assignment, backlog handling, and progress tracking |
| 📊 Reporting | Task progress, sprint reporting, and analytics-oriented views |
| 📎 Attachments | File uploads with Cloudinary integration |
| ✅ Validation | Input validation and global exception handling |

## 🏗️ Architecture

```text
Client / Frontend
       │
       │ HTTP / REST
       ▼
┌──────────────────────┐
│ Controller Layer     │
├──────────────────────┤
│ Service Layer        │
├──────────────────────┤
│ Repository Layer     │
├──────────────────────┤
│ Entity / DTO Layer   │
├──────────────────────┤
│ Security Layer       │
└──────────┬───────────┘
           │
           ▼
      MySQL Database
```

## 🧱 Major Modules

- **Authentication & Security** — JWT authentication, Spring Security, authorization rules
- **User Management** — users, roles, and access control
- **Issue Management** — task/issue lifecycle and assignment
- **Workflow Management** — status transitions and progress flow
- **Sprint Management** — sprint and backlog organization
- **Kanban Board** — board-oriented task visualization
- **Reporting** — progress and analytics capabilities
- **Attachments** — file storage and attachment handling

## 🛠️ Tech Stack

### Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate ORM
- Maven

### Database & Storage

- MySQL
- Cloudinary

### Frontend

- HTML
- CSS
- JavaScript

### Security

- JWT authentication
- Role-based authorization
- Protected REST endpoints
- Input validation and centralized exception handling

## 🔗 API Areas

The REST API is organized around the application's main domains:

- Authentication
- Users
- Issues / Tasks
- Workflows
- Sprints
- Reports
- Attachments

## 🚀 Getting Started

### 1. Clone

```bash
git clone https://github.com/prashantpiyush1111/task-management-system.git
cd task-management-system
```

### 2. Configure MySQL

Create a database and configure the application's datasource using your local environment/configuration.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/task_management
spring.datasource.username=root
spring.datasource.password=your_password
```

> Keep local credentials and secrets out of version control.

### 3. Run

```bash
mvn spring-boot:run
```

## 🔐 Security Notes

The application uses Spring Security and JWT-based authentication to protect REST resources. Authorization is applied through role-based access rules so protected operations are available only to permitted users.

## 📈 Project Focus

This project demonstrates practical experience with:

- REST API design
- Spring Boot application architecture
- Authentication and authorization
- JPA/Hibernate persistence
- Relational database modeling
- Modular service/repository design
- Validation and exception handling

## 🗺️ Future Enhancements

Potential extensions include richer real-time collaboration, notification workflows, advanced analytics, and additional deployment options.

## 👨‍💻 Author

**Prashant Maurya**  
Java Full Stack Developer

GitHub: [@prashantpiyush1111](https://github.com/prashantpiyush1111)

## 📄 License

See the repository license for current usage terms.
