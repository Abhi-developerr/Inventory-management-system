import { useMutation, useQueryClient } from '@tanstack/react-query';
import { updateCategory } from '../../../api/category.api';
import { toast } from 'react-toastify';

/**
 * Hook to update a category
 */
export const useUpdateCategory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }) => updateCategory(id, data),
    onSuccess: () => {
      // Invalidate categories queries to refetch
      queryClient.invalidateQueries({ queryKey: ['categories'] });
      toast.success('Category updated successfully!');
    },
    onError: (error) => {
      console.error('Update category error:', error);
      // Toast already shown by axios interceptor
    },
  });
};
