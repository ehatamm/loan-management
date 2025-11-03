import { useEffect, useState, useImperativeHandle, forwardRef } from 'react';
import { getAllLoans } from '../../api/client';
import type { Loan } from './types';
import { DataTable } from '../../components/DataTable';
import { LOAN_COLUMNS } from './types/columnDefinitions';

interface LoansListProps {
  onLoanSelect: (loanId: string) => void;
}

export interface LoansListRef {
  refetch: () => void;
}

export const LoansList = forwardRef<LoansListRef, LoansListProps>(({ onLoanSelect }, ref) => {
  const [loans, setLoans] = useState<Loan[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchLoans = async (abortSignal?: AbortSignal) => {
    setLoading(true);
    setError(null);
    try {
      const loansData = await getAllLoans(abortSignal);
      setLoans(loansData);
    } catch (err) {
      if (err instanceof Error && err.name !== 'AbortError') {
        setError(err.message || 'Failed to load loans');
      }
    } finally {
      if (!abortSignal?.aborted) {
        setLoading(false);
      }
    }
  };

  useImperativeHandle(ref, () => ({
    refetch: () => {
      const abortController = new AbortController();
      fetchLoans(abortController.signal);
    },
  }));

  useEffect(() => {
    const abortController = new AbortController();
    fetchLoans(abortController.signal);
    return () => {
      abortController.abort();
    };
  }, []);

  return (
    <DataTable<Loan>
      title="All Loans"
      columns={LOAN_COLUMNS}
      rows={loans}
      loading={loading}
      error={error}
      emptyMessage="No loans found. Create a new loan to get started."
      onRowClick={(id) => onLoanSelect(String(id))}
    />
  );
});

LoansList.displayName = 'LoansList';

