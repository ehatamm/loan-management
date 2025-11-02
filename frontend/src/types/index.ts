export enum LoanType {
  CONSUMER = 'CONSUMER',
  CAR = 'CAR',
  MORTGAGE = 'MORTGAGE',
}

export enum ScheduleType {
  ANNUITY = 'ANNUITY',
  EQUAL_PRINCIPAL = 'EQUAL_PRINCIPAL',
}

export interface Loan {
  id: string;
  loanType: LoanType;
  amount: number;
  periodMonths: number;
  annualInterestRate: number;
  scheduleType: ScheduleType;
  startDate: string; // ISO date string
}

export interface CreateLoanRequest {
  loanType: LoanType;
  amount: number;
  periodMonths: number;
  annualInterestRate: number;
  scheduleType: ScheduleType;
  startDate: string; // ISO date string
}

export interface ScheduleItem {
  paymentDate: string; // ISO date string
  payment: number;
  principal: number;
  interest: number;
  remainingBalance: number;
}

export interface ScheduleResponse {
  items: ScheduleItem[];
}

export interface ErrorResponse {
  error: string;
  message: string;
  fieldErrors?: Record<string, string>;
}

