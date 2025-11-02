import { useNavigate } from 'react-router-dom';
import { Dialog, DialogTitle, IconButton } from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import { LoanForm } from '../features/loan/LoanForm';

export function CreateLoanPage() {
  const navigate = useNavigate();

  const handleClose = () => {
    navigate('/');
  };

  const handleSuccess = (loanId: string) => {
    navigate(`/loans/${loanId}/schedule`);
  };

  return (
    <Dialog
      open={true}
      onClose={handleClose}
      maxWidth="md"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 2,
        },
      }}
    >
      <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 0 }}>
        Create New Loan
        <IconButton
          aria-label="close"
          onClick={handleClose}
          sx={{
            color: (theme) => theme.palette.grey[500],
          }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <LoanForm onSuccess={handleSuccess} />
    </Dialog>
  );
}

