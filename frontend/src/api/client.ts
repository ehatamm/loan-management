import type {
  Loan,
  CreateLoanRequest,
  ScheduleResponse,
  ErrorResponse,
} from '../types';

const API_BASE_URL = '/api';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorData: ErrorResponse = await response.json();
    throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
  }
  return response.json();
}

export async function createLoan(request: CreateLoanRequest): Promise<Loan> {
  const response = await fetch(`${API_BASE_URL}/loans`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  return handleResponse<Loan>(response);
}

export async function getLoan(id: string): Promise<Loan> {
  const response = await fetch(`${API_BASE_URL}/loans/${id}`);

  return handleResponse<Loan>(response);
}

export async function getSchedule(id: string): Promise<ScheduleResponse> {
  const response = await fetch(`${API_BASE_URL}/loans/${id}/schedule`);

  return handleResponse<ScheduleResponse>(response);
}

