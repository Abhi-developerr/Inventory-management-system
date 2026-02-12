import { useQuery } from '@tanstack/react-query';
import { getOrderById } from '../../../api/order.api';

/**
 * Hook to fetch a single order by ID
 */
export const useOrderById = (orderId) => {
  return useQuery({
    queryKey: ['orders', orderId],
    queryFn: () => getOrderById(orderId),
    enabled: !!orderId,
  });
};
