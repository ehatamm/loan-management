import { CssBaseline } from '@mui/material';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { Layout } from './components/Layout';
import { LoansPage } from './pages/LoansPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      {
        index: true,
        element: <LoansPage />,
      },
      {
        path: 'loans/new',
        element: <LoansPage />,
      },
      {
        path: 'loans/:id/schedule',
        element: <LoansPage />,
      },
    ],
  },
]);

function App() {
  return (
    <>
      <CssBaseline />
      <RouterProvider router={router} />
    </>
  );
}

export default App;

