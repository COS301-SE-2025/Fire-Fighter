# FireFighter Access Management Platform – Backend

This repository contains the Spring Boot / Maven scaffolding for the FireFighter Access Management Platform. It provides a minimal starting point; you’ll add entities, services, controllers, repositories, security, etc., on top of this foundation.

---

## Table of Contents

1. [Prerequisites](#prerequisites)  
2. [Project Structure](#project-structure)  
3. [Getting the Code](#getting-the-code)  
4. [Building & Running Locally](#building--running-locally)  
5. [Adding New Dependencies](#adding-new-dependencies)  
   - [PostgreSQL & Spring Data JPA](#postgresql--spring-data-jpa)  
   - [Spring Security](#spring-security)  
6. [Configuration & Profiles](#configuration--profiles)  
7. [Packaging & Deployment](#packaging--deployment)  
8. [Common Maven Commands](#common-maven-commands)

---

## Prerequisites

Before you start, ensure you have:

- **Java 17 JDK**  
- **Maven 3.6+** (or use the included Maven Wrapper)  
- **Git**  
- **PostgreSQL 10+** (for local/dev database)  
- An IDE or editor with Java/Spring support (IntelliJ, VS Code, Eclipse)

On Ubuntu (WSL), you can install these with:

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven git postgresql-client
```

---

## Project Structure

```
firefighter-platform/
├── mvnw*                ← Maven wrapper scripts
├── pom.xml              ← Maven build descriptor
├── README.md            ← (This file)
└── src/
    ├── main/
    │   ├── java/com/apex/firefighter/
    │   │   ├── FirefighterApplication.java    ← Application entry point
    │   │   └── … your code: controllers, services, entities, repos
    │   └── resources/
    │       ├── application.properties          ← Default config
    │       └── application-dev.properties      ← Dev-only overrides
    └── test/
        └── java/com/apex/firefighter/…        ← Unit and integration tests
```

---

## Getting the Code

```bash
git clone https://github.com/your-org/firefighter-platform.git
cd firefighter-platform
```

---

## Building & Running Locally

You can use the Maven Wrapper (`./mvnw`) or your system-wide `mvn`. From the project root:

```bash
# Compile + run in one step
./mvnw spring-boot:run

# OR package, then run the JAR
./mvnw clean package
java -jar target/firefighter-platform-0.0.1-SNAPSHOT.jar
```

The application will start on **port 8080** by default. Verify:

```bash
curl http://localhost:8080/actuator/health
# OR your own /api/health endpoint
```

---

## Adding New Dependencies

All dependencies are managed in **pom.xml**. To add a new library:

1. Open **pom.xml**.
2. Locate the `<dependencies>` section.
3. Add your new `<dependency>…</dependency>`.
4. Save and rebuild:

   ```bash
   ./mvnw clean package
   ```

### PostgreSQL & Spring Data JPA

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>
```

### Spring Security

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

After adding, rebuild and restart your app so that the new dependencies are on the classpath.

---

## Configuration & Profiles

Configuration lives under `src/main/resources`:

* **application.properties**
  Contains your default settings.

* **application-dev.properties**
  Anything in here (e.g. embedded H2, local DB creds) applies when you run with `-Dspring.profiles.active=dev`.

A typical **PostgreSQL** setup in `application.properties`:

```properties
# Server
server.port=8080

# Datasource
spring.datasource.url=jdbc:postgresql://localhost:5432/firefighter
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASS

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Override any of these per-environment via system properties, env vars or profile-specific files.

---

## Packaging & Deployment

1. **Build the executable JAR:**

   ```bash
   ./mvnw clean package
   ```

2. **Deploy**
   Copy `target/firefighter-platform-*.jar` to your server and run:

   ```bash
   java -jar firefighter-platform-0.0.1-SNAPSHOT.jar \
     --spring.profiles.active=prod
   ```
3. **Repositories Unit testing:**
  ```bash
  ./mvnw test
  ```

You can containerise with Docker later by adding a simple `Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jre
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

---

## Common Maven Commands

| Task                  | Command                      |
| --------------------- | ---------------------------- |
| Run the app           | `./mvnw spring-boot:run`     |
| Compile & package     | `./mvnw clean package`       |
| Run tests             | `./mvnw test`                |
| Skip tests            | `./mvnw package -DskipTests` |
| View dependency tree  | `./mvnw dependency:tree`     |
| Generate project site | `./mvnw site`                |

---
=======
<h1 align="center" id="title">FireFighter</h1>

<p align="center"><img src="https://socialify.git.ci/COS301-SE-2025/Fire-Fighter/image?font=Inter&amp;issues=1&amp;name=1&amp;pattern=Circuit+Board&amp;pulls=1&amp;stargazers=1&amp;theme=Auto" alt="project-image"></p>

<p id="description">FireFighter Access Management Platform is a secure auditable system designed to streamline emergency privilege escalations within BMW Group’s IT Hub. It enables authorised support engineers to request time-limited elevated access tied to incident tickets through a responsive front-end complete with real-time notifications and a tamper-evident audit trail for full compliance. This repository contains the initial front-end implementation—built with Angular Ionic and Firebase Authentication—with back-end services and data persistence to follow in future releases.</p>

<p align="center"><img src="https://img.shields.io/badge/Angular-DD0031?logo=angular&amp;logoColor=white" alt="shields"><img src="https://img.shields.io/badge/Ionic-3880FF?logo=ionic&amp;logoColor=white" alt="shields"><img src="https://img.shields.io/badge/Capacitor-000000?logo=capacitor&amp;logoColor=white)" alt="shields"><img src="https://img.shields.io/badge/Maven-C71A36?logo=apache-maven&amp;logoColor=white" alt="shields"><img src="https://img.shields.io/badge/Spring_Boot-6DB33F?logo=springboot&amp;logoColor=white" alt="shields"><img src="https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&amp;logoColor=white" alt="shields"><img src="https://img.shields.io/badge/Tailwind_CSS-06B6D4?logo=tailwind-css&amp;logoColor=white" alt="shields"></p>

  
  
<h2>🧐 Features</h2>

Here are our current features:

*   User Authentication
*   Mock Dashboard
*   Mock Ticket Creation
*   Mock Notification Viewing
*   Theming

  
  
<h2>💻 Built with</h2>

Technologies used in the project:

*   Angular
*   Ionic Capacitor
*   Google Firebase
*   PostgreSQL
*   Maven
*   SpringBoot

## Team Members

Team Apex brings together five final-year computer science students with complementary skills to deliver the FireFighter platform:

| **Name**                  | **Role & Expertise**                                                                                      | **LinkedIn**                                                                                                            |
| ------------------------- | --------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| Zander van Schoor         | Full-stack developer specialising in Angular, Ionic Capacitor, Spring Boot and real-time notifications    | [linkedin.com/in/zander-van-schoor](https://www.linkedin.com/in/zander-van-schoor/)                                     |
| Sebastiaan van den Berg   | Backend engineer & COS 301 project lead skilled in Java, C++, Node.js, networking architecture & security | [linkedin.com/in/sebastiaan-van-den-berg-67774b216](https://www.linkedin.com/in/sebastiaan-van-den-berg-67774b216/)     |
| Franco van der Westhuizen | API designer experienced with scalable RESTful services in Java, C++, Python and optimized data models    | [linkedin.com/in/franco-van-der-westhuizen-14287b17b](https://www.linkedin.com/in/franco-van-der-westhuizen-14287b17b/) |
| Lukas Swanepoel           | Software architect focused on structured, maintainable code (Java, C++, JavaScript) and design patterns   | [linkedin.com/in/lukas-swanepoel-533604356](https://www.linkedin.com/in/lukas-swanepoel-533604356/)                     |
| Mark Herbst               | Backend specialist proficient in Java, C++ and server architectures, with full-stack flexibility          | [linkedin.com/in/mark-herbst-397b9730b](https://www.linkedin.com/in/mark-herbst-397b9730b/)                             |
