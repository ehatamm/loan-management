import type { Loan, CreateLoanRequest } from '../features/loan/types';
import type { ScheduleResponse } from '../features/schedule/types';
import type { ErrorResponse } from '../types';

const API_BASE_URL = '/api';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let errorMessage = `HTTP error! status: ${response.status}`;
    const contentType = response.headers.get('content-type');
    
    if (contentType?.includes('application/json')) {
      try {
        const errorData: ErrorResponse = await response.json();
        errorMessage = errorData.message || errorData.error || errorMessage;
      } catch {
        // Ignore JSON parse errors, use default message
      }
    }
    
    throw new Error(errorMessage);
  }
  return response.json();
}

export async function createLoan(request: CreateLoanRequest, signal?: AbortSignal): Promise<Loan> {
  try {
    const response = await fetch(`${API_BASE_URL}/loans`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
      signal,
    });

    return handleResponse<Loan>(response);
  } catch (err) {
    if (err instanceof Error && err.name === 'AbortError') {
      throw err;
    }
    throw new Error(err instanceof Error ? err.message : 'Network error occurred');
  }
}

export async function getLoan(id: string, signal?: AbortSignal): Promise<Loan> {
  try {
    const response = await fetch(`${API_BASE_URL}/loans/${id}`, { signal });
    return handleResponse<Loan>(response);
  } catch (err) {
    if (err instanceof Error && err.name === 'AbortError') {
      throw err;
    }
    throw new Error(err instanceof Error ? err.message : 'Network error occurred');
  }
}

export async function getSchedule(id: string, signal?: AbortSignal): Promise<ScheduleResponse> {
  try {
    const response = await fetch(`${API_BASE_URL}/loans/${id}/schedule`, { signal });
    return handleResponse<ScheduleResponse>(response);
  } catch (err) {
    if (err instanceof Error && err.name === 'AbortError') {
      throw err;
    }
    throw new Error(err instanceof Error ? err.message : 'Network error occurred');
  }
}

export async function getAllLoans(signal?: AbortSignal): Promise<Loan[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/loans`, { signal });
    return handleResponse<Loan[]>(response);
  } catch (err) {
    if (err instanceof Error && err.name === 'AbortError') {
      throw err;
    }
    throw new Error(err instanceof Error ? err.message : 'Network error occurred');
  }
}

