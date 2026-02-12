import { useQuery } from '@tanstack/react-query';
import { getProducts } from '../../../api/product.api';

/**
 * Hook to fetch paginated products
 */
export const useProducts = (page = 0, size = 20, sort = 'name,asc') => {
  return useQuery({
    queryKey: ['products', { page, size, sort }],
    queryFn: () => getProducts({ page, size, sort }),
  });
};
