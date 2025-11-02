import { CssBaseline, Container, Typography } from '@mui/material'

function App() {
  return (
    <>
      <CssBaseline />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Loan Management
        </Typography>
      </Container>
    </>
  )
}

export default App

