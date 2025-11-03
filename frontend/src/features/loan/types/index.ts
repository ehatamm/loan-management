import { ScheduleType } from '../../../types';

export enum LoanType {
  CONSUMER = 'CONSUMER',
  CAR = 'CAR',
  MORTGAGE = 'MORTGAGE',
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

