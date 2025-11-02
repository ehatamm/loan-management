import { useEffect, useState } from 'react';
import { getSchedule } from '../../api/client';
import type { ScheduleItem } from '../../types';
import { DataTable } from '../../components/DataTable';
import { SCHEDULE_ITEM_COLUMNS } from '../../types/columnDefinitions';

interface ScheduleDisplayProps {
  loanId: string;
}

export function ScheduleDisplay({ loanId }: ScheduleDisplayProps) {
  const [schedule, setSchedule] = useState<ScheduleItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchSchedule = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await getSchedule(loanId);
        setSchedule(response.items);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load schedule');
      } finally {
        setLoading(false);
      }
    };

    if (loanId) {
      fetchSchedule();
    }
  }, [loanId]);

  return (
    <DataTable<ScheduleItem>
      title="Repayment Schedule"
      columns={SCHEDULE_ITEM_COLUMNS}
      rows={schedule}
      loading={loading}
      error={error}
      getRowId={(_item, index) => index}
    />
  );
}

