import { useMutation, useQueryClient } from '@tanstack/react-query';
import { deleteCategory } from '../../../api/category.api';
import { toast } from 'react-toastify';

/**
 * Hook to delete a category
 */
export const useDeleteCategory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteCategory,
    onSuccess: () => {
      // Invalidate categories queries to refetch
      queryClient.invalidateQueries({ queryKey: ['categories'] });
      toast.success('Category deleted successfully!');
    },
    onError: (error) => {
      console.error('Delete category error:', error);
      // Toast already shown by axios interceptor
    },
  });
};
