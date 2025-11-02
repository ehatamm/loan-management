import { useEffect, useState } from 'react';
import { getAllLoans } from '../../api/client';
import type { Loan } from '../../types';
import { DataTable } from '../../components/DataTable';
import { LOAN_COLUMNS } from '../../types/columnDefinitions';

interface LoansListProps {
  onLoanSelect: (loanId: string) => void;
}

export function LoansList({ onLoanSelect }: LoansListProps) {
  const [loans, setLoans] = useState<Loan[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchLoans = async () => {
      setLoading(true);
      setError(null);
      try {
        const loansData = await getAllLoans();
        setLoans(loansData);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load loans');
      } finally {
        setLoading(false);
      }
    };

    fetchLoans();
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
}

