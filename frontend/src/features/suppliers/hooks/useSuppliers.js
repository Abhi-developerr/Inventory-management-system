import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { getSuppliers } from '../../../api/supplier.api';

export const useSuppliers = (page = 0, size = 20, sort = 'name,asc') => {
  return useQuery({
    queryKey: ['suppliers', { page, size, sort }],
    queryFn: () => getSuppliers({ page, size, sort }),
  });
};
