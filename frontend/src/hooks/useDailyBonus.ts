import { useCallback, useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { useAuth } from './useAuth';
import { getDailyBonusStatus, claimDailyBonus } from '@/services/dailyBonusService';
import type { DailyBonusStatus, ClaimBonusResponse } from '@/types/bonus';
import type { HttpError } from '@/lib/http-error';

export function useDailyBonus() {
  const { isAuthenticated, refreshUser } = useAuth();
  const [status, setStatus] = useState<DailyBonusStatus | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isClaiming, setIsClaiming] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [isAvailable, setIsAvailable] = useState<boolean>(true);

  const fetchStatus = useCallback(async (): Promise<void> => {
    if (!isAuthenticated) {
      setStatus(null);
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const data = await getDailyBonusStatus();
      setStatus(data);
      setIsAvailable(true);
    } catch (err) {
      const httpErr = err as HttpError;
      // If endpoint is not found (backend branch not merged yet), fail gracefully
      if (httpErr?.status === 404) {
        setIsAvailable(false);
      } else {
        setError(httpErr?.message || 'No se pudo obtener el estado del bonus diario.');
      }
    } finally {
      setIsLoading(false);
    }
  }, [isAuthenticated]);

  const claim = useCallback(async (): Promise<ClaimBonusResponse | null> => {
    if (!isAuthenticated) return null;

    setIsClaiming(true);
    setError(null);

    try {
      const response = await claimDailyBonus();
      await refreshUser();

      setStatus((prev) => {
        if (!prev) return prev;
        return {
          ...prev,
          currentStreak: response.currentStreak,
          alreadyClaimedToday: true,
        };
      });

      toast.success(
        `¡Recompensa reclamada! Sumaste $${response.givenBonus} créditos a tu cuenta. Racha actual: ${response.currentStreak} días 🔥`
      );
      return response;
    } catch (err) {
      const httpErr = err as HttpError;
      const message = httpErr?.message || 'Ocurrió un error al reclamar el bonus diario.';
      setError(message);
      toast.error(message);
      return null;
    } finally {
      setIsClaiming(false);
    }
  }, [isAuthenticated, refreshUser]);

  useEffect(() => {
    void fetchStatus();
  }, [fetchStatus]);

  return {
    status,
    isLoading,
    isClaiming,
    error,
    isAvailable,
    claim,
    refetch: fetchStatus,
  };
}
