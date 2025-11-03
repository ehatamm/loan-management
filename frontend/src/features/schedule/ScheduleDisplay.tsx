import { useEffect, useState } from 'react';
import { getSchedule } from '../../api/client';
import type { ScheduleItem } from './types';
import { DataTable } from '../../components/DataTable';
import { SCHEDULE_ITEM_COLUMNS } from './types/columnDefinitions';

interface ScheduleDisplayProps {
  loanId: string;
}

export function ScheduleDisplay({ loanId }: ScheduleDisplayProps) {
  const [schedule, setSchedule] = useState<ScheduleItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const abortController = new AbortController();

    const fetchSchedule = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await getSchedule(loanId, abortController.signal);
        setSchedule(response.items);
      } catch (err) {
        if (err instanceof Error && err.name !== 'AbortError') {
          setError(err.message || 'Failed to load schedule');
        }
      } finally {
        if (!abortController.signal.aborted) {
          setLoading(false);
        }
      }
    };

    if (loanId) {
      fetchSchedule();
    }

    return () => {
      abortController.abort();
    };
  }, [loanId]);

  return (
    <DataTable<ScheduleItem>
      columns={SCHEDULE_ITEM_COLUMNS}
      rows={schedule}
      loading={loading}
      error={error}
      getRowId={(_item, index) => index}
      disablePaper
    />
  );
}

