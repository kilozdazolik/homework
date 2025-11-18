The application provides a web-based operator interface for parsing various file formats (Fixed-width, Pipe-delimited, Semicolon-delimited) ensuring data integrity and fault tolerance.

## 🛠️ Technology Stack

* **Java 21**
* **Spring Boot 3.x**
* **Spring Batch** (Core ETL engine)
* **Spring Data JPA / Hibernate** (Database persistence)
* **PostgreSQL** (Target database)
* **Thymeleaf** (Web UI)

## 💻 How to Run

### Prerequisites
1.  **Java 21** installed.
2.  **PostgreSQL** server running locally.
3.  An empty database created named `homework`.

### Configuration
Before running, verify your database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/homework
spring.datasource.username=postgres
spring.datasource.password=changeme
