import { GridColDef } from '@mui/x-data-grid';
import type { ScheduleItem } from './index';

// Field labels for ScheduleItem
export const SCHEDULE_ITEM_FIELD_LABELS: Partial<Record<keyof ScheduleItem, string>> = {
  paymentDate: 'Payment Date',
  payment: 'Payment',
  principal: 'Principal',
  interest: 'Interest',
  remainingBalance: 'Remaining Balance',
};

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

