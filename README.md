# Four Leaves Shoe - Project Overview
- URL: https://shoeselling-fourleavesshoes.onrender.com
- This project aims to replicate the functionality of an e-commerce website. It offers a comprehensive suite of features that cater to both customers and store managers.

# Technologies Used

- Front-end: Thymeleaf (using template bootstrap)
- Backend: Spring Boot
- Database: SQL

## Features

### For Customers
- **Store Information**: Provides details about the store.
- **Order Placement**: Allows customers to place orders for their desired products.
- **Payment Gateway**: Facilitates payment transactions for purchases.

### For Store Management
- **User Information Management**: Handles the data related to store users.
- **Product Management**: Involves adding, updating, and removing products.
- **Order Management**: Tracks and manages customer orders.
- **User Role Assignments**: Administers the roles and permissions of users on the website.

## Admin Account

**Username**: doananh0100  
**Password**: Duc2112002@

## Main Features

### Design and User Interface
- Built according to the MVC model and designing UI/UX for the application.
- ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/a12210f8-4a25-42f4-9170-506571f3f747)

### E-commerce Functionalities
- Provided functionalities of an e-commerce website, including viewing items, managing the shopping cart, order placing for customers, and shop management for employees.

### Security
- Utilized Spring Security and JWT tokens for managing authentication and authorization.
- Secured the web application against attacks like CSRF, Brute Force, Clickjacking, XSS.

### Payment Integration
- Payment is handled through Momo e-wallet and Vnpay payment gateway.

### Integration with Other Services
- Working with Gmail, Excel, and PDF applications to manage orders for customers and data management for the store.
- Wrote APIs for Android app integration.

### Social Login and Communication
- Used OAuth2 for logging in with Google, Facebook (under development).
- Used Twilio to send SMS messages through a phone number for verification support (under development).

# ===================================================================================================================

## 1. Provided functionalities of an e-commerce website, including viewing items, managing the shopping cart, order placing for customers, and shop management for employees.

### 1.1. Account registration and login
- Registration
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/44cac01e-4a48-4e31-a2e4-35bb4dce51b7)

- Register successfully
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/81e3651e-b642-4ca3-b671-d324e229c314)

- Login
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/76a69b20-ea21-4a29-a88f-ac4caeece27f)

- Login successfully
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/28d5c7bc-cc4f-4492-8dcc-67cb8b27144b)


### 1.2. View homepage, products, product details
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/649e918b-01c5-4766-95c3-945f24b1aacf)
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/78a28ae4-6295-409b-a970-4606099c1d07)
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/51e3d658-0ab0-45a9-b908-e2853010a049)

### 1.3. View, add, remove products from the shopping cart
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/bfe2bd94-dda7-441e-91ef-065a8ef23873)

### 1.4. Place Cash on Delivery (COD) orders
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/1da2c500-4693-40ba-a68c-5f3c932f48d5)
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/87d43512-0fb6-4de5-8ad8-e13dafcd3ac1)

## 2. Payment is handled through Momo e-wallet and Vnpay payment gateway.

### 2.1. Payment through Momo e-wallet
![chonPhuongThucThanhToan](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/33abae17-62af-4195-acee-5e37726ed427)
![trangThanhToanMomo](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/4ddf59cb-08c2-44fe-943a-f94108b8750d)

- At the homepage of Momo's application
  ![momoap_trangChu](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/17d5da92-6362-4b37-b3f7-4a8758f03780)
  ![momoapp_thanhToanThanhCong](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/ca0e5fd5-1f6c-49ec-81e2-879097cb90d3)
  ![momoapp_thongTinDonHang](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/69c3c9bc-a7e4-4899-977f-4f9cbd74152a)

- After check out successfully, the webpage is reset.
  ![thanhToanMomoThanhCong](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/19f16a25-1eb1-41ef-b327-348b9ff7f5dc)
  ![trangThaiOrder](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/b7746bf4-a866-4fef-ada7-6652f76d69d1)

### 2.2. Payment through Vnpay payment gateway
![chonPhuongThucThanhToan](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/6ac7a1e3-75c6-49be-b608-3c26518a95e4)
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/f0b1959e-a6c5-4051-9d36-0c2fa3003ebf)
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/f153790b-b772-44de-ac79-2a5f26232f2d)
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/8c740ddd-1ae5-4d29-bdbd-5adcbb71713c)
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/67947bcb-cccb-4413-8e8e-3af923a3521d)

## 3. Working with Gmail, Excel, and PDF applications to manage orders for customers and data management for the store.

### 3.1. Working with Gmail
- Change password
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/dce95b65-8b18-4d8c-b594-225c3522287c)
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/260b303f-5c80-48e8-b90f-d724d076f896)

- Send invoices after purchasing
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/74141053-0ceb-459f-8ea7-8e6e59ff4b7e)

### 3.2. Working with Excel (CSV) - under development

- Export, Import CSV files in store management for employees

### 3.3. Working with PDF - under development

- Print invoices in PDF format

## 4. Wrote APIs for Android app integration.
- Example of getting product's list api
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/c6be7ed9-6874-4ce8-9146-4f0a503b3c36)

- Example of getting staff's list api
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/34a04b7c-32f4-458b-bd47-5821c54cdd65)


## 5. Utilized Spring Security and JWT tokens for managing authentication and authorization.

### 5.1. JWT tokens and permissions
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/6f8c3ec7-f06c-45e6-9a11-48639e2da7d8)
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/ecdb5f49-59fa-489a-94bf-a311627eba71)
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/e2549f2f-50c5-4578-8395-58ecddd0dd1c)

### 5.2. Incorrect login
![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/6c3bef7b-83a1-4464-8237-d6b4bf347c50)

### 5.3. Access denied when trying to access unauthorized areas (before and after login)
- Without login: Let's access this url without login: http://localhost:3030/admin/user. Here is the result
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/3c77ed53-fb0e-4bff-97d5-814b228c7afe)

- After login
  ![image](https://github.com/VoVanDuc20110635/DoAnXinViec_fourleavesshoes/assets/116067030/7936d63b-e965-48b5-9af5-d28e6699cd3a)

## 6. Secured the web application against attacks like CSRF, Brute Force, Clickjacking, XSS.

- Protect against CSRF (Cross-Site Request Forgery) attacks: FourLeavesShoes uses CSRF tokens to validate user requests and prevent CSRF attacks.
- Protect against Brute Force attacks: FourLeavesShoes applies a policy to limit the number of failed login attempts and waiting time between logins to prevent Brute Force attacks.
- Protect against Clickjacking attacks: FourLeavesShoes uses HTTP header frame options to prevent Clickjacking attacks.
- Protect against XSS (Cross-Site Scripting) attacks: FourLeavesShoes applies input filtering and output encoding to prevent XSS attacks.

## 7. Used OAuth2 for logging in with Google, Facebook (under development).

## 8. Used Twilio to send SMS messages through a phone number for verification support (under development).
