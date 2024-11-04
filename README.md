# Sheet Cell Collaborative Application

**![Sheet_Cell_Banner](https://github.com/user-attachments/assets/9a983ab6-cd39-48dc-b16e-730f83224f74)**  

---

## Overview

![mainScreen](https://github.com/user-attachments/assets/2bd4c726-c7f4-4c94-ad15-869af1c06dea)

Sheet Cell is a collaborative client-server desktop application built in Java using JavaFX, providing an Excel-like spreadsheet environment for real-time data manipulation, analysis, and visualization. Designed to support concurrent users, it includes real-time chat and version control, making it ideal for teams working on shared sheets.

## Table of Contents

1. [Features](#features)
   - [Basic Features](#basic-features)
   - [Advanced Features](#advanced-features)
2. [Architecture](#architecture)
3. [Technologies Used](#technologies-used)
4. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Installation](#installation)
5. [Screenshots and Usage](#screenshots-and-usage)
6. [Customization and Permissions](#customization-and-permissions)
7. [Known Issues and Limitations](#known-issues-and-limitations)

---

## Features

### Basic Features

- **Spreadsheet Interface**  
  Familiar grid layout for data entry and manipulation, similar to conventional spreadsheet applications.

- **Real-Time Collaboration**  
  Multiple users can edit the same sheet simultaneously, with updates reflected in real-time for all connected users.

- **Cell Operations**  
  - Update cell values and view dependencies.
  - Manage ranges for grouped operations.

### Advanced Features

- **Sorting and Filtering**  
  - Multi-column sorting and flexible filtering for customized data views.

- **Graphing and Visualization**  
  - Create line, bar, and other charts from selected data ranges with real-time statistics for maximum, minimum, and average values.

- **Dynamic Analysis**  
  - Run-time analysis to experiment with data changes and observe dependencies without permanently modifying the sheet.

- **Auto-Complete and Find & Replace**  
  - Auto-complete based on detected patterns and sequence predictions.
  - Find and replace values with a preview option.

- **Permissions and User Management**  
  - Access levels (Owner, Reader, Writer) for each user, managed directly through the dashboard.

- **Real-Time Chat**  
  - Built-in chat for seamless communication between users.

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

---

## Getting Started

### Screenshots and Usage

#### Sheet Access
![gettingIntoTheSystem](https://github.com/user-attachments/assets/ced82721-544b-4ef6-873b-70f060951bdf)

#### Version History
![PreviousVersions](https://github.com/user-attachments/assets/bb69beed-9482-46be-8486-62b1ef3e6e85)

#### Runtime Analysis
![RunTimeAnalysis](https://github.com/user-attachments/assets/140363a5-7b1b-4d2f-81ba-0c7b1af50f7f)

#### Graph Creation
![makingGraph](https://github.com/user-attachments/assets/57323d5f-e87b-4f4f-83f1-560d64712034)

#### Sorting Rows
![SortingRows](https://github.com/user-attachments/assets/1281f15c-303e-4ed5-90ad-e9aee40c31b5)

#### Filtering Data
![filter data](https://github.com/user-attachments/assets/6d4668ac-feb2-452d-9c33-45a4ca225ce5)

#### Find and Replace
![FindAndReplace](https://github.com/user-attachments/assets/6df11887-9088-417d-bb4f-55a20c391d88)

#### Auto Complete
![AutoComplete](https://github.com/user-attachments/assets/eedcd836-dcdf-4591-923a-984d4c51597c)

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- JavaFX SDK 
- Apache Tomcat server 
  
### Installation

1. **Clone the Repository**  
   ```bash
   git clone https://github.com/yourusername/yourprojectname.git
