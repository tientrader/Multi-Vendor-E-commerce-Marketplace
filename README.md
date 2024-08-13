# E-commerce and Social Platform Microservices

## Overview

This project is an E-commerce and Social Platform built with a microservices architecture. It supports a wide range of functionalities, including user authentication/authorization, shop & product management, order processing, social interactions (posts, likes, comments), notifications, and search capabilities. The system is designed to be scalable, fault-tolerant, and secure, making it suitable for modern web applications.

## Table of Contents

- [Technologies](#technologies)
- [Architecture](#architecture)
- [Security](#security)
- [Observability and Monitoring](#observability-and-monitoring)
- [Microservices](#microservices)
  - [User Service](#user-service)
  - [Product Service](#product-service)
  - [Order Service](#order-service)
  - [Cart Service](#cart-service)
  - [Post Service](#post-service)
  - [Notification Service](#notification-service)
  - [File Service](#file-service)
  - [Shop Service](#shop-service)

## Technologies

- **Architecture**: Microservices
- **Back-end**: Java, Spring Boot, Spring Cloud, OpenFeign, Reactor, OAuth2, Kafka, Keycloak, Docker, Resilience4j
- **Data Storage**: MySQL, MongoDB, Elasticsearch, Redis
- **Observability**: Prometheus, Grafana, Loki, Tempo

## Architecture

The system is composed of multiple microservices, each responsible for a specific domain within the application. These microservices communicate with each other primarily through HTTP REST APIs, managed by Spring Cloud Gateway. Keycloak is employed for identity and access management, enabling secure authentication and authorization across the system. Kafka is used for event-driven communication between services, such as sending notifications after certain actions.

### Key Components

- **API Gateway**: Manages routing, load balancing, and security for incoming API requests using Spring Cloud Gateway.
- **Keycloak**: Centralized identity management solution, handling authentication, authorization, SSO, and social logins.
- **Reactive Programming**: Utilized in services that require non-blocking, asynchronous processing for scalability.
- **Caching**: Redis is used to cache data and manage user sessions, enhancing system performance.
- **Search**: Elasticsearch provides fast, scalable full-text search capabilities across entities like products and posts.

## Security

- **Identity and Access Management**: Keycloak is used to manage user identities, roles, and permissions across all services.
- **OAuth2**: Provides secure authorization for accessing services.
- **SSO**: Single Sign-On (SSO) is implemented via Keycloak, with support for social logins (Google, Facebook).

## Observability and Monitoring

- **Prometheus**: Collects metrics from various services for monitoring.
- **Grafana**: Visualizes metrics data and provides real-time dashboards.
- **Loki**: Centralized log management and analysis.
- **Tempo**: Distributed tracing for performance monitoring and debugging.
## Microservices

### User Service

- **Description**: Manages user profiles, roles, and authentication via Keycloak.
- **Key Features**:
  - User registration and login
  - Profile management
  - Role-based access control (RBAC)

### Product Service

- **Description**: Handles product listings, categories, and inventory management.
- **Key Features**:
  - CRUD operations for products and categories
  - Integration with Elasticsearch for fast and scalable product search
  - Inventory management
  - Integration with Order Service to manage stock levels

### Order Service

- **Description**: Manages order processing, payment integration, and order history.
- **Key Features**:
  - Order creation, tracking, and cancellation
  - Payment processing (integrated with third-party payment services like PayPal and Stripe)
  - Integration with Product Service for stock management
  - Interaction with Cart Service to create orders from user carts

### Cart Service

- **Description**: Handles shopping cart management for users.
- **Key Features**:
  - CRUD operations for shopping carts
  - Integration with Order Service for seamless order creation
  - Caching and session management using Redis

### Post Service

- **Description**: Manages user-generated content, including posts, likes, and comments.
- **Key Features**:
  - CRUD operations for posts, likes, and comments
  - User interaction tracking and content moderation

### Notification Service

- **Description**: Handles real-time notifications for users.
- **Key Features**:
  - Event-driven notification system using Kafka
  - Management of notification preferences
  - Integration with other services to send notifications based on user activity

### File Service

- **Description**: Manages file uploads and storage for user profiles and product images.
- **Key Features**:
  - Secure file upload and download
  - Metadata management for files
  - Integration with User and Product services

### Shop Service

- **Description**: Manages the creation and management of shops by users.
- **Key Features**:
  - CRUD operations for shops
  - Integration with Product Service for categorizing products under specific shops
  - Permission management for shop owners, ensuring only authorized users can manage shops
