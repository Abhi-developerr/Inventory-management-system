import { useQuery } from '@tanstack/react-query';
import { getActiveCategories } from '../../../api/category.api';

/**
 * Hook to fetch active categories (for dropdowns)
 */
export const useActiveCategories = () => {
  return useQuery({
    queryKey: ['categories', 'active'],
    queryFn: getActiveCategories,
    staleTime: 5 * 60 * 1000, // 5 minutes
    gcTime: 10 * 60 * 1000, // 10 minutes (previously cacheTime)
    retry: 2,
  });
};
