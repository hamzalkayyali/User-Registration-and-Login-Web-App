# 📝 User Registration and Login Web App

A simple Java web application that allows users to **sign up**, **log in**, and manage their passwords.  
When a user logs in successfully, the homepage displays a list of all registered users.

---

## 🛠️ Tech Stack
- **Backend**: Java Servlets + JSP  
- **Database**: Oracle XE  
- **Server**: Apache Tomcat 9  
- **IDE**: Eclipse  
- **Build Tool**: Manual / Eclipse (no Maven)  

---

## 🚀 Features
- ✅ User Registration (sign up with username + password)  
- ✅ User Login (authenticate with Oracle DB)  
- ✅ Show all registered users on the homepage after login  
- ✅ Oracle Database integration using JDBC  
- ✅ **Password Management**:
  - Passwords must be **complex** (uppercase, lowercase, digit, special character)  
  - Users **cannot reuse any of their last 4 passwords**  
  - Passwords are **hashed** before storing (never stored in plain text)  
  - **Reset Password** functionality (for users who forgot their password)  
  - **Force Password Change** if a password **expires after 90 days**  

---

## 🎥 Demo
Watch the app in action here: [Demo Video](https://youtu.be/2sMCAvb8YcU)
