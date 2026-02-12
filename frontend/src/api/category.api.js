import axiosInstance from './axios';

export const getCategories = async (params = {}) => {
  const { page = 0, size = 20, sort = 'name,asc' } = params;
  const response = await axiosInstance.get('/categories', {
    params: { page, size, sort },
  });
  return response.data.data; // Page<CategoryResponse>
};

export const getActiveCategories = async () => {
  try {
    const response = await axiosInstance.get('/categories/active');
    const data = response.data?.data;
    // Ensure we return an array
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error('Error fetching active categories:', error);
    return [];
  }
};

export const getCategoryById = async (id) => {
  const response = await axiosInstance.get(`/categories/${id}`);
  return response.data.data; // CategoryResponse
};

export const createCategory = async (categoryData) => {
  const response = await axiosInstance.post('/categories', categoryData);
  return response.data.data; // CategoryResponse
};

export const updateCategory = async (id, categoryData) => {
  const response = await axiosInstance.put(`/categories/${id}`, categoryData);
  return response.data.data; // CategoryResponse
};

export const deleteCategory = async (id) => {
  await axiosInstance.delete(`/categories/${id}`);
};
