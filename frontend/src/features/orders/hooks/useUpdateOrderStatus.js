import { useMutation, useQueryClient } from '@tanstack/react-query';
import { updateOrderStatus } from '../../../api/order.api';
import { toast } from 'react-toastify';

/**
 * Hook to update order status
 */
export const useUpdateOrderStatus = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, status }) => updateOrderStatus(id, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      toast.success('Order status updated successfully!');
    },
    onError: (error) => {
      console.error('Update order status error:', error);
    },
  });
};
