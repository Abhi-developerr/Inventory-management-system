import { useMutation, useQuery } from '@tanstack/react-query';
import {
  initiateTwoFactor,
  verifyAndEnable2FA,
  verifyTotpCode,
  verifyRecoveryCode,
  getTwoFactorStatus,
  disableTwoFactor,
  regenerateRecoveryCodes,
} from '../../../api/twoFactor.api';
import { toast } from 'react-toastify';

export const useTwoFactorStatus = () => {
  return useQuery({
    queryKey: ['twoFactorStatus'],
    queryFn: getTwoFactorStatus,
  });
};

export const useInitiateTwoFactor = () => {
  return useMutation({
    mutationFn: initiateTwoFactor,
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to initiate 2FA');
    },
  });
};

export const useVerifyAndEnable2FA = () => {
  return useMutation({
    mutationFn: verifyAndEnable2FA,
    onSuccess: () => {
      toast.success('2FA enabled successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to enable 2FA');
    },
  });
};

export const useVerifyTotpCode = () => {
  return useMutation({
    mutationFn: verifyTotpCode,
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Invalid TOTP code');
    },
  });
};

export const useVerifyRecoveryCode = () => {
  return useMutation({
    mutationFn: verifyRecoveryCode,
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Invalid recovery code');
    },
  });
};

export const useDisableTwoFactor = () => {
  return useMutation({
    mutationFn: disableTwoFactor,
    onSuccess: () => {
      toast.success('2FA disabled successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to disable 2FA');
    },
  });
};

export const useRegenerateRecoveryCodes = () => {
  return useMutation({
    mutationFn: regenerateRecoveryCodes,
    onSuccess: () => {
      toast.success('Recovery codes regenerated');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to regenerate codes');
    },
  });
};
