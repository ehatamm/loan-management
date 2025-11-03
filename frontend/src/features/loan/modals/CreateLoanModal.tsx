import { useNavigate } from 'react-router-dom';
import { Dialog, DialogTitle, IconButton } from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import { LoanForm } from '../LoanForm';

export function CreateLoanModal() {
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
      <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 2 }}>
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

