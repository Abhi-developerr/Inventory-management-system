import { useState } from 'react';
import { Plus, Filter } from 'lucide-react';
import { Link } from 'react-router-dom';
import Button from '../components/common/Button';
import ErrorMessage from '../components/common/ErrorMessage';
import ExportActions from '../components/common/ExportActions';
import { useOrders } from '../features/orders/hooks/useOrders';
import OrderList from '../features/orders/components/OrderList';
import { ORDER_STATUS } from '../utils/constants';

const OrdersPage = () => {
  const [currentPage, setCurrentPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState('ALL');

  const { data, isLoading, error } = useOrders(currentPage, 20, 'orderDate,desc');

  const orders = data?.content || [];
  const totalPages = data?.totalPages || 0;

  // Filter orders by status client-side (backend filter would be better in production)
  const filteredOrders = statusFilter === 'ALL'
    ? orders
    : orders.filter(order => order.status === statusFilter);

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Orders</h1>
          <p className="text-gray-600 mt-1">Manage customer orders</p>
        </div>
        <div className="flex items-center gap-3">
          <ExportActions type="orders" />
          <Link to="/orders/create">
            <Button variant="primary" className="inline-flex items-center gap-2">
              <Plus className="h-4 w-4" />
              Create Order
            </Button>
          </Link>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="mb-6 flex items-center gap-2 flex-wrap">
        <button
          onClick={() => setStatusFilter('ALL')}
          className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
            statusFilter === 'ALL'
              ? 'bg-primary-600 text-white'
              : 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-300'
          }`}
        >
          All Orders
        </button>
        {Object.keys(ORDER_STATUS).map((status) => (
          <button
            key={status}
            onClick={() => setStatusFilter(status)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
              statusFilter === status
                ? 'bg-primary-600 text-white'
                : 'bg-white text-gray-700 hover:bg-gray-50 border border-gray-300'
            }`}
          >
            {status.charAt(0) + status.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      {/* Error State */}
      {error && (
        <ErrorMessage
          message="Failed to load orders. Please try again."
          className="mb-6"
        />
      )}

      {/* Orders List */}
      <OrderList
        orders={filteredOrders}
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={handlePageChange}
        isLoading={isLoading}
      />
    </div>
  );
};

export default OrdersPage;
