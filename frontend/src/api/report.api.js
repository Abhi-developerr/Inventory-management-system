import axiosInstance from './axios';

export const generateReport = async (reportRequest) => {
  const response = await axiosInstance.post('/reports/generate', reportRequest);
  return response.data.data;
};

export const getReports = async (params = {}) => {
  const { page = 0, size = 20, sort = 'createdAt,desc' } = params;
  const response = await axiosInstance.get('/reports', {
    params: { page, size, sort },
  });
  return response.data;
};

export const getReportById = async (id) => {
  const response = await axiosInstance.get(`/reports/${id}`);
  return response.data.data;
};

export const deleteReport = async (id) => {
  const response = await axiosInstance.delete(`/reports/${id}`);
  return response.data.data;
};

export const scheduleReport = async (id, frequency, emailRecipients) => {
  const response = await axiosInstance.post(`/reports/${id}/schedule`, null, {
    params: { frequency, emailRecipients },
  });
  return response.data.data;
};

export const downloadReport = async (id) => {
  const response = await axiosInstance.get(`/reports/${id}/download`, {
    responseType: 'blob',
  });
  return response.data;
};
