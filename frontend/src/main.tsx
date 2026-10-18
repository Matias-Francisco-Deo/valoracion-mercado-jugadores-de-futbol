
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { AppRoutes } from './routes/AppRoutes';
import './index.css';
import { AuthNavigationSync } from './context/AuthNavigationSync';

createRoot(document.getElementById('root')!).render(
    <BrowserRouter>
      <AuthProvider>
        <AuthNavigationSync/>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
);
