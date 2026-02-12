import axios from './axios';

/**
 * Analytics API
 */
export const analyticsAPI = {
  getDashboardAnalytics: () => axios.get('/analytics/dashboard'),
  
  getTopProducts: (limit = 10) => axios.get(`/analytics/top-products?limit=${limit}`),
  
  getSalesTrend: (days = 30) => axios.get(`/analytics/sales-trend?days=${days}`),
  
  getInventoryForecast: (productId, daysToForecast = 14) =>
    axios.get(`/analytics/forecast/${productId}?daysToForecast=${daysToForecast}`),
  
  getLowStockAlerts: () => axios.get('/analytics/low-stock-alerts'),
  
  getOrderStatusBreakdown: () => axios.get('/analytics/order-status-breakdown'),
};

/**
 * Notifications API
 */
export const notificationsAPI = {
  getNotifications: (page = 0, size = 10) =>
    axios.get(`/notifications?page=${page}&size=${size}`),
  
  getUnread: (page = 0, size = 10) =>
    axios.get(`/notifications/unread?page=${page}&size=${size}`),
  
  getUnreadCount: () => axios.get('/notifications/unread/count'),
  
  markAsRead: (id) => axios.patch(`/notifications/${id}/read`),
  
  markAllAsRead: () => axios.patch('/notifications/read-all'),
  
  delete: (id) => axios.delete(`/notifications/${id}`),
};

/**
 * Audit API
 */
export const auditAPI = {
  getAuditLogs: (page = 0, size = 10) =>
    axios.get(`/audit?page=${page}&size=${size}`),
  
  getEntityAuditLogs: (entityType, entityId, page = 0, size = 10) =>
    axios.get(`/audit/${entityType}/${entityId}?page=${page}&size=${size}`),
};

/**
 * Export API
 */
export const exportAPI = {
  exportProductsToExcel: () =>
    axios.get('/export/products/excel', { responseType: 'blob' }),
  
  exportProductsToCSV: () =>
    axios.get('/export/products/csv', { responseType: 'blob' }),
  
  exportOrdersToExcel: () =>
    axios.get('/export/orders/excel', { responseType: 'blob' }),
};

/**
 * Bulk Operations API
 */
export const bulkAPI = {
  bulkCreateProducts: (products) =>
    axios.post('/bulk/products', products),
  
  bulkUpdateProducts: (updates) =>
    axios.put('/bulk/products', updates),
  
  bulkDeleteProducts: (productIds) =>
    axios.delete('/bulk/products', { data: productIds }),
  
  bulkUpdateStock: (stockUpdates) =>
    axios.put('/bulk/products/stock', stockUpdates),
};
