// Types aligned with backend DTOs

export interface User {
  id: number;
  name: string;
  email: string;
}

export interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  token: string | null;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

// Category colors enum matching backend
export type CategoryColor =
  | 'RED' | 'TEAL' | 'BLUE' | 'GREEN' | 'YELLOW'
  | 'PINK' | 'ROYAL_BLUE' | 'PURPLE' | 'CYAN' | 'ORANGE'
  | 'VIOLET' | 'EMERALD' | 'ROSE' | 'AMBER' | 'INDIGO';

export interface Category {
  id: number;
  name: string;
  icon: string;
  color: CategoryColor;
  isCustom: boolean;
}

export interface CreateCategoryRequest {
  name: string;
  icon: string;
  color: CategoryColor;
}

// Account types matching backend
export type AccountType = 'CHECKING' | 'SAVINGS';

export interface BankAccount {
  id: number;
  bankName: string;
  nickName: string;
  accountType: AccountType;
  maskedAccountNum: string;
  balance: number;
  lastSync: string;
}

export interface ConnectBankRequest {
  bankName: string;
  accountType: AccountType;
  nickName: string;
  accountNum: string;
}

// Transaction types matching backend
export type TransactionType = 'INCOME' | 'EXPENSE';

export interface Transaction {
  id: number;
  amount: number;
  description: string;
  date: string;
  category: string;
  bankAccount: string;
  transactionType: TransactionType;
}

export interface CreateTransactionRequest {
  date: string;
  transactionType: TransactionType;
  amount: number;
  description?: string;
  categoryId: number;
  bankAccountId: number;
}

export interface TransactionFilterParams {
  transactionType?: TransactionType;
  categoryId?: number;
  bankAccountId?: number;
  fromDate?: string;
  toDate?: string;
  page?: number;
  size?: number;
}

// Budget types matching backend
export type BudgetPeriod = 'MONTHLY' | 'YEARLY';

export interface Budget {
  id: number;
  categoryName: string;
  categoryIcon: string;
  amount: number;
  spentAmount: number;
  period: BudgetPeriod;
}

export interface CreateBudgetRequest {
  category: { id: number };
  amount: number;
  period: BudgetPeriod;
}

// Dashboard types matching backend
export interface MonthlyData {
  month: string;
  income: number;
  expenses: number;
}

export interface ExpenseByCategory {
  categoryName: string;
  amount: number;
  color: string;
}

export interface DashboardData {
  totalIncome: number;
  totalExpenses: number;
  totalSavings: number;
  accounts: BankAccount[];
  recentTransactions: Transaction[];
  monthlyData: MonthlyData[];
  expensesByCategory: ExpenseByCategory[];
}

// Paginated response from Spring Boot
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// Filter options for UI (kept for backward compatibility in some components)
export interface FilterOptions {
  accountId?: number;
  categoryId?: number;
  type?: TransactionType | 'all';
  dateFrom?: string;
  dateTo?: string;
  search?: string;
}

// Job types
export interface JobRequest {
  jobId: string;
  idempotencyKey: string;
  status: 'SUBMITTED' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  result?: string;
  bankAccountId: number;
  userId: number;
  createdAt: string;
  updatedAt: string;
}

export interface InitiateJobResponse {
  jobId: string;
  status: string;
}