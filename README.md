# 🚀 Social-Marketplace

**Social-Marketplace** is a robust two-sided marketplace platform that seamlessly connects buyers and sellers through a sophisticated online system. Buyers can explore and purchase diverse products or services from a wide array of vendors, while sellers can effectively showcase their offerings to potential customers. This project is meticulously designed to enhance user experience, minimize latency, and incorporate modern security features, thereby fostering trust and engagement within the platform.

Additionally, **Social-Marketplace** includes a comprehensive suite of features for order management, payment processing, and integration of various transaction methods. The platform serves not only as a connection channel but also as a catalyst for online business activities, driving revenue growth for all participants.

---

## 📚 Table of Contents
1. [Project Architecture](#project-architecture)
   - [Microservices Architecture](#microservices-architecture)
   - [Event-Driven Architecture](#event-driven-architecture)
2. [Technologies Used](#technologies-used)
   - [Java & Spring Framework](#1-java--spring-framework)
   - [Databases](#2-databases)
   - [Identity and Access Management](#3-identity-and-access-management)
   - [Service Communication](#4-service-communication)
   - [Payment Management](#5-payment-management)
   - [Monitoring and Logging](#6-monitoring-and-logging)
   - [Distributed Tracing](#7-distributed-tracing)
   - [Security and Performance](#8-security-and-performance)
3. [Key Contributions](#key-contributions)
4. [Conclusion](#conclusion)

---

## 🏗 Project Architecture

### Microservices Architecture

- **Separation of Services:**
  Each component of the system is designed as an independent microservice with specific responsibilities. Key microservices include user management, product catalog, shopping cart, order processing, payment processing, notification, and analytics services. This architecture enhances maintainability and scalability, allowing each component to be updated or scaled independently.

- **Fault-Tolerant Design:**
  In a microservices architecture, the failure of one service does not impact the operation of others, minimizing system downtime. For example, if the cart service encounters an issue, users can still log in and browse products seamlessly.

- **Simplified Maintenance and Upgrades:**
  Individual microservices can be updated without disrupting the overall system, allowing the development team to enhance performance, add features, or fix bugs quickly without affecting end users.

### Event-Driven Architecture

- **Utilizing Kafka as the Event Coordinator:**
  Apache Kafka serves as the event bus, facilitating communication between microservices. Events such as “new order” or “user registration” are relayed to relevant services, such as payment processing and notification services. This approach allows asynchronous event handling, ensuring services operate independently and efficiently.

- **High Scalability and Reliability:**
  Kafka is capable of handling millions of events daily, ensuring timely responses to user-seller interactions. Its log-based persistence allows for data recovery and guarantees that no event data is lost.

---

## 🛠 Technologies Used

### 1. Java & Spring Framework

- **Java:**
  Java is the primary language for building microservices, leveraging its multithreading capabilities and high performance. It provides a robust environment for scalable and maintainable solutions, particularly for services handling high transaction volumes like payments and orders.

- **Spring Framework:**
  - **Spring Boot:** Enables rapid microservice development with minimal configuration, significantly reducing boilerplate code.
  - **Spring WebFlux:** Facilitates the creation of asynchronous APIs through reactive programming, ensuring system responsiveness under high loads.
  - **Spring Security:** Manages security across microservices, offering authentication and authorization features. Key integrations include Single Sign-On (SSO) and JSON Web Token (JWT) authentication.
  - **Spring Data JPA:** Streamlines database interactions through repositories, simplifying data retrieval and CRUD operations.
  - **Spring Cloud:** Provides essential tools for managing and routing microservices, enhancing scalability and flexibility in API development.

### 2. Databases

- **MySQL:**
  MySQL ensures data consistency through ACID properties, allowing critical transactions to be processed accurately. It is configured with replication and sharding to handle high query volumes without performance degradation.

- **MongoDB:**
  MongoDB is employed for unstructured data storage, such as product information and user reviews, providing a flexible and scalable NoSQL solution.

- **Redis:**
  Redis acts as a caching solution to alleviate load on MySQL and MongoDB, accelerating data access and reducing the number of database queries. It supports publisher-subscriber patterns for real-time inter-service communication.

- **AWS S3:**
  AWS S3 is utilized for storing large volumes of unstructured data (e.g., images and videos), providing a highly durable and scalable storage solution.

### 3. Identity and Access Management

- **Keycloak:**
  - **Single Sign-On (SSO):** Users can log in once to access multiple services, with support for OAuth 2.0 and OpenID Connect.
  - **User Management:** Simplifies registration, email verification, and password recovery processes.
  - **Role-Based Access Control (RBAC):** Ensures that only authorized users can access specific features, enhancing security for sensitive operations.

- **JWT (JSON Web Tokens):**
  JWT secures API communications with authentication tokens, mitigating CSRF attacks and enhancing data security.

### 4. Service Communication

- **OpenFeign:**
  OpenFeign simplifies inter-microservice communication by automatically creating REST client proxies, reducing boilerplate code and enhancing developer productivity.

- **Kafka:**
  Kafka facilitates asynchronous communication between services, ensuring safe storage and distribution of messages.

### 5. Payment Management

- **Stripe:**
  Stripe provides comprehensive payment processing APIs that support various payment methods and currencies. Its webhook integration enables real-time transaction tracking.

### 6. Monitoring and Logging

- **Prometheus & Grafana:**
  These tools collect performance metrics for analysis and visualization, enabling real-time monitoring of system health.

- **Loki:**
  Loki serves as a centralized logging system, aggregating logs from microservices for efficient diagnosis and resolution of issues.

### 7. Distributed Tracing

- **Tempo:**
  Tempo allows for tracking the flow of requests through the system, helping to identify bottlenecks and optimize performance.

### 8. Security and Performance

- **Resilience4j:**
  - **Circuit Breaker:** Prevents requests to unavailable services, maintaining system stability.
  - **Retry Mechanism:** Automatically retries failed requests, enhancing reliability.
  - **Rate Limiter:** Controls the rate of requests to prevent system overload and ensure fair resource allocation.

- **Gzip Compression:**
  Gzip reduces the size of API response payloads, speeding up load times and minimizing bandwidth usage.

---

## 💡 Key Contributions

- **Identity and Access Management:** 
  Integrated Keycloak with Spring Security to implement SSO, user registration, social login, email verification, and password recovery. Rolled out role-based access control (RBAC) using JWT, enhancing security with OAuth 2.0 and OpenID Connect.

- **API Performance:**
  Improved API Gateway performance by implementing non-blocking JWT decoding and efficient routing with Spring Cloud Gateway and Reactor. Utilized asynchronous programming with Kafka and CompletableFuture to reduce response times by approximately 95%, and applied Gzip compression to decrease payload sizes by around 90%.

- **Data Management:**
  Optimized system performance using Redis caching to enhance response times and reduce database load. Leveraged MongoDB for scalable NoSQL solutions and MySQL for data integrity. Designed a cost-effective and scalable image storage solution with AWS S3.

- **Resilience and Monitoring:**
  Enhanced system resilience through Resilience4j circuit breakers, retries, fallbacks, and rate limiting. Implemented real-time monitoring with Prometheus and Grafana, centralized logging with Loki, and distributed tracing with Tempo.

- **Payment Integration:**
  Developed a comprehensive payment system using Stripe, incorporating charge functionalities for efficient order payments and subscription features for premium upgrades, thereby providing users with additional benefits and seamless transaction experiences.

---

## 🏁 Conclusion

**Social-Marketplace** stands as a testament to modern software architecture principles, emphasizing scalability, performance, and security. By leveraging a suite of powerful technologies, the platform not only meets current demands but is also poised for future growth and enhancement. Through continuous integration of cutting-edge features and a commitment to user experience, **Social-Marketplace** aims to redefine the online marketplace landscape.
