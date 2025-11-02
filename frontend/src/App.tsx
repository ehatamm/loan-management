import { useState } from 'react'
import { CssBaseline, Container, Typography } from '@mui/material'
import { LoanForm } from './features/loan/LoanForm'
import { ScheduleDisplay } from './features/loan/ScheduleDisplay'

function App() {
  const [selectedLoanId, setSelectedLoanId] = useState<string | null>(null);

  const handleLoanCreated = (loanId: string) => {
    setSelectedLoanId(loanId);
  };

  return (
    <>
      <CssBaseline />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Loan Management
        </Typography>
        <LoanForm onSuccess={handleLoanCreated} />
        {selectedLoanId && <ScheduleDisplay loanId={selectedLoanId} />}
      </Container>
    </>
  )
}

export default App

