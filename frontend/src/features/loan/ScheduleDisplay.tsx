import { useEffect, useState } from 'react';
import { Box, CircularProgress, Paper, Typography, Alert } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { getSchedule } from '../../api/client';
import type { ScheduleItem } from '../../types';

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

  const columns: GridColDef<ScheduleItem>[] = [
    {
      field: 'paymentDate',
      headerName: 'Payment Date',
      width: 150,
      valueFormatter: (value: string) => new Date(value).toLocaleDateString(),
    },
    {
      field: 'payment',
      headerName: 'Payment',
      width: 120,
      type: 'number',
      valueFormatter: (value: number) => `€${value.toFixed(2)}`,
    },
    {
      field: 'principal',
      headerName: 'Principal',
      width: 120,
      type: 'number',
      valueFormatter: (value: number) => `€${value.toFixed(2)}`,
    },
    {
      field: 'interest',
      headerName: 'Interest',
      width: 120,
      type: 'number',
      valueFormatter: (value: number) => `€${value.toFixed(2)}`,
    },
    {
      field: 'remainingBalance',
      headerName: 'Remaining Balance',
      width: 150,
      type: 'number',
      valueFormatter: (value: number) => `€${value.toFixed(2)}`,
    },
  ];

  const rows = schedule.map((item, index) => ({
    id: index,
    ...item,
  }));

  if (loading) {
    return (
      <Paper sx={{ p: 3, mt: 2 }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
          <CircularProgress />
        </Box>
      </Paper>
    );
  }

  if (error) {
    return (
      <Paper sx={{ p: 3, mt: 2 }}>
        <Alert severity="error">{error}</Alert>
      </Paper>
    );
  }

  return (
    <Paper sx={{ p: 3, mt: 2 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        Repayment Schedule
      </Typography>
      <DataGrid
        rows={rows}
        columns={columns}
        autoHeight
        pageSizeOptions={[10, 25, 50, 100]}
        initialState={{
          pagination: {
            paginationModel: { pageSize: 25 },
          },
        }}
        sx={{ mt: 2 }}
      />
    </Paper>
  );
}

