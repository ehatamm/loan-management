import { useMemo } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Box, Button } from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { LoansList } from './LoansList';
import { CreateLoanModal } from './modals/CreateLoanModal';
import { ScheduleModal } from '../../features/schedule/modals/ScheduleModal';

export function LoansPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLoanSelect = (loanId: string) => {
    navigate(`/loans/${loanId}/schedule`);
  };

  const isCreateModalOpen = location.pathname === '/loans/new';
  const isScheduleModalOpen = useMemo(
    () => location.pathname.match(/^\/loans\/[^/]+\/schedule$/),
    [location.pathname]
  );

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
      {isCreateModalOpen && <CreateLoanModal />}
      {isScheduleModalOpen && <ScheduleModal />}
    </Box>
  );
}

