// Environment configuration
const config = {
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  appName: import.meta.env.VITE_APP_NAME || 'Inventory Management System',
  appVersion: import.meta.env.VITE_APP_VERSION || '1.0.0',
  wsBaseUrl: import.meta.env.VITE_WS_BASE_URL || 'http://localhost:8080/api/ws',
};

export default config;
