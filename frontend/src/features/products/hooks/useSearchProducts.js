import { useQuery } from '@tanstack/react-query';
import { searchProducts } from '../../../api/product.api';

/**
 * Hook to search products
 */
export const useSearchProducts = (query, page = 0, size = 20, sort = 'name,asc', enabled = true) => {
  return useQuery({
    queryKey: ['products', 'search', { query, page, size, sort }],
    queryFn: () => searchProducts(query, { page, size, sort }),
    enabled: enabled && query.length > 0,
  });
};
