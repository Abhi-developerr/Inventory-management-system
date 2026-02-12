import { useQuery } from '@tanstack/react-query';
import { getCategories } from '../../../api/category.api';

/**
 * Hook to fetch paginated categories
 */
export const useCategories = (page = 0, size = 20, sort = 'name,asc') => {
  return useQuery({
    queryKey: ['categories', { page, size, sort }],
    queryFn: () => getCategories({ page, size, sort }),
  });
};
