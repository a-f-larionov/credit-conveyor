# 🔐 Credit Conveyor – Loan Management

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen)](https://spring.io/projects/spring-boot)
[![JWT](https://img.shields.io/badge/JWT-0.12.6-orange)](https://jwt.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)](https://www.docker.com/)

Микросервис кредитов на **Spring Boot 3 + JWT**.  
Менеджмент кредитов, оплата

---

## 🧰 Стек
- Java 17, Spring Boot 3, Spring Security 6
- JWT (jjwt), PostgreSQL, Spring Data JPA
- Docker, Lombok, Maven

---

## 🚀 Настройка & Запуск
```bash
git clone https://github.com/a-f-larionov/credit-conveyor.git
cd credit-conveyor
cp .env.example .env
cp ./api-gateway/.env.example ./api-gateway/.env
cp ./credit-service/.env.example ./credit-service/.env
cp ./loan-management-service/.env.example ./loan-management-service/.env
docker-compose up -d --build