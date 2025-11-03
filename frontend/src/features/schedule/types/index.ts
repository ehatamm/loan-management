export interface ScheduleItem {
  id?: string | number;
  paymentDate: string; // ISO date string
  payment: number;
  principal: number;
  interest: number;
  remainingBalance: number;
}

export interface ScheduleResponse {
  items: ScheduleItem[];
}

