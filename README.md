# Sheet Cell Collaborative Application

**![Sheet_Cell_Banner](https://github.com/user-attachments/assets/9a983ab6-cd39-48dc-b16e-730f83224f74)**  

---

## Overview

Sheet Cell is a collaborative client-server desktop application built in Java using JavaFX, providing an Excel-like spreadsheet environment for real-time data manipulation, analysis, and visualization. Designed to support concurrent users, it includes real-time chat and version control, making it ideal for teams working on shared sheets.

![mainScreen](https://github.com/user-attachments/assets/2bd4c726-c7f4-4c94-ad15-869af1c06dea)

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
   - [Basic Features](#basic-features)
   - [Advanced Features](#advanced-features)
3. [Architecture](#architecture)
4. [Technologies Used](#technologies-used)
5. [Running the Application](#running-the-application)
   - [Server Setup](#server-setup)
   - [Client Setup](#client-setup)
   - [Additional Commands (Optional)](#additional-commands-optional)

---

## Features
This application provides comprehensive spreadsheet functionality with real-time collaboration capabilities.

### Basic Features
- **Interactive Spreadsheet Interface**: Excel-like grid layout for intuitive data entry and manipulation, with real-time updates and formula processing.
- **Real-Time Collaboration**: Enables multiple users to edit sheets simultaneously with built-in chat system and instant change visibility.

### Advanced Features
- **Advanced Sorting & Filtering**: The application offers powerful data organization capabilities through its sorting and filtering system. Users can sort data across multiple columns simultaneously, apply complex filters based on various criteria, and create hierarchical views of their data. This makes it easy to analyze large datasets and find specific information quickly.

<img src="https://github.com/user-attachments/assets/6d4668ac-feb2-452d-9c33-45a4ca225ce5" width="600">

<br><br>

- **Graphing Visualization & Analysis**: Transform your data into meaningful insights with our comprehensive visualization tools. Create dynamic line and bar charts from selected data ranges, view real-time statistical information (maximum, minimum, average values), and perform runtime analysis to test scenarios without affecting the original data. The analysis feature allows users to observe dependencies and potential impacts before committing changes.

<img src="https://github.com/user-attachments/assets/140363a5-7b1b-4d2f-81ba-0c7b1af50f7f" width="600">

<br><br>

- **Smart Features**: Enhance your productivity with intelligent auto-complete and find-replace capabilities. The auto-complete function analyzes patterns in your data to predict and suggest cell values, while the advanced find-replace tool allows you to locate and modify specific values across the entire sheet with a convenient preview option to review changes before applying them.


<img src="https://github.com/user-attachments/assets/eedcd836-dcdf-4591-923a-984d4c51597c" width="600">

<br><br>

- **Version Management**: Our powerful version control system maintains complete transparency and data integrity throughout your project's lifecycle. The system automatically tracks and stores all sheet modifications, allowing users to view detailed change histories, compare different versions. With features like user identification, timestamping, and comprehensive audit trails, teams can confidently track changes, recover from unintended modifications, and maintain accountability across all collaborative activities.

<img src="https://github.com/user-attachments/assets/bb69beed-9482-46be-8486-62b1ef3e6e85" width="600">

<br><br>

- **Access Control**: The application provides robust security through a comprehensive permission system that ensures data protection and controlled access. The system implements three distinct user roles: Owner, Writer, Reader.


<img src="https://github.com/user-attachments/assets/ced82721-544b-4ef6-873b-70f060951bdf" width="600">



---

## Architecture

This project follows an MVC architecture and a client-server model to synchronize data across multiple users.

### Server-Side

- **Java Servlets**: Handle requests and manage session data.
- **Data Persistence**: Supports session and version tracking.

### Client-Side

- **JavaFX**: For a responsive, interactive UI.
- **FXML**: For UI component design across screens.
- **HttpRequestManager**: Manages HTTP requests to handle CRUD operations and sheet updates.

---

## Technologies Used

- Java 11+
- JavaFX for UI
- FXML for layout design
- MVC architecture
- Tomcat server
- Gson for JSON serialization
- Docker

---
  
## Running the Application

Download the "Download-To-Run" zip file, navigate to the zipped folder structure containing the `client_setup` and `server_setup` folders. Follow these instructions to get the server and client running.

### Server Setup

1. **Requirements**:
   - Ensure that **Docker Desktop** is installed and running.
   - Microsoft operating system

2. **Steps to Start the Server**:
   - Open the `server_setup` folder.
   - Double-click `run.bat` to start the server.
     - This script will build the Docker image, start the Docker container, and deploy the application on `http://localhost:8080`.
   - Verify that the server is running by opening `http://localhost:8080` in a web browser.

### Client Setup

1. **Steps to Start the Client**:
   - Open the `client_setup` folder.
   - Double-click `run.bat` to start the client application.
   - To open multiple clients, run `run.bat` again in a new window.
   - The client application will connect to the server running on `http://localhost:8080`.

### Additional Commands (Optional)

- **To stop the server**, run:
  ```bash
  docker stop my-java-app-server

- **To restart the server**, run:
  ```bash
  docker start my-java-app-server
