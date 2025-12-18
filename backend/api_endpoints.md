# API Endpoints & Request Bodies

This document lists all the API endpoints available in the application, grouped by Controller. Use these details to configure your Postman requests.

## Dashboard Controller
**Base URL:** `/api/dashboard`

### Get Dashboard Data
*   **URL:** `/`
*   **Method:** `GET`
*   **Body:** None
*   **Response:** Contains totalIncome, totalExpenses, totalSavings, accounts, recentTransactions, monthlyData, expensesByCategory

---

## Auth Controller

**Base URL:** `/api/auth`

### Register User
*   **URL:** `/register`
*   **Method:** `POST`
*   **Body:** `RegisterUserRequest`
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

### Login User
*   **URL:** `/login`
*   **Method:** `POST`
*   **Body:** `LoginUserRequest`
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

---

## Bank Controller
**Base URL:** `/api/banks`

### Get User Bank Accounts
*   **URL:** `/`
*   **Method:** `GET`
*   **Body:** None

### Connect Bank Account
*   **URL:** `/`
*   **Method:** `POST`
*   **Body:** `ConnectBankRequest`
```json
{
  "bankName": "Chase",
  "accountType": "CHECKING",
  "nickName": "My Checking",
  "accountNum": "123456789"
}
```
*   **Note:** `accountType` can be `CHECKING` or `SAVINGS`.

### Sync Transactions
*   **URL:** `/{id}/sync`
*   **Method:** `POST`
*   **Body:** None

---

## Budget Controller
**Base URL:** `/api/budgets`

### Get User Budgets
*   **URL:** `/`
*   **Method:** `GET`
*   **Body:** None

### Create Budget
*   **URL:** `/`
*   **Method:** `POST`
*   **Body:** `CreateBudgetRequest`
```json
{
  "category": {
    "id": 1
  },
  "amount": 1000.00,
  "period": "MONTHLY"
}
```
*   **Note:** `period` can be `MONTHLY` or `YEARLY`. The `category` field expects a Category object; typically providing the `id` of an existing category is sufficient.

### Update Budget
*   **URL:** `/{budgetId}`
*   **Method:** `PUT`
*   **Body:** `CreateBudgetRequest` (Same as Create)

### Delete Budget
*   **URL:** `/{budgetId}`
*   **Method:** `DELETE`
*   **Body:** None

---

## Category Controller
**Base URL:** `/api/categories`

### Get All Categories
*   **URL:** `/`
*   **Method:** `GET`
*   **Body:** None

### Create Category
*   **URL:** `/`
*   **Method:** `POST`
*   **Body:** `CreateCategoryRequest`
```json
{
  "name": "Groceries",
  "icon": "shopping_cart",
  "color": "GREEN"
}
```
*   **Note:** `color` values include: `RED`, `TEAL`, `BLUE`, `GREEN`, `YELLOW`, `PINK`, `ROYAL_BLUE`, `PURPLE`, `CYAN`, `ORANGE`, `VIOLET`, `EMERALD`, `ROSE`, `AMBER`, `INDIGO`.

### Update Category
*   **URL:** `/{categoryId}`
*   **Method:** `PUT`
*   **Body:** `CreateCategoryRequest` (Same as Create)

### Delete Category
*   **URL:** `/{categoryId}`
*   **Method:** `DELETE`
*   **Body:** None

---

## Transaction Controller
**Base URL:** `/api/transactions`

### Get User Transactions
*   **URL:** `/`
*   **Method:** `GET`
*   **Params:**
    *   `page` (optional, default 0)
    *   `size` (optional, default 20)
    *   *Example:* `?page=0&size=20`
*   **Body:** None

### Search Transactions
*   **URL:** `/search`
*   **Method:** `GET`
*   **Params:**
    *   `query` (required)
    *   `page` (optional)
    *   `size` (optional)
    *   *Example:* `?query=coffee&page=0&size=20`
*   **Body:** None

### Filter Transactions
*   **URL:** `/filter`
*   **Method:** `GET`
*   **Params:** (All optional)
    *   `transactionType` (EXPENSE, INCOME)
    *   `categoryId`
    *   `bankAccountId`
    *   `fromDate` (YYYY-MM-DD)
    *   `toDate` (YYYY-MM-DD)
    *   `page`
    *   `size`
    *   *Example:* `?transactionType=EXPENSE&categoryId=1&fromDate=2023-01-01`
*   **Body:** None

### Create Transaction
*   **URL:** `/`
*   **Method:** `POST`
*   **Body:** `CreateTransactionRequest`
```json
{
  "date": "2023-10-27",
  "transactionType": "EXPENSE",
  "amount": 50.00,
  "description": "Grocery shopping",
  "CategoryId": 1,
  "bankAccountId": 1
}
```
*   **Note:** `transactionType` can be `EXPENSE` or `INCOME`.
