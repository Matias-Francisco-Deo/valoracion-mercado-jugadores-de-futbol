import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { AuthProvider } from './context/AuthContext';
import { AppRoutes } from './routes/AppRoutes';
import './index.css';
import { AuthNavigationSync } from './context/AuthNavigationSync';

createRoot(document.getElementById('root')!).render(
    <BrowserRouter>
      <AuthProvider>
        <AuthNavigationSync/>
        <AppRoutes />
        <ToastContainer position="top-center" autoClose={3000} hideProgressBar={false} closeOnClick pauseOnHover />
      </AuthProvider>
    </BrowserRouter>
);
