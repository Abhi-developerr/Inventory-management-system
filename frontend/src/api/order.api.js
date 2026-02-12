import axiosInstance from './axios';

export const getOrders = async (params = {}) => {
  const { page = 0, size = 20, sort = 'orderDate,desc' } = params;
  const response = await axiosInstance.get('/orders', {
    params: { page, size, sort },
  });
  return response.data.data; // Page<OrderResponse>
};

export const getOrderById = async (id) => {
  const response = await axiosInstance.get(`/orders/${id}`);
  return response.data.data; // OrderResponse
};

export const getOrdersByStatus = async (status, params = {}) => {
  const { page = 0, size = 20, sort = 'orderDate,desc' } = params;
  const response = await axiosInstance.get(`/orders/status/${status}`, {
    params: { page, size, sort },
  });
  return response.data.data;
};

export const searchOrders = async (query, params = {}) => {
  const { page = 0, size = 20, sort = 'orderDate,desc' } = params;
  const response = await axiosInstance.get('/orders/search', {
    params: { query, page, size, sort },
  });
  return response.data.data;
};

export const createOrder = async (orderData) => {
  const response = await axiosInstance.post('/orders', orderData);
  return response.data.data; // OrderResponse
};

export const updateOrderStatus = async (id, status) => {
  const response = await axiosInstance.patch(`/orders/${id}/status`, { status });
  return response.data.data; // OrderResponse
};

export const cancelOrder = async (id) => {
  const response = await axiosInstance.post(`/orders/${id}/cancel`);
  return response.data.data;
};
