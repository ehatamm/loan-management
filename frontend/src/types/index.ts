// Shared types across features
export enum ScheduleType {
  ANNUITY = 'ANNUITY',
  EQUAL_PRINCIPAL = 'EQUAL_PRINCIPAL',
}

export interface ErrorResponse {
  error: string;
  message: string;
  fieldErrors?: Record<string, string>;
}

