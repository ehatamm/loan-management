import { useState, FormEvent } from 'react';
import {
  Box,
  Button,
  Paper,
  Typography,
  Alert,
} from '@mui/material';
import { createLoan } from '../../api/client';
import type { CreateLoanRequest } from '../../types';
import { LoanType, ScheduleType } from '../../types';
import { FormSelectField } from './components/FormSelectField';
import { FormTextField } from './components/FormTextField';

interface LoanFormProps {
  onSuccess?: (loanId: string) => void;
}

export function LoanForm({ onSuccess }: LoanFormProps) {
  const [formData, setFormData] = useState<CreateLoanRequest>({
    loanType: LoanType.CONSUMER,
    amount: 0,
    periodMonths: 12,
    annualInterestRate: 0,
    scheduleType: ScheduleType.ANNUITY,
    startDate: new Date().toISOString().split('T')[0],
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateField = <K extends keyof CreateLoanRequest>(
    field: K,
    value: CreateLoanRequest[K]
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    setError(null);
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const loan = await createLoan(formData);
      onSuccess?.(loan.id);
      setFormData({
        loanType: LoanType.CONSUMER,
        amount: 0,
        periodMonths: 12,
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
    <Paper sx={{ p: 3, mt: 2 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        Create New Loan
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
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

        <FormTextField
          label="Amount"
          type="number"
          value={formData.amount}
          onChange={(value) => updateField('amount', value as number)}
          inputProps={{ min: 0.01, step: 0.01 }}
        />

        <FormTextField
          label="Period (Months)"
          type="number"
          value={formData.periodMonths}
          onChange={(value) => updateField('periodMonths', value as number)}
          inputProps={{ min: 1 }}
        />

        <FormTextField
          label="Annual Interest Rate (%)"
          type="number"
          value={formData.annualInterestRate}
          onChange={(value) => updateField('annualInterestRate', value as number)}
          inputProps={{ min: 0, max: 100, step: 0.01 }}
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
          onChange={(value) => updateField('startDate', value as string)}
          InputLabelProps={{ shrink: true }}
        />

        <Button type="submit" variant="contained" disabled={loading} sx={{ mt: 2 }}>
          {loading ? 'Creating...' : 'Create Loan'}
        </Button>
      </Box>
    </Paper>
  );
}
