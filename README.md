E-Commerce Microservices Project

Overview

This is a comprehensive e-commerce application built using a microservices architecture. The project utilizes a range of modern technologies to ensure efficient, secure, and scalable operations.

Technologies

	•	Architecture: Microservices
	•	Back-end:
	•	Java
	•	Spring Framework
	•	OpenFeign
	•	Reactive Programming (Reactor)
	•	OAuth2
	•	Kafka
	•	Firebase
	•	KeyCloak
	•	Docker
	•	Prometheus
	•	Grafana
	•	Loki
	•	Tempo
	•	Resilience4j
	•	Databases:
	•	MySQL
	•	MongoDB

 ![identity-service (3)](https://github.com/user-attachments/assets/8cd67bb8-ba35-40b5-a700-e926f02e0b66)

Key Contributions

Security

	•	Keycloak Integration: Centralized identity management with Single Sign-On (SSO) and social login integrations.
	•	Robust Security Protocols: Efficient database management and proactive monitoring for production environments.

API Gateway and Reactive Operations

	•	Spring Cloud Gateway: Efficient API traffic routing and management.
	•	Reactor: Asynchronous and non-blocking operations for improved microservice performance.

Microservices Development

	•	Elasticsearch: Fast full-text search and scalable analytics capabilities.
	•	MySQL: Ensures data integrity across Identity, Order, and Payment services.
	•	Redis: Optimized performance with caching and session management, particularly for the Cart service.
	•	MongoDB: Flexible document storage for Product, Post, Notification, and File services.

Exception Handling

	•	Centralized Error Management: Consistent error responses with custom exceptions for specific scenarios.
	•	Validation and Access Control: Effective handling of validation errors and access control.

Observability and Fault Tolerance

	•	Tempo: Comprehensive distributed tracing for monitoring microservices interactions.
	•	Prometheus & Grafana: Real-time metrics and dashboards for system monitoring.
	•	Loki: Centralized log management and analysis.
	•	Resilience4j: Circuit breakers and retry mechanisms for enhanced fault tolerance.

Deployment

	•	Docker: Containerized applications with management via Docker Hub.
	•	AWS EC2: Deployed and scaled applications for enhanced performance and availability.
	•	CI/CD Pipelines: Streamlined integration, delivery, and operational monitoring processes.
