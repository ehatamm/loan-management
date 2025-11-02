import { useNavigate, useLocation } from 'react-router-dom';
import { Box, Button } from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { LoansList } from '../features/loan/LoansList';
import { CreateLoanPage } from './CreateLoanPage';
import { SchedulePage } from './SchedulePage';

export function LoansPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLoanSelect = (loanId: string) => {
    navigate(`/loans/${loanId}/schedule`);
  };

  const isCreateModalOpen = location.pathname === '/loans/new';
  const isScheduleModalOpen = location.pathname.match(/^\/loans\/[^/]+\/schedule$/);

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box sx={{ flexGrow: 1 }} />
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/loans/new')}
          size="large"
          sx={{ minWidth: 160 }}
        >
          Create Loan
        </Button>
      </Box>
      <LoansList onLoanSelect={handleLoanSelect} />
      {isCreateModalOpen && <CreateLoanPage />}
      {isScheduleModalOpen && <SchedulePage />}
    </Box>
  );
}

