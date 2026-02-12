import { useParams, useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, Calendar, User, Mail, Phone, MapPin, Package } from 'lucide-react';
import { useOrderById } from '../features/orders/hooks/useOrderById';
import { useUpdateOrderStatus } from '../features/orders/hooks/useUpdateOrderStatus';
import { usePermissions } from '../hooks/usePermissions';
import Button from '../components/common/Button';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import OrderStatusBadge from '../features/orders/components/OrderStatusBadge';
import { formatCurrency, formatDateTime } from '../utils/formatters';
import { ORDER_STATUS } from '../utils/constants';
import { toast } from 'react-toastify';

const OrderDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { canUpdateOrderStatus } = usePermissions();
  
  const { data: order, isLoading, error } = useOrderById(id);
  const updateStatusMutation = useUpdateOrderStatus();

  const handleStatusUpdate = async (newStatus) => {
    try {
      await updateStatusMutation.mutateAsync({ orderId: id, status: newStatus });
      toast.success(`Order status updated to ${newStatus}`);
    } catch (error) {
      // Error handled by mutation hook
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (error) {
    return (
      <div>
        <ErrorMessage message="Failed to load order details" />
        <Button onClick={() => navigate('/orders')} className="mt-4">
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Orders
        </Button>
      </div>
    );
  }

  if (!order) {
    return (
      <div>
        <ErrorMessage message="Order not found" />
        <Button onClick={() => navigate('/orders')} className="mt-4">
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Orders
        </Button>
      </div>
    );
  }

  const nextStatuses = getNextStatuses(order.status);

  return (
    <div>
      {/* Header */}
      <div className="mb-6 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="outline" onClick={() => navigate('/orders')}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back
          </Button>
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{order.orderNumber}</h1>
            <p className="text-gray-600 mt-1 flex items-center gap-2">
              <Calendar className="h-4 w-4" />
              {formatDateTime(order.orderDate)}
            </p>
          </div>
        </div>
        <OrderStatusBadge status={order.status} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-6">
          {/* Order Items */}
          <div className="bg-white rounded-lg shadow-soft p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
              <Package className="h-5 w-5" />
              Order Items
            </h2>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Product</th>
                    <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Quantity</th>
                    <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Unit Price</th>
                    <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Total</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {order.orderItems?.map((item) => (
                    <tr key={item.id}>
                      <td className="px-4 py-4">
                        <div>
                          <Link 
                            to={`/products`} 
                            className="font-medium text-primary-600 hover:text-primary-700"
                          >
                            {item.productName}
                          </Link>
                          <p className="text-sm text-gray-500">SKU: {item.productSku}</p>
                        </div>
                      </td>
                      <td className="px-4 py-4 text-right">{item.quantity}</td>
                      <td className="px-4 py-4 text-right">{formatCurrency(item.unitPrice)}</td>
                      <td className="px-4 py-4 text-right font-medium">
                        {formatCurrency(item.quantity * item.unitPrice)}
                      </td>
                    </tr>
                  ))}
                </tbody>
                <tfoot className="bg-gray-50">
                  <tr>
                    <td colSpan="3" className="px-4 py-4 text-right font-semibold text-gray-900">
                      Total Amount
                    </td>
                    <td className="px-4 py-4 text-right font-bold text-xl text-primary-600">
                      {formatCurrency(order.totalAmount)}
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>

          {/* Status Update Actions */}
          {canUpdateOrderStatus && nextStatuses.length > 0 && (
            <div className="bg-white rounded-lg shadow-soft p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">Update Order Status</h2>
              <div className="flex flex-wrap gap-3">
                {nextStatuses.map((status) => (
                  <Button
                    key={status}
                    variant={status === ORDER_STATUS.CANCELLED ? 'danger' : 'primary'}
                    onClick={() => handleStatusUpdate(status)}
                    loading={updateStatusMutation.isPending}
                  >
                    Mark as {status}
                  </Button>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Customer Information */}
          <div className="bg-white rounded-lg shadow-soft p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Customer Information</h2>
            <div className="space-y-3">
              <div className="flex items-start gap-3">
                <User className="h-5 w-5 text-gray-400 mt-0.5" />
                <div>
                  <p className="text-sm text-gray-500">Name</p>
                  <p className="font-medium text-gray-900">{order.customerName}</p>
                </div>
              </div>
              {order.customerEmail && (
                <div className="flex items-start gap-3">
                  <Mail className="h-5 w-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-500">Email</p>
                    <a 
                      href={`mailto:${order.customerEmail}`} 
                      className="font-medium text-primary-600 hover:text-primary-700"
                    >
                      {order.customerEmail}
                    </a>
                  </div>
                </div>
              )}
              {order.customerPhone && (
                <div className="flex items-start gap-3">
                  <Phone className="h-5 w-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-500">Phone</p>
                    <a 
                      href={`tel:${order.customerPhone}`} 
                      className="font-medium text-primary-600 hover:text-primary-700"
                    >
                      {order.customerPhone}
                    </a>
                  </div>
                </div>
              )}
              {order.shippingAddress && (
                <div className="flex items-start gap-3">
                  <MapPin className="h-5 w-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-500">Shipping Address</p>
                    <p className="font-medium text-gray-900 whitespace-pre-line">
                      {order.shippingAddress}
                    </p>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Additional Notes */}
          {order.notes && (
            <div className="bg-white rounded-lg shadow-soft p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">Order Notes</h2>
              <p className="text-gray-600 whitespace-pre-line">{order.notes}</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

// Helper function to determine next possible statuses
function getNextStatuses(currentStatus) {
  const statusFlow = {
    [ORDER_STATUS.CREATED]: [ORDER_STATUS.CONFIRMED, ORDER_STATUS.CANCELLED],
    [ORDER_STATUS.CONFIRMED]: [ORDER_STATUS.SHIPPED, ORDER_STATUS.CANCELLED],
    [ORDER_STATUS.SHIPPED]: [ORDER_STATUS.DELIVERED],
    [ORDER_STATUS.DELIVERED]: [],
    [ORDER_STATUS.CANCELLED]: [],
  };
  
  return statusFlow[currentStatus] || [];
}

export default OrderDetailsPage;
