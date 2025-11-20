
The application provides a web-based operator interface for parsing various file formats (Fixed-width, Pipe-delimited, Semicolon-delimited) ensuring data integrity and fault tolerance.

## 🛠️ Technology Stack

* **Java 21**
* **Spring Boot 3.x**
* **Spring Batch** (Core ETL engine)
* **Spring Data JPA / Hibernate** (Database persistence)
* **PostgreSQL** (Target database)
* **Thymeleaf** (Web UI)

## 💻 How to Run

### Option 1: Docker (Recommended) 
The application is fully containerized. You don't need Java or PostgreSQL installed on your machine. 
1. **Build the project:** ```bash mvn clean package -DskipTests ``` 
2.  **Start the environment (App + DB):** ```bash docker-compose up --build ``` 
3. **Access the UI:** Open your browser at `http://localhost:8080` 

### Option 2: Local Development
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
