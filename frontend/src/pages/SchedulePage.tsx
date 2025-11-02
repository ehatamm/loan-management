import { useParams, useNavigate } from 'react-router-dom';
import { Dialog, DialogTitle, IconButton, DialogContent } from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import { ScheduleDisplay } from '../features/loan/ScheduleDisplay';
import { Alert } from '@mui/material';

export function SchedulePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const handleClose = () => {
    navigate('/');
  };

  if (!id) {
    return (
      <Dialog open={true} onClose={handleClose} maxWidth="lg" fullWidth>
        <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 0 }}>
          Error
          <IconButton
            aria-label="close"
            onClick={handleClose}
            sx={{ color: (theme) => theme.palette.grey[500] }}
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent dividers sx={{ pt: 3, px: 3 }}>
          <Alert severity="error">Loan ID is required</Alert>
        </DialogContent>
      </Dialog>
    );
  }

  return (
    <Dialog
      open={true}
      onClose={handleClose}
      maxWidth="lg"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 2,
        },
      }}
    >
      <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 0 }}>
        Repayment Schedule
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
      <DialogContent sx={{ pt: 3, px: 3 }}>
        <ScheduleDisplay loanId={id} />
      </DialogContent>
    </Dialog>
  );
}

