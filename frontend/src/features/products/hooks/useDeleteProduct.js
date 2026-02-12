import { useMutation, useQueryClient } from '@tanstack/react-query';
import { deleteProduct } from '../../../api/product.api';
import { toast } from 'react-toastify';

/**
 * Hook to delete a product
 */
export const useDeleteProduct = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
      toast.success('Product deleted successfully!');
    },
    onError: (error) => {
      console.error('Delete product error:', error);
    },
  });
};
