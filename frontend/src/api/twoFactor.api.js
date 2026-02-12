import axiosInstance from './axios';

export const initiateTwoFactor = async () => {
  const response = await axiosInstance.post('/auth/2fa/initiate');
  return response.data.data;
};

export const verifyAndEnable2FA = async (data) => {
  const response = await axiosInstance.post('/auth/2fa/verify', data);
  return response.data.data;
};

export const verifyTotpCode = async (code) => {
  const response = await axiosInstance.post('/auth/2fa/verify-code', { code });
  return response.data.data;
};

export const verifyRecoveryCode = async (code) => {
  const response = await axiosInstance.post('/auth/2fa/verify-recovery', { code });
  return response.data.data;
};

export const getTwoFactorStatus = async () => {
  const response = await axiosInstance.get('/auth/2fa/status');
  return response.data.data;
};

export const disableTwoFactor = async () => {
  const response = await axiosInstance.post('/auth/2fa/disable');
  return response.data.data;
};

export const regenerateRecoveryCodes = async () => {
  const response = await axiosInstance.post('/auth/2fa/regenerate-codes');
  return response.data.data;
};
