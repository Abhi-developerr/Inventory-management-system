import { STORAGE_KEYS } from './constants';

export const getStorageItem = (key, defaultValue = null) => {
  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : defaultValue;
  } catch (error) {
    console.error(`Error getting item from localStorage: ${key}`, error);
    return defaultValue;
  }
};

export const setStorageItem = (key, value) => {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    console.error(`Error setting item in localStorage: ${key}`, error);
  }
};

export const removeStorageItem = (key) => {
  try {
    localStorage.removeItem(key);
  } catch (error) {
    console.error(`Error removing item from localStorage: ${key}`, error);
  }
};

export const clearStorage = () => {
  try {
    localStorage.clear();
  } catch (error) {
    console.error('Error clearing localStorage', error);
  }
};

export const getAuthToken = () => {
  return getStorageItem(STORAGE_KEYS.AUTH_TOKEN);
};

export const setAuthToken = (token) => {
  setStorageItem(STORAGE_KEYS.AUTH_TOKEN, token);
};

export const removeAuthToken = () => {
  removeStorageItem(STORAGE_KEYS.AUTH_TOKEN);
};

export const getUserData = () => {
  return getStorageItem(STORAGE_KEYS.USER_DATA);
};

export const setUserData = (userData) => {
  setStorageItem(STORAGE_KEYS.USER_DATA, userData);
};

export const removeUserData = () => {
  removeStorageItem(STORAGE_KEYS.USER_DATA);
};

export const clearAuthData = () => {
  removeAuthToken();
  removeUserData();
};
