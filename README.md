# E-commerce and Social Platform Microservices

## Overview

This project represents a highly scalable and fault-tolerant E-commerce and Social Platform, designed using a microservices architecture. It includes comprehensive features for user management, product and order processing, social interactions (posts, likes, comments), notifications, and advanced search capabilities. The platform emphasizes high availability, security, and performance, making it ideal for modern web applications.

## Table of Contents

- [Technologies](#technologies)
- [Architecture](#architecture)
- [Security](#security)
- [Observability and Monitoring](#observability-and-monitoring)
- [Microservices Overview](#microservices-overview)
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

The platform is built using a microservices architecture, where each service is responsible for a specific domain. Inter-service communication is handled through RESTful APIs, with Spring Cloud Gateway serving as the API Gateway. Identity management and security are managed by Keycloak, while Kafka is used for event-driven communication, such as notifications.

### Core Components

- **API Gateway**: Spring Cloud Gateway handles API routing, load balancing, and security.
- **Keycloak**: Provides centralized authentication, authorization, and SSO capabilities.
- **Reactive Programming**: Reactor enables non-blocking, asynchronous operations for high scalability.
- **Caching**: Redis is used for in-memory caching and session management, improving performance.
- **Search**: Elasticsearch supports fast, full-text search capabilities across multiple entities.

## Security

- **Keycloak Integration**: Implements centralized identity and access management, supporting SSO and social logins for enhanced security and user experience.
- **OAuth2**: Secures service-to-service communication with token-based authentication.
- **SSO**: Facilitates seamless Single Sign-On (SSO) with social login options through Keycloak.

## Observability and Monitoring

- **Prometheus**: Collects and stores real-time metrics from various services.
- **Grafana**: Provides real-time visualization of metrics through dashboards.
- **Loki**: Centralizes log aggregation and management for better troubleshooting.
- **Tempo**: Enables distributed tracing, helping to monitor and resolve performance issues.

## Microservices Overview

### User Service

- **Role**: Manages user profiles, authentication, and roles via Keycloak.
- **Key Features**: User registration, login, profile management, role-based access control.

### Product Service

- **Role**: Handles product listings, categories, and inventory management.
- **Key Features**: CRUD operations, Elasticsearch for search, inventory tracking, pagination, filtering, and sorting.

### Order Service

- **Role**: Manages order creation, processing, and payment integration.
- **Key Features**: Order management, stock verification, integration with Cart Service.

### Cart Service

- **Role**: Provides shopping cart management for users.
- **Key Features**: CRUD operations on cart items, order creation, Redis-based caching for performance.

### Post Service

- **Role**: Manages social interactions such as posts, likes, and comments.
- **Key Features**: CRUD operations, content moderation, pagination, filtering, and sorting.

### Notification Service

- **Role**: Handles real-time notifications for users.
- **Key Features**: Kafka-driven event notifications, preference management, service integration.

### File Service

- **Role**: Manages file uploads, storage, and retrieval.
- **Key Features**: Secure file handling, metadata management, integration with other services.

### Shop Service

- **Role**: Allows users to create and manage their shops.
- **Key Features**: Shop creation, product management integration, role-based access control.

### Search Service

- **Role**: Provides advanced search capabilities across platform entities.
- **Key Features**: Full-text search with Elasticsearch, scalable and efficient search operations.
