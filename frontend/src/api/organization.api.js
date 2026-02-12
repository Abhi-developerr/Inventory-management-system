import axiosInstance from './axios';

const organizationApi = {
  get: (url, config) => axiosInstance.get(url, config),
  post: (url, data) => axiosInstance.post(url, data),
  put: (url, data) => axiosInstance.put(url, data),
  delete: (url) => axiosInstance.delete(url),
  patch: (url, data) => axiosInstance.patch(url, data),
};

export const getOrganizations = (params) => axiosInstance.get('/organizations', { params });
export const getOrganizationById = (id) => axiosInstance.get(`/organizations/${id}`);
export const createOrganization = (data) => axiosInstance.post('/organizations', data);
export const updateOrganization = (id, data) => axiosInstance.put(`/organizations/${id}`, data);
export const deleteOrganization = (id) => axiosInstance.delete(`/organizations/${id}`);
export const toggleOrganizationStatus = (id) => axiosInstance.patch(`/organizations/${id}/toggle-status`);

export default organizationApi;
