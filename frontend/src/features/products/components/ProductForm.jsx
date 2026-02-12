import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import Modal from '../../../components/common/Modal';
import Button from '../../../components/common/Button';
import { useCreateProduct } from '../hooks/useCreateProduct';
import { useUpdateProduct } from '../hooks/useUpdateProduct';
import { useActiveCategories } from '../../categories/hooks/useActiveCategories';
import { RotateCcw } from 'lucide-react';

const ProductForm = ({ isOpen, onClose, product = null }) => {
  const isEditMode = !!product;
  const createMutation = useCreateProduct();
  const updateMutation = useUpdateProduct();
  const { data: categoriesData, isLoading: categoriesLoading, error: categoriesError, refetch: refetchCategories } = useActiveCategories();

  const categories = Array.isArray(categoriesData) ? categoriesData : [];

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
  } = useForm({
    defaultValues: {
      name: '',
      sku: '',
      description: '',
      price: '',
      stockQuantity: '',
      lowStockThreshold: 10,
      categoryId: '',
      imageUrl: '',
      isActive: true,
    },
  });

  // Populate form when editing
  useEffect(() => {
    if (product) {
      setValue('name', product.name);
      setValue('sku', product.sku);
      setValue('description', product.description || '');
      setValue('price', product.price);
      setValue('stockQuantity', product.stockQuantity);
      setValue('lowStockThreshold', product.lowStockThreshold || 10);
      setValue('categoryId', product.categoryId);
      setValue('imageUrl', product.imageUrl || '');
      setValue('isActive', product.isActive);
    } else {
      reset();
    }
  }, [product, setValue, reset]);

  const onSubmit = async (data) => {
    try {
      // Convert string numbers to actual numbers
      const formattedData = {
        ...data,
        price: parseFloat(data.price),
        stockQuantity: parseInt(data.stockQuantity, 10),
        lowStockThreshold: parseInt(data.lowStockThreshold, 10),
        categoryId: parseInt(data.categoryId, 10),
      };

      if (isEditMode) {
        await updateMutation.mutateAsync({ id: product.id, data: formattedData });
      } else {
        await createMutation.mutateAsync(formattedData);
      }
      handleClose();
    } catch (error) {
      // Error already handled by mutation
    }
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  const isLoading = createMutation.isPending || updateMutation.isPending;

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={isEditMode ? 'Edit Product' : 'Create New Product'}
      size="lg"
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Name */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Product Name <span className="text-danger-500">*</span>
            </label>
            <input
              {...register('name', {
                required: 'Product name is required',
                minLength: { value: 2, message: 'Name must be at least 2 characters' },
              })}
              type="text"
              className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                errors.name ? 'border-danger-500' : 'border-gray-300'
              }`}
              placeholder="e.g., iPhone 15 Pro"
            />
            {errors.name && <p className="mt-1 text-sm text-danger-600">{errors.name.message}</p>}
          </div>

          {/* SKU */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              SKU <span className="text-danger-500">*</span>
            </label>
            <input
              {...register('sku', {
                required: 'SKU is required',
                pattern: { value: /^[A-Za-z0-9\-]+$/, message: 'SKU must be alphanumeric' },
              })}
              type="text"
              className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                errors.sku ? 'border-danger-500' : 'border-gray-300'
              }`}
              placeholder="e.g., IP15PRO-256-BLK"
            />
            {errors.sku && <p className="mt-1 text-sm text-danger-600">{errors.sku.message}</p>}
          </div>

          {/* Barcode */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Barcode Number
            </label>
            <input
              {...register('barcodeNumber')}
              type="text"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              placeholder="e.g., 8901234567890 (EAN-13, UPC, etc.)"
            />
            <p className="mt-1 text-xs text-gray-500">Optional: For barcode scanning</p>
          </div>

          {/* Price */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Price ($) <span className="text-danger-500">*</span>
            </label>
            <input
              {...register('price', {
                required: 'Price is required',
                min: { value: 0.01, message: 'Price must be greater than 0' },
              })}
              type="number"
              step="0.01"
              className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                errors.price ? 'border-danger-500' : 'border-gray-300'
              }`}
              placeholder="0.00"
            />
            {errors.price && <p className="mt-1 text-sm text-danger-600">{errors.price.message}</p>}
          </div>

          {/* Stock Quantity */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Stock Quantity <span className="text-danger-500">*</span>
            </label>
            <input
              {...register('stockQuantity', {
                required: 'Stock quantity is required',
                min: { value: 0, message: 'Stock cannot be negative' },
              })}
              type="number"
              className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                errors.stockQuantity ? 'border-danger-500' : 'border-gray-300'
              }`}
              placeholder="0"
            />
            {errors.stockQuantity && (
              <p className="mt-1 text-sm text-danger-600">{errors.stockQuantity.message}</p>
            )}
          </div>

          {/* Low Stock Threshold */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Low Stock Threshold
            </label>
            <input
              {...register('lowStockThreshold', {
                min: { value: 0, message: 'Threshold cannot be negative' },
              })}
              type="number"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              placeholder="10"
            />
          </div>

          {/* Category */}
          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="block text-sm font-medium text-gray-700">
                Category <span className="text-danger-500">*</span>
              </label>
              {categoriesError && (
                <button
                  type="button"
                  onClick={() => refetchCategories()}
                  className="text-xs text-blue-600 hover:text-blue-800 flex items-center gap-1"
                >
                  <RotateCcw className="h-3 w-3" />
                  Retry
                </button>
              )}
            </div>
            <select
              {...register('categoryId', { required: 'Category is required' })}
              disabled={categoriesLoading}
              className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:bg-gray-100 disabled:cursor-not-allowed ${
                errors.categoryId ? 'border-danger-500' : 'border-gray-300'
              }`}
            >
              <option value="">
                {categoriesLoading ? 'Loading categories...' : categories.length === 0 ? 'No categories available' : 'Select a category'}
              </option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.name}
                </option>
              ))}
            </select>
            {categoriesError && (
              <p className="mt-1 text-sm text-danger-600">Failed to load categories. Click "Retry" to reload.</p>
            )}
            {errors.categoryId && (
              <p className="mt-1 text-sm text-danger-600">{errors.categoryId.message}</p>
            )}
          </div>
        </div>

        {/* Description */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
          <textarea
            {...register('description')}
            rows={3}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            placeholder="Product description (optional)"
          />
        </div>

        {/* Image URL */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
          <input
            {...register('imageUrl')}
            type="url"
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            placeholder="https://example.com/image.jpg"
          />
        </div>

        {/* Active Status */}
        <div className="flex items-center gap-2">
          <input
            {...register('isActive')}
            type="checkbox"
            id="isActive"
            className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label htmlFor="isActive" className="text-sm font-medium text-gray-700">
            Active
          </label>
        </div>

        {/* Actions */}
        <div className="flex gap-3 justify-end pt-4 border-t">
          <Button type="button" variant="outline" onClick={handleClose} disabled={isLoading}>
            Cancel
          </Button>
          <Button type="submit" variant="primary" loading={isLoading} disabled={isLoading}>
            {isEditMode ? 'Update' : 'Create'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default ProductForm;
