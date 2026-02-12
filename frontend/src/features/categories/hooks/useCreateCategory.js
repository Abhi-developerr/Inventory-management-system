import { useMutation, useQueryClient } from '@tanstack/react-query';
import { createCategory } from '../../../api/category.api';
import { toast } from 'react-toastify';

/**
 * Hook to create a new category
 */
export const useCreateCategory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createCategory,
    onSuccess: () => {
      // Invalidate categories queries to refetch
      queryClient.invalidateQueries({ queryKey: ['categories'] });
      toast.success('Category created successfully!');
    },
    onError: (error) => {
      console.error('Create category error:', error);
      // Toast already shown by axios interceptor
    },
  });
};
