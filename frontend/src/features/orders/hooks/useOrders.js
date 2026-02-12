import { useQuery } from '@tanstack/react-query';
import { getOrders } from '../../../api/order.api';

/**
 * Hook to fetch paginated orders
 */
export const useOrders = (page = 0, size = 20, sort = 'orderDate,desc') => {
  return useQuery({
    queryKey: ['orders', { page, size, sort }],
    queryFn: () => getOrders({ page, size, sort }),
  });
};
