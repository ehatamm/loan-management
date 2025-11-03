import { GridColDef } from '@mui/x-data-grid';
import { ScheduleType } from '../../../types';
import type { Loan } from './index';
import { LoanType } from './index';

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

