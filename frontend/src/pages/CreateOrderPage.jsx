import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { ArrowLeft, Plus, Trash2, Scan } from 'lucide-react';
import Button from '../components/common/Button';
import { useCreateOrder } from '../features/orders/hooks/useCreateOrder';
import { useProducts } from '../features/products/hooks/useProducts';
import { formatCurrency } from '../utils/formatters';
import BarcodeScannerModal from '../features/barcode/components/BarcodeScannerModal';

const CreateOrderPage = () => {
  const navigate = useNavigate();
  const createMutation = useCreateOrder();
  const { data: productsData } = useProducts(0, 100, 'name,asc');
  const products = productsData?.content || [];

  const [orderItems, setOrderItems] = useState([]);
  const [scannerOpen, setScannerOpen] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();

  const handleAddItem = () => {
    setOrderItems([...orderItems, { productId: '', quantity: 1, unitPrice: 0 }]);
  };

  const handleRemoveItem = (index) => {
    setOrderItems(orderItems.filter((_, i) => i !== index));
  };

  const handleItemChange = (index, field, value) => {
    const updated = [...orderItems];
    updated[index][field] = value;

    // Auto-fill unit price when product is selected
    if (field === 'productId') {
      const product = products.find(p => p.id === parseInt(value));
      if (product) {
        updated[index].unitPrice = product.price;
      }
    }

    setOrderItems(updated);
  };

  const handleProductScanned = (product) => {
    // Check if product already in order
    const existingIndex = orderItems.findIndex(item => item.productId === product.id.toString());
    
    if (existingIndex >= 0) {
      // Increment quantity
      const updated = [...orderItems];
      updated[existingIndex].quantity += 1;
      setOrderItems(updated);
    } else {
      // Add new item
      setOrderItems([
        ...orderItems,
        { productId: product.id.toString(), quantity: 1, unitPrice: product.price }
      ]);
    }
  };

  const calculateTotal = () => {
    return orderItems.reduce((sum, item) => {
      return sum + (parseFloat(item.unitPrice) || 0) * (parseInt(item.quantity) || 0);
    }, 0);
  };

  const onSubmit = async (data) => {
    if (orderItems.length === 0) {
      alert('Please add at least one item to the order');
      return;
    }

    const orderData = {
      customerName: data.customerName,
      customerEmail: data.customerEmail || undefined,
      customerPhone: data.customerPhone || undefined,
      shippingAddress: data.shippingAddress || undefined,
      notes: data.notes || undefined,
      items: orderItems.map(item => ({
        productId: parseInt(item.productId),
        quantity: parseInt(item.quantity),
        unitPrice: parseFloat(item.unitPrice),
      })),
    };

    try {
      await createMutation.mutateAsync(orderData);
      navigate('/orders');
    } catch (error) {
      // Error already handled
    }
  };

  return (
    <div>
      {/* Header */}
      <div className="mb-6">
        <button
          onClick={() => navigate('/orders')}
          className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-4"
        >
          <ArrowLeft className="h-4 w-4" />
          Back to Orders
        </button>
        <h1 className="text-3xl font-bold text-gray-900">Create New Order</h1>
        <p className="text-gray-600 mt-1">Fill in the order details</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        {/* Customer Information */}
        <div className="bg-white rounded-lg shadow-soft p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Customer Information</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Customer Name <span className="text-danger-500">*</span>
              </label>
              <input
                {...register('customerName', { required: 'Customer name is required' })}
                type="text"
                className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 ${
                  errors.customerName ? 'border-danger-500' : 'border-gray-300'
                }`}
                placeholder="John Doe"
              />
              {errors.customerName && (
                <p className="mt-1 text-sm text-danger-600">{errors.customerName.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Email
              </label>
              <input
                {...register('customerEmail')}
                type="email"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                placeholder="john@example.com"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Phone
              </label>
              <input
                {...register('customerPhone')}
                type="tel"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                placeholder="+1 234 567 8900"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Shipping Address
              </label>
              <input
                {...register('shippingAddress')}
                type="text"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                placeholder="123 Main St, City, State"
              />
            </div>
          </div>

          <div className="mt-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Notes
            </label>
            <textarea
              {...register('notes')}
              rows={2}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              placeholder="Additional notes (optional)"
            />
          </div>
        </div>

        {/* Order Items */}
        <div className="bg-white rounded-lg shadow-soft p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Order Items</h2>
            <div className="flex gap-2">
              <Button
                type="button"
                variant="secondary"
                size="sm"
                onClick={() => setScannerOpen(true)}
                icon={Scan}
                className="gap-2"
              >
                Scan Barcode
              </Button>
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={handleAddItem}
                className="inline-flex items-center gap-2"
              >
              <Plus className="h-4 w-4" />
              Add Item
            </Button>
            </div>
          </div>

          {orderItems.length === 0 ? (
            <p className="text-gray-500 text-center py-4">No items added. Click "Add Item" to start.</p>
          ) : (
            <div className="space-y-3">
              {orderItems.map((item, index) => (
                <div key={index} className="flex gap-3 items-start p-3 bg-gray-50 rounded-lg">
                  <div className="flex-1 grid grid-cols-1 md:grid-cols-4 gap-3">
                    <div className="md:col-span-2">
                      <select
                        value={item.productId}
                        onChange={(e) => handleItemChange(index, 'productId', e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                      >
                        <option value="">Select Product</option>
                        {products.map((product) => (
                          <option key={product.id} value={product.id}>
                            {product.name} ({product.sku}) - {formatCurrency(product.price)}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <input
                        type="number"
                        min="1"
                        value={item.quantity}
                        onChange={(e) => handleItemChange(index, 'quantity', e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                        placeholder="Qty"
                      />
                    </div>
                    <div>
                      <input
                        type="number"
                        step="0.01"
                        min="0"
                        value={item.unitPrice}
                        onChange={(e) => handleItemChange(index, 'unitPrice', e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                        placeholder="Price"
                      />
                    </div>
                  </div>
                  <button
                    type="button"
                    onClick={() => handleRemoveItem(index)}
                    className="p-2 text-danger-600 hover:bg-danger-50 rounded-lg transition-colors"
                  >
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>
              ))}

              {/* Total */}
              <div className="flex justify-end pt-3 border-t">
                <div className="text-right">
                  <p className="text-sm text-gray-600">Total Amount</p>
                  <p className="text-2xl font-bold text-gray-900">{formatCurrency(calculateTotal())}</p>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Actions */}
        <div className="flex gap-3 justify-end">
          <Button
            type="button"
            variant="outline"
            onClick={() => navigate('/orders')}
            disabled={createMutation.isPending}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            variant="primary"
            loading={createMutation.isPending}
            disabled={createMutation.isPending || orderItems.length === 0}
          >
            Create Order
          </Button>
        </div>
      </form>

      {/* Barcode Scanner Modal */}
      <BarcodeScannerModal
        isOpen={scannerOpen}
        onClose={() => setScannerOpen(false)}
        onProductFound={handleProductScanned}
      />
    </div>
  );
};

export default CreateOrderPage;
