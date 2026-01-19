# FinTrack 💰

FinTrack is a smart, AI-powered personal finance tracker that helps you manage your money with ease. It leverages Google's Gemini AI to automatically categorize and clean up your transaction data, providing you with clear insights into your spending habits.

## 🚀 Features

*   **AI-Powered Transaction Sync**: Automatically syncs and cleans transaction descriptions using Gemini LLM.
*   **Smart Categorization**: Transactions are automatically assigned categories based on their description.
*   **Comprehensive Dashboard**: Visualize your income, expenses, and net worth with interactive charts.
*   **Bank Account Management**: Track multiple bank accounts in one place.
*   **Budget Tracking**: Set budgets for different categories and monitor your progress.
*   **Advanced Search & Filter**: Easily find specific transactions with powerful filtering options.
*   **Secure Authentication**: JWT-based security to keep your data safe.
*   **Responsive Design**: Built with modern web technologies for a smooth experience on any device.

## 🛠️ Tech Stack

### Backend
*   **Java 21**: Core language.
*   **Spring Boot 3.5.7**: Framework for building the REST API.
*   **PostgreSQL**: Primary database.
*   **Spring Data JPA / Hibernate**: ORM for database interactions.
*   **Spring Security + JWT**: For secure authentication and authorization.
*   **Lombok**: Reduces boilerplate code.
*   **MapStruct**: Efficient object mapping.
*   **Google Gemini API**: For AI-driven transaction processing.

### Frontend
*   **React 18**: UI library.
*   **TypeScript**: For type-safe code.
*   **Vite**: Fast build tool and dev server.
*   **TailwindCSS**: Utility-first CSS framework for styling.
*   **TanStack Query**: For efficient server state management.
*   **Recharts**: For beautiful and responsive charts.
*   **React Hook Form + Yup**: For form handling and validation.

## ⚙️ Prerequisites

Before you begin, ensure you have the following installed:
*   [Java SDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
*   [Node.js](https://nodejs.org/) (v18 or higher recommended)
*   [PostgreSQL](https://www.postgresql.org/)

## 🏃‍♂️ Getting Started

### 1. Database Setup
Create a PostgreSQL database named `fintrack`.
```sql
CREATE DATABASE fintrack;
```

### 2. Backend Setup
Navigate to the `backend` directory. Use Maven to run the application.

```bash
cd backend
# Run with the Maven Wrapper (Unix)
./mvnw spring-boot:run

# Run with the Maven Wrapper (Windows)
./mvnw.cmd spring-boot:run
```
The backend server will start on `http://localhost:8000`.

**Configuration:**
The application uses `src/main/resources/application.properties` for configuration. Update the following fields if your local setup differs:
*   `spring.datasource.username` 
*   `spring.datasource.password` 
*   `gemini.api.key` (Requires a valid Google Gemini API Key)

### 3. Frontend Setup
Navigate to the `frontend` directory to install dependencies and start the dev server.

```bash
cd frontend
npm install
npm run dev
```

The application will be available at `http://localhost:5173`.

## 🔑 Environment Variables

Key configurations found in `application.properties`:
*   `spring.datasource.url`: JDBC URL for PostgreSQL.
*   `app.jwt.secret`: Secret key for signing JWT tokens.
*   `gemini.api.key`: Your Google Gemini API Key for AI functionality.

