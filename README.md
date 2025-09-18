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

## ⚠️ Disclaimer

This project is built **for learning purposes only**.  
It simulates features of a digital wallet (like PayPal) including authentication, wallets, and card tokens.  

- ❌ Not PCI-DSS compliant  
- ❌ Not safe for production  
- ❌ Do not use with real card data  

All card details used in this project are **dummy test cards** (e.g., `4111 1111 1111 1111`) commonly provided by payment gateways for sandbox testing.

---

## 🔑 Authentication Flow

- **Register** → create a user account (Customer, Merchant, or Admin)  
- **Login** → receive a JWT access token  
- **Authorize** → pass JWT in the `Authorization: Bearer <token>` header for protected endpoints  

---

## 💳 Card Tokenization Flow (Simulated)

This project demonstrates **card tokenization logic** using **JWTs**:

1. **User signs in** → receives an auth token  
2. **User submits card info (test card)**  
3. **System generates a `CardToken` (JWT)** → containing masked card details  
4. **Future transactions** use this `CardToken` instead of raw card data  

✅ In real systems, card tokenization is handled by **payment gateways** (Stripe, PayPal, Adyen, etc.).  
This project only **mimics** that flow for educational purposes.

---

## 📂 Project Structure



## 🚀 Getting Started

1. Clone this repository  
   ```bash
   git clone https://github.com/mabrikado/sheshapay-api.git
   cd sheshapay-api
