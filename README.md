# 🏦 SheshaPay API

**SheshaPay API** — *Fast. Secure. Connected.*  
A **Spring Boot REST API** for powering a digital wallet and payment platform.  

---

## 📖 Overview

This project is the **backend service** for **SheshaPay**, a digital wallet inspired by PayPal.  
It provides secure REST APIs for **user registration, authentication, wallet operations, and transaction management**.  

The backend is built as a **Spring Boot monolith** with role-based access and a PostgreSQL database.  

---

## ✨ Features

- **User Management**: register, login, JWT authentication  
- **Wallet Operations**: top-up, transfer, withdraw  
- **Transactions**: logs for deposits, withdrawals, payments, transfers  
- **Admin Controls**: approve users, monitor activity  
- **Security**: JWT, role-based authorization  

---

## 🗄️ Database (Simplified)

**Tables included in this project:**
- `USER` → stores customers & admins  
- `WALLET` → user balances  
- `TRANSACTION` → all transfers, deposits, withdrawals  
- `FUNDING_SOURCE` → linked bank accounts/cards  

---

## 🏗️ Tech Stack

- **Backend Framework**: Spring Boot  
- **Database**: PostgreSQL (H2 optional for tests)  
- **ORM**: Spring Data JPA  
- **Security**: Spring Security, JWT  
- **Build Tool**: Maven  
- **Testing**: JUnit, Mockito  

---

## 🚀 Getting Started

1. Clone this repository  
   ```bash
   git clone https://github.com/mabrikado/sheshapay-api.git
   cd sheshapay-api
