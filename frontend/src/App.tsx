import { CssBaseline, Container, Typography } from '@mui/material'
import { LoanForm } from './features/loan/LoanForm'

function App() {
  const handleLoanCreated = (loanId: string) => {
    console.log('Loan created:', loanId);
    // TODO: Navigate to schedule view or show success message
  };

  return (
    <>
      <CssBaseline />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Loan Management
        </Typography>
        <LoanForm onSuccess={handleLoanCreated} />
      </Container>
    </>
  )
}

export default App

