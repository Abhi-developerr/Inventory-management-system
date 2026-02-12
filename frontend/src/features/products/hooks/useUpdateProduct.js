import { useMutation, useQueryClient } from '@tanstack/react-query';
import { updateProduct } from '../../../api/product.api';
import { toast } from 'react-toastify';

/**
 * Hook to update a product
 */
export const useUpdateProduct = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }) => updateProduct(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
      toast.success('Product updated successfully!');
    },
    onError: (error) => {
      console.error('Update product error:', error);
    },
  });
};
