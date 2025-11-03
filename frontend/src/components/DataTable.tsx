import { useMemo } from 'react';
import { Box, CircularProgress, Paper, Typography, Alert } from '@mui/material';
import { DataGrid, GridColDef, GridValidRowModel } from '@mui/x-data-grid';

interface DataTableProps<T extends GridValidRowModel> {
  title?: string;
  columns: GridColDef<T>[];
  rows: T[];
  loading: boolean;
  error: string | null;
  emptyMessage?: string;
  onRowClick?: (id: string | number) => void;
  getRowId?: (row: T, index: number) => string | number;
  disablePaper?: boolean;
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
  disablePaper = false,
}: DataTableProps<T>) {
  const paperSx = disablePaper 
    ? { p: 0, mt: 0 }
    : { p: 3, mt: title ? 2 : 0 };

  const renderContent = (content: React.ReactNode) => {
    if (disablePaper) {
      return <Box sx={paperSx}>{content}</Box>;
    }
    return <Paper sx={paperSx}>{content}</Paper>;
  };

  if (loading) {
    return renderContent(
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return renderContent(<Alert severity="error">{error}</Alert>);
  }

  if (rows.length === 0) {
    return renderContent(
      <>
        {title && (
          <Typography variant="h5" component="h2" gutterBottom>
            {title}
          </Typography>
        )}
        <Alert severity="info" sx={{ mt: 2 }}>
          {emptyMessage || 'No data available.'}
        </Alert>
      </>
    );
  }

  const processedRows = useMemo(
    () =>
      rows.map((row, index) => ({
        ...row,
        id: getRowId ? getRowId(row, index) : (row.id ?? index),
      })),
    [rows, getRowId]
  );

  const handleRowClick = onRowClick 
    ? (params: { row: T & { id: string | number } }) => {
        const id = typeof params.row.id === 'string' || typeof params.row.id === 'number' 
          ? params.row.id 
          : String(params.row.id);
        onRowClick(id);
      }
    : undefined;

  return renderContent(
    <>
      {title && (
        <Typography variant="h5" component="h2" gutterBottom>
          {title}
        </Typography>
      )}
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
    </>
  );
}

