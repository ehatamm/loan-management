import { GridColDef } from '@mui/x-data-grid';
import { LoanType, ScheduleType } from '../types';
import type { Loan, ScheduleItem } from '../types';

// Field labels for Loan
export const LOAN_FIELD_LABELS: Record<keyof Loan, string> = {
  id: 'ID',
  loanType: 'Loan Type',
  amount: 'Amount',
  periodMonths: 'Period (Months)',
  annualInterestRate: 'Interest Rate (%)',
  scheduleType: 'Schedule Type',
  startDate: 'Start Date',
};

// Field labels for ScheduleItem
export const SCHEDULE_ITEM_FIELD_LABELS: Partial<Record<keyof ScheduleItem, string>> = {
  paymentDate: 'Payment Date',
  payment: 'Payment',
  principal: 'Principal',
  interest: 'Interest',
  remainingBalance: 'Remaining Balance',
};

// Column definitions for Loan
export const LOAN_COLUMNS: GridColDef<Loan>[] = [
  {
    field: 'loanType',
    headerName: LOAN_FIELD_LABELS.loanType,
    width: 130,
    valueFormatter: (value: LoanType) => value,
  },
  {
    field: 'amount',
    headerName: LOAN_FIELD_LABELS.amount,
    width: 130,
    type: 'number',
    valueFormatter: (value: number) => `€${value.toFixed(2)}`,
  },
  {
    field: 'periodMonths',
    headerName: LOAN_FIELD_LABELS.periodMonths,
    width: 140,
    type: 'number',
  },
  {
    field: 'annualInterestRate',
    headerName: LOAN_FIELD_LABELS.annualInterestRate,
    width: 150,
    type: 'number',
    valueFormatter: (value: number) => `${value.toFixed(2)}%`,
  },
  {
    field: 'scheduleType',
    headerName: LOAN_FIELD_LABELS.scheduleType,
    width: 150,
    valueFormatter: (value: ScheduleType) => value.replace('_', ' '),
  },
  {
    field: 'startDate',
    headerName: LOAN_FIELD_LABELS.startDate,
    width: 130,
    valueFormatter: (value: string) => new Date(value).toLocaleDateString(),
  },
];

// Column definitions for ScheduleItem
export const SCHEDULE_ITEM_COLUMNS: GridColDef<ScheduleItem>[] = [
  {
    field: 'paymentDate',
    headerName: SCHEDULE_ITEM_FIELD_LABELS.paymentDate,
    width: 150,
    valueFormatter: (value: string) => new Date(value).toLocaleDateString(),
  },
  {
    field: 'payment',
    headerName: SCHEDULE_ITEM_FIELD_LABELS.payment,
    width: 120,
    type: 'number',
    valueFormatter: (value: number) => `€${value.toFixed(2)}`,
  },
  {
    field: 'principal',
    headerName: SCHEDULE_ITEM_FIELD_LABELS.principal,
    width: 120,
    type: 'number',
    valueFormatter: (value: number) => `€${value.toFixed(2)}`,
  },
  {
    field: 'interest',
    headerName: SCHEDULE_ITEM_FIELD_LABELS.interest,
    width: 120,
    type: 'number',
    valueFormatter: (value: number) => `€${value.toFixed(2)}`,
  },
  {
    field: 'remainingBalance',
    headerName: SCHEDULE_ITEM_FIELD_LABELS.remainingBalance,
    width: 150,
    type: 'number',
    valueFormatter: (value: number) => `€${value.toFixed(2)}`,
  },
];

