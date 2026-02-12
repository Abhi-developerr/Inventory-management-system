import axiosInstance from './axios';

export const getProducts = async (params = {}) => {
  const { page = 0, size = 20, sort = 'name,asc' } = params;
  const response = await axiosInstance.get('/products', {
    params: { page, size, sort },
  });
  return response.data.data; // Page<ProductResponse>
};

export const getProductById = async (id) => {
  const response = await axiosInstance.get(`/products/${id}`);
  return response.data.data; // ProductResponse
};

export const getProductsByCategory = async (categoryId, params = {}) => {
  const { page = 0, size = 20, sort = 'name,asc' } = params;
  const response = await axiosInstance.get(`/products/category/${categoryId}`, {
    params: { page, size, sort },
  });
  return response.data.data;
};

export const searchProducts = async (query, params = {}) => {
  const { page = 0, size = 20, sort = 'name,asc' } = params;
  const response = await axiosInstance.get('/products/search', {
    params: { query, page, size, sort },
  });
  return response.data.data;
};

export const getLowStockProducts = async () => {
  const response = await axiosInstance.get('/products/low-stock');
  return response.data.data; // List<ProductResponse>
};

export const createProduct = async (productData) => {
  const response = await axiosInstance.post('/products', productData);
  return response.data.data; // ProductResponse
};

export const updateProduct = async (id, productData) => {
  const response = await axiosInstance.put(`/products/${id}`, productData);
  return response.data.data; // ProductResponse
};

export const deleteProduct = async (id) => {
  await axiosInstance.delete(`/products/${id}`);
};

export const updateProductStock = async (id, quantity) => {
  const response = await axiosInstance.patch(`/products/${id}/stock`, null, {
    params: { quantity },
  });
  return response.data;
};
