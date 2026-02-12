import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  generateReport,
  getReports,
  getReportById,
  deleteReport,
  scheduleReport,
  downloadReport,
} from '../../../api/report.api';
import { toast } from 'react-toastify';

export const useReports = (page = 0, size = 20) => {
  return useQuery({
    queryKey: ['reports', { page, size }],
    queryFn: () => getReports({ page, size }),
  });
};

export const useReportById = (id) => {
  return useQuery({
    queryKey: ['reports', id],
    queryFn: () => getReportById(id),
    enabled: !!id,
  });
};

export const useGenerateReport = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: generateReport,
    onSuccess: (data) => {
      toast.success('Report generated successfully');
      queryClient.invalidateQueries({ queryKey: ['reports'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to generate report');
    },
  });
};

export const useDeleteReport = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteReport,
    onSuccess: () => {
      toast.success('Report deleted successfully');
      queryClient.invalidateQueries({ queryKey: ['reports'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to delete report');
    },
  });
};

export const useScheduleReport = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, frequency, emailRecipients }) =>
      scheduleReport(id, frequency, emailRecipients),
    onSuccess: () => {
      toast.success('Report scheduled successfully');
      queryClient.invalidateQueries({ queryKey: ['reports'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to schedule report');
    },
  });
};

export const useDownloadReport = () => {
  return useMutation({
    mutationFn: downloadReport,
    onSuccess: (blob, id) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `report-${id}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success('Report downloaded');
    },
    onError: (error) => {
      toast.error('Failed to download report');
    },
  });
};
