import axiosInstance from './axios';

export const getSuppliers = async (params = {}) => {
  const { page = 0, size = 20, sort = 'name,asc' } = params;
  const response = await axiosInstance.get('/suppliers', {
    params: { page, size, sort },
  });
  return response.data.data;
};

export const getSupplierById = async (id) => {
  const response = await axiosInstance.get(`/suppliers/${id}`);
  return response.data.data;
};

export const searchSuppliers = async (query, params = {}) => {
  const { page = 0, size = 20, sort = 'name,asc' } = params;
  const response = await axiosInstance.get('/suppliers/search', {
    params: { query, page, size, sort },
  });
  return response.data.data;
};

export const createSupplier = async (supplierData) => {
  const response = await axiosInstance.post('/suppliers', supplierData);
  return response.data.data;
};

export const updateSupplier = async (id, supplierData) => {
  const response = await axiosInstance.put(`/suppliers/${id}`, supplierData);
  return response.data.data;
};

export const deleteSupplier = async (id) => {
  await axiosInstance.delete(`/suppliers/${id}`);
};

export const linkProductToSupplier = async (supplierId, productId, costPrice, supplierSku = null) => {
  const params = { costPrice };
  if (supplierSku) {
    params.supplierSku = supplierSku;
  }
  const response = await axiosInstance.post(`/suppliers/${supplierId}/products/${productId}`, null, { params });
  return response.data;
};

export const unlinkProductFromSupplier = async (supplierId, productId) => {
  await axiosInstance.delete(`/suppliers/${supplierId}/products/${productId}`);
};
