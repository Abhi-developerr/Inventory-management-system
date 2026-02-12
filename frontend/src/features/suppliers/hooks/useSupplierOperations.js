import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { getSupplierById, updateSupplier, deleteSupplier, createSupplier, linkProductToSupplier, unlinkProductFromSupplier, searchSuppliers } from '../../../api/supplier.api';
import { toast } from 'react-toastify';

export const useSupplierById = (id) => {
  return useQuery({
    queryKey: ['suppliers', id],
    queryFn: () => getSupplierById(id),
    enabled: !!id,
  });
};

export const useCreateSupplier = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createSupplier,
    onSuccess: () => {
      toast.success('Supplier created successfully');
      queryClient.invalidateQueries({ queryKey: ['suppliers'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to create supplier');
    },
  });
};

export const useUpdateSupplier = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }) => updateSupplier(id, data),
    onSuccess: (data) => {
      toast.success('Supplier updated successfully');
      queryClient.invalidateQueries({ queryKey: ['suppliers'] });
      queryClient.setQueryData(['suppliers', data.id], data);
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to update supplier');
    },
  });
};

export const useDeleteSupplier = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteSupplier,
    onSuccess: () => {
      toast.success('Supplier deleted successfully');
      queryClient.invalidateQueries({ queryKey: ['suppliers'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to delete supplier');
    },
  });
};

export const useLinkProductToSupplier = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ supplierId, productId, costPrice, supplierSku }) =>
      linkProductToSupplier(supplierId, productId, costPrice, supplierSku),
    onSuccess: () => {
      toast.success('Product linked to supplier');
      queryClient.invalidateQueries({ queryKey: ['suppliers'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to link product');
    },
  });
};

export const useUnlinkProductFromSupplier = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ supplierId, productId }) => unlinkProductFromSupplier(supplierId, productId),
    onSuccess: () => {
      toast.success('Product unlinked from supplier');
      queryClient.invalidateQueries({ queryKey: ['suppliers'] });
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to unlink product');
    },
  });
};

export const useSearchSuppliers = (query, page = 0, size = 20) => {
  return useQuery({
    queryKey: ['suppliers', 'search', query, { page, size }],
    queryFn: () => searchSuppliers(query, { page, size }),
    enabled: !!query,
  });
};
