import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { getPurchaseOrders, getPurchaseOrderById, approvePurchaseOrder, receivePurchaseOrder, cancelPurchaseOrder, createPurchaseOrder } from '../../../api/purchaseOrder.api';
import { toast } from 'react-toastify';

export const usePurchaseOrders = (page = 0, size = 20) => {
  return useQuery({
    queryKey: ['purchaseOrders', { page, size }],
    queryFn: () => getPurchaseOrders({ page, size }),
  });
};

export const usePurchaseOrderById = (id) => {
  return useQuery({
    queryKey: ['purchaseOrders', id],
    queryFn: () => getPurchaseOrderById(id),
    enabled: !!id,
  });
};

export const useCreatePurchaseOrder = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createPurchaseOrder,
    onSuccess: () => {
      toast.success('Purchase order created successfully');
      queryClient.invalidateQueries({ queryKey: ['purchaseOrders'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to create purchase order');
    },
  });
};

export const useApprovePurchaseOrder = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: approvePurchaseOrder,
    onSuccess: () => {
      toast.success('Purchase order approved');
      queryClient.invalidateQueries({ queryKey: ['purchaseOrders'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to approve purchase order');
    },
  });
};

export const useReceivePurchaseOrder = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: receivePurchaseOrder,
    onSuccess: () => {
      toast.success('Purchase order received and stock updated');
      queryClient.invalidateQueries({ queryKey: ['purchaseOrders'] });
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to receive purchase order');
    },
  });
};

export const useCancelPurchaseOrder = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: cancelPurchaseOrder,
    onSuccess: () => {
      toast.success('Purchase order cancelled');
      queryClient.invalidateQueries({ queryKey: ['purchaseOrders'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to cancel purchase order');
    },
  });
};
