
import { useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export const AuthNavigationSync = () => {
    const location = useLocation();
    const { isAuthenticated, token, refreshUser } = useAuth();

    const lastRequest = useRef<string | null>(null);

    useEffect(() => {
        if (!isAuthenticated || !token) return;

        const requestKey = `${location.key}:${token}`;

        if (lastRequest.current === requestKey) return;

        lastRequest.current = requestKey;

        refreshUser().catch((error) => {
        console.error('Error al actualizar el usuario:', error);
        });
    }, [location.key, isAuthenticated, token, refreshUser]);

    return null;
};
