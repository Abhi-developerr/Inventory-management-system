import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createProduct } from '../../../api/product.api';
import { toast } from 'react-toastify';

/**
 * Hook to create a new product
 */
export const useCreateProduct = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] });
      toast.success('Product created successfully!');
    },
    onError: (error) => {
      console.error('Create product error:', error);
    },
  });
};
