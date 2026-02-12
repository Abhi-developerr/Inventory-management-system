import { useQuery } from '@tanstack/react-query';
import { getLowStockProducts } from '../../../api/product.api';

/**
 * Hook to fetch low stock products
 */
export const useLowStockProducts = () => {
  return useQuery({
    queryKey: ['products', 'low-stock'],
    queryFn: getLowStockProducts,
    refetchInterval: 60000, // Refetch every minute
  });
};
