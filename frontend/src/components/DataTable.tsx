import { Box, CircularProgress, Paper, Typography, Alert } from '@mui/material';
import { DataGrid, GridColDef, GridValidRowModel } from '@mui/x-data-grid';

interface DataTableProps<T extends GridValidRowModel> {
  title: string;
  columns: GridColDef<T>[];
  rows: T[];
  loading: boolean;
  error: string | null;
  emptyMessage?: string;
  onRowClick?: (id: string | number) => void;
  getRowId?: (row: T, index: number) => string | number;
}

export function DataTable<T extends GridValidRowModel>({
  title,
  columns,
  rows,
  loading,
  error,
  emptyMessage,
  onRowClick,
  getRowId,
}: DataTableProps<T>) {
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

  if (rows.length === 0) {
    return (
      <Paper sx={{ p: 3, mt: 2 }}>
        <Typography variant="h5" component="h2" gutterBottom>
          {title}
        </Typography>
        <Alert severity="info" sx={{ mt: 2 }}>
          {emptyMessage || 'No data available.'}
        </Alert>
      </Paper>
    );
  }

  const processedRows = rows.map((row, index) => ({
    ...row,
    id: getRowId ? getRowId(row, index) : (row.id ?? index),
  }));

  const handleRowClick = onRowClick 
    ? (params: { row: T & { id: string | number } }) => {
        const id = typeof params.row.id === 'string' || typeof params.row.id === 'number' 
          ? params.row.id 
          : String(params.row.id);
        onRowClick(id);
      }
    : undefined;

  return (
    <Paper sx={{ p: 3, mt: 2 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        {title}
      </Typography>
      <DataGrid
        rows={processedRows}
        columns={columns}
        autoHeight
        pageSizeOptions={[10, 25, 50, 100]}
        initialState={{
          pagination: {
            paginationModel: { pageSize: 25 },
          },
        }}
        onRowClick={handleRowClick}
        sx={onRowClick ? { mt: 2, cursor: 'pointer' } : { mt: 2 }}
        slotProps={{
          pagination: {
            labelRowsPerPage: 'Rows per page:',
          },
        }}
      />
    </Paper>
  );
}

