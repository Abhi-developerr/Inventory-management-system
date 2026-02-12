import axiosInstance from './axios';

export const getPurchaseOrders = async (params = {}) => {
  const { page = 0, size = 20, sort = 'createdAt,desc' } = params;
  const response = await axiosInstance.get('/purchase-orders', {
    params: { page, size, sort },
  });
  return response.data.data;
};

export const getPurchaseOrderById = async (id) => {
  const response = await axiosInstance.get(`/purchase-orders/${id}`);
  return response.data.data;
};

export const getPurchaseOrdersByStatus = async (status, params = {}) => {
  const { page = 0, size = 20, sort = 'createdAt,desc' } = params;
  const response = await axiosInstance.get(`/purchase-orders/status/${status}`, {
    params: { page, size, sort },
  });
  return response.data.data;
};

export const createPurchaseOrder = async (poData) => {
  const response = await axiosInstance.post('/purchase-orders', poData);
  return response.data.data;
};

export const approvePurchaseOrder = async (id) => {
  const response = await axiosInstance.post(`/purchase-orders/${id}/approve`);
  return response.data.data;
};

export const receivePurchaseOrder = async (id) => {
  const response = await axiosInstance.post(`/purchase-orders/${id}/receive`);
  return response.data.data;
};

export const cancelPurchaseOrder = async (id) => {
  const response = await axiosInstance.post(`/purchase-orders/${id}/cancel`);
  return response.data.data;
};
