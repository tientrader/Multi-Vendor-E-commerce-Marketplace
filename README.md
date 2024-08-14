# E-commerce and Social Platform Microservices

## Overview

This project is a robust E-commerce and Social Platform built with a microservices architecture. It encompasses a range of functionalities including user authentication, shop & product management, order processing, social interactions (posts, likes, comments), notifications, and search capabilities. The system is designed for scalability, fault-tolerance, and advanced security, making it suitable for modern web applications.

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
  - [Search Service](#search-service)

## Technologies

- **Architecture**: Microservices
- **Back-end**: Java, Spring Boot, Spring Cloud, OpenFeign, Reactor, OAuth2, Kafka, Keycloak, Docker, Resilience4j
- **Data Storage**: MySQL, MongoDB, Elasticsearch, Redis
- **Observability**: Prometheus, Grafana, Loki, Tempo

## Architecture

The platform is structured around microservices, each handling distinct domain functionalities. Communication between services is managed through HTTP REST APIs, with Spring Cloud Gateway overseeing routing. Keycloak provides comprehensive identity and access management, while Kafka facilitates event-driven communication, such as sending notifications.

### Key Components

- **API Gateway**: Employs Spring Cloud Gateway for effective routing, load balancing, and security of API requests.
- **Keycloak**: Offers centralized identity management, handling authentication, authorization, SSO, and social logins.
- **Reactive Programming**: Utilizes Reactor to build scalable, non-blocking asynchronous operations within services.
- **Caching**: Redis enhances performance with in-memory caching and session management.
- **Search**: Elasticsearch delivers fast, scalable full-text search capabilities across entities.

## Security

- **Keycloak Integration**: Architected a robust identity management system with advanced SSO and social login capabilities, enhancing security and providing a seamless user experience.
- **OAuth2**: Ensures secure authorization for service access.
- **SSO**: Implements Single Sign-On (SSO) through Keycloak, supporting social logins (Google, Facebook) for a unified user experience.

## Observability and Monitoring

- **Prometheus**: Collects real-time metrics from various services.
- **Grafana**: Visualizes metrics data and offers real-time dashboards.
- **Loki**: Centralizes log management for effective aggregation and analysis.
- **Tempo**: Provides distributed tracing to monitor and debug performance issues.

## Microservices

### User Service

- **Description**: Manages user profiles, roles, and authentication via Keycloak.
- **Key Features**: User registration, login, profile management, and role-based access control.

### Product Service

- **Description**: Handles product listings, categories, and inventory.
- **Key Features**: CRUD operations for products and categories, integration with Elasticsearch for scalable search, and inventory management.

### Order Service

- **Description**: Manages order processing, payment integration, and order history.
- **Key Features**: Order creation, stock management with Product Service, and integration with Cart Service.

### Cart Service

- **Description**: Manages shopping cart functionality.
- **Key Features**: CRUD operations, integration with Order Service, and caching/session management with Redis.

### Post Service

- **Description**: Manages user-generated content including posts, likes, and comments.
- **Key Features**: CRUD operations, user interaction tracking, and content moderation.

### Notification Service

- **Description**: Handles real-time user notifications.
- **Key Features**: Event-driven notifications using Kafka, notification preferences management, and integration with other services.

### File Service

- **Description**: Manages file uploads and storage.
- **Key Features**: Secure file upload/download, metadata management, and integration with User and Product services.

### Shop Service

- **Description**: Manages shop creation and management by users.
- **Key Features**: CRUD operations for shops, integration with Product Service for shop-related product management, and permission management for shop owners.

### Search Service

- **Description**: Provides advanced search functionalities across various entities.
- **Key Features**: Full-text search capabilities using Elasticsearch, allowing efficient and scalable search operations for products, posts, and other entities.
