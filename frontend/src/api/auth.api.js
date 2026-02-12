import axiosInstance from './axios';

export const login = async (credentials) => {
  const response = await axiosInstance.post('/auth/login', credentials);
  // axiosInstance now returns the full response object
  // response.data contains ApiResponse { success, message, data, timestamp }
  // We need to return response.data.data which contains JwtAuthenticationResponse
  return response.data.data;
};

export const register = async (userData) => {
  const response = await axiosInstance.post('/auth/register', userData);
  return response.data.data;
};


export const getCurrentUser = async () => {
  const response = await axiosInstance.get('/auth/me');
  return response.data.data;
};
