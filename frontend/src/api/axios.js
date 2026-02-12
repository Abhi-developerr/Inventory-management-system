import axios from 'axios';
import config from '../config';
import { getAuthToken, clearAuthData } from '../utils/storage';
import { toast } from 'react-toastify';

// Create axios instance
const axiosInstance = axios.create({
  baseURL: config.apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - attach JWT token
axiosInstance.interceptors.request.use(
  (config) => {
    const token = getAuthToken();
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - handle errors globally
axiosInstance.interceptors.response.use(
  (response) => {
    // Return the full response object so callers can access response.data (ApiResponse)
    return response;
  },
  (error) => {
    // Handle network errors
    if (!error.response) {
      toast.error('Network error. Please check your connection.');
      return Promise.reject(new Error('Network error'));
    }

    const { status, data } = error.response;

    // Handle 401 Unauthorized - token expired or invalid
    if (status === 401) {
      clearAuthData();
      window.location.href = '/login';
      toast.error('Session expired. Please login again.');
      return Promise.reject(new Error('Unauthorized'));
    }

    // Handle 403 Forbidden - insufficient permissions
    if (status === 403) {
      toast.error('You do not have permission to perform this action.');
      return Promise.reject(new Error('Forbidden'));
    }

    // Handle 404 Not Found
    if (status === 404) {
      toast.error(data?.message || 'Resource not found.');
      return Promise.reject(new Error('Not found'));
    }

    // Handle 500 Internal Server Error
    if (status >= 500) {
      toast.error('Server error. Please try again later.');
      return Promise.reject(new Error('Server error'));
    }

    // Handle other errors (400, 422, etc.)
    const errorMessage = data?.message || 'An error occurred';
    toast.error(errorMessage);
    
    return Promise.reject(error);
  }
);

export default axiosInstance;
