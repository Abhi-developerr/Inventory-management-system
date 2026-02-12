import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import Modal from '../../../components/common/Modal';
import Input from '../../../components/common/Input';
import Button from '../../../components/common/Button';
import { useCreateSupplier, useUpdateSupplier } from '../hooks/useSupplierOperations';

const SupplierForm = ({ isOpen, onClose, supplier }) => {
  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    defaultValues: supplier || {},
  });

  const createMutation = useCreateSupplier();
  const updateMutation = useUpdateSupplier();

  useEffect(() => {
    if (supplier) {
      reset(supplier);
    } else {
      reset({});
    }
  }, [supplier, reset]);

  const onSubmit = async (data) => {
    try {
      if (supplier) {
        await updateMutation.mutateAsync({ id: supplier.id, data });
      } else {
        await createMutation.mutateAsync(data);
      }
      reset();
      onClose();
    } catch (error) {
      // Error handled by mutation
    }
  };

  const isLoading = createMutation.isPending || updateMutation.isPending;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={supplier ? 'Edit Supplier' : 'Create New Supplier'}
      size="lg"
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Supplier Name"
            placeholder="e.g., ABC Electronics"
            {...register('name', { required: 'Name is required' })}
            error={errors.name?.message}
            disabled={isLoading}
          />
          <Input
            label="Supplier Code"
            placeholder="e.g., SUP001"
            {...register('code', { required: 'Code is required' })}
            error={errors.code?.message}
            disabled={isLoading}
          />
        </div>

        <div>
          <Input
            label="Description"
            placeholder="Brief description about the supplier"
            {...register('description')}
            error={errors.description?.message}
            disabled={isLoading}
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Contact Person"
            placeholder="John Doe"
            {...register('contactPerson')}
            error={errors.contactPerson?.message}
            disabled={isLoading}
          />
          <Input
            label="Email"
            type="email"
            placeholder="contact@supplier.com"
            {...register('contactEmail')}
            error={errors.contactEmail?.message}
            disabled={isLoading}
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Phone"
            type="tel"
            placeholder="+1-XXX-XXX-XXXX"
            {...register('contactPhone')}
            error={errors.contactPhone?.message}
            disabled={isLoading}
          />
          <Input
            label="Address"
            placeholder="Full address"
            {...register('address')}
            error={errors.address?.message}
            disabled={isLoading}
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Input
            label="Lead Time (days)"
            type="number"
            placeholder="7"
            {...register('leadTimeDays', { required: 'Lead time is required', min: 1 })}
            error={errors.leadTimeDays?.message}
            disabled={isLoading}
          />
          <Input
            label="Minimum Order Qty"
            type="number"
            placeholder="10"
            {...register('minimumOrderQuantity', { required: 'MOQ is required', min: 1 })}
            error={errors.minimumOrderQuantity?.message}
            disabled={isLoading}
          />
          <Input
            label="Rating"
            type="number"
            step="0.1"
            min="0"
            max="5"
            placeholder="4.5"
            {...register('rating')}
            error={errors.rating?.message}
            disabled={isLoading}
          />
        </div>

        <div>
          <Input
            label="Payment Terms"
            placeholder="Net 30, Net 60, etc."
            {...register('paymentTerms')}
            error={errors.paymentTerms?.message}
            disabled={isLoading}
          />
        </div>

        <div className="flex gap-3 justify-end pt-4">
          <Button variant="outline" onClick={onClose} disabled={isLoading}>
            Cancel
          </Button>
          <Button type="submit" loading={isLoading}>
            {supplier ? 'Update' : 'Create'} Supplier
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default SupplierForm;
