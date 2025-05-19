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