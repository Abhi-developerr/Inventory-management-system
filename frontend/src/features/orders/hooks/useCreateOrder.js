import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createOrder } from '../../../api/order.api';
import { toast } from 'react-toastify';

/**
 * Hook to create a new order
 */
export const useCreateOrder = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createOrder,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: ['products'] }); // Refresh products due to stock change
      toast.success('Order created successfully!');
    },
    onError: (error) => {
      console.error('Create order error:', error);
    },
  });
};
