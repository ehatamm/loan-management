import { useState, FormEvent } from 'react';
import {
  Box,
  Button,
  Alert,
  DialogContent,
} from '@mui/material';
import { createLoan } from '../../api/client';
import type { CreateLoanRequest } from './types';
import { LoanType } from './types';
import { ScheduleType } from '../../types';
import { FormSelectField } from './components/FormSelectField';
import { FormTextField } from './components/FormTextField';
import { NumericInput } from './components/NumericInput';

interface LoanFormProps {
  onSuccess?: (loanId: string) => void;
}

export function LoanForm({ onSuccess }: LoanFormProps) {
  const [formData, setFormData] = useState<CreateLoanRequest>({
    loanType: LoanType.CONSUMER,
    amount: 0,
    periodMonths: 0,
    annualInterestRate: 0,
    scheduleType: ScheduleType.ANNUITY,
    startDate: new Date().toISOString().split('T')[0],
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isFormValid = (): boolean => {
    return (
      formData.amount >= 0.01 &&
      formData.periodMonths >= 1 &&
      formData.annualInterestRate >= 0.01 &&
      formData.annualInterestRate <= 100 &&
      formData.startDate !== ''
    );
  };

  const updateField = <K extends keyof CreateLoanRequest>(
    field: K,
    value: CreateLoanRequest[K]
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    setError(null);
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    
    if (!isFormValid()) {
      setError('Please fill in all required fields with valid values');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const loan = await createLoan(formData);
      onSuccess?.(loan.id);
      setFormData({
        loanType: LoanType.CONSUMER,
        amount: 0,
        periodMonths: 0,
        annualInterestRate: 0,
        scheduleType: ScheduleType.ANNUITY,
        startDate: new Date().toISOString().split('T')[0],
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create loan');
    } finally {
      setLoading(false);
    }
  };

  return (
    <DialogContent sx={{ pt: 4, px: 3, pb: 3 }}>
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        <Box sx={{ mt: 1 }}>
          <FormSelectField
            label="Loan Type"
            value={formData.loanType}
            onChange={(value) => updateField('loanType', value)}
            options={[
              { value: LoanType.CONSUMER, label: 'Consumer' },
              { value: LoanType.CAR, label: 'Car' },
              { value: LoanType.MORTGAGE, label: 'Mortgage' },
            ]}
          />
        </Box>

        <NumericInput
          label="Amount"
          value={formData.amount}
          onChange={(value) => updateField('amount', value)}
          allowDecimals
          min={0.01}
          required
        />

        <NumericInput
          label="Period (Months)"
          value={formData.periodMonths}
          onChange={(value) => updateField('periodMonths', value)}
          allowDecimals={false}
          min={1}
          required
        />

        <NumericInput
          label="Annual Interest Rate (%)"
          value={formData.annualInterestRate}
          onChange={(value) => updateField('annualInterestRate', value)}
          allowDecimals
          min={0.01}
          max={100}
          required
        />

        <FormSelectField
          label="Schedule Type"
          value={formData.scheduleType}
          onChange={(value) => updateField('scheduleType', value)}
          options={[
            { value: ScheduleType.ANNUITY, label: 'Annuity' },
            { value: ScheduleType.EQUAL_PRINCIPAL, label: 'Equal Principal' },
          ]}
        />

        <FormTextField
          label="Start Date"
          type="date"
          value={formData.startDate}
          onChange={(value) => {
            if (typeof value === 'string') {
              updateField('startDate', value);
            }
          }}
          InputLabelProps={{ shrink: true }}
        />

        <Button type="submit" variant="contained" disabled={loading || !isFormValid()} sx={{ mt: 2 }}>
          {loading ? 'Creating...' : 'Create Loan'}
        </Button>
      </Box>
    </DialogContent>
  );
}
