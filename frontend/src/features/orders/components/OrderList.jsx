import { Eye } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import Table from '../../../components/common/Table';
import Pagination from '../../../components/common/Pagination';
import { formatCurrency, formatDateTime } from '../../../utils/formatters';
import OrderStatusBadge from './OrderStatusBadge';

const OrderList = ({ orders, currentPage, totalPages, onPageChange, isLoading }) => {
  const navigate = useNavigate();

  const handleViewDetails = (orderId) => {
    navigate(`/orders/${orderId}`);
  };

  const columns = [
    {
      header: 'Order Number',
      accessor: 'orderNumber',
      render: (row) => (
        <div>
          <p className="font-medium text-gray-900">{row.orderNumber}</p>
          <p className="text-xs text-gray-500">{formatDateTime(row.orderDate)}</p>
        </div>
      ),
    },
    {
      header: 'Customer',
      accessor: 'customerName',
      render: (row) => (
        <div>
          <p className="text-sm text-gray-900">{row.customerName}</p>
          {row.customerEmail && (
            <p className="text-xs text-gray-500">{row.customerEmail}</p>
          )}
        </div>
      ),
    },
    {
      header: 'Status',
      accessor: 'status',
      render: (row) => <OrderStatusBadge status={row.status} />,
    },
    {
      header: 'Total Amount',
      accessor: 'totalAmount',
      render: (row) => (
        <span className="font-medium text-gray-900">{formatCurrency(row.totalAmount)}</span>
      ),
    },
    {
      header: 'Items',
      accessor: 'items',
      render: (row) => (
        <span className="text-sm text-gray-600">{row.items?.length || 0} items</span>
      ),
    },
    {
      header: 'Actions',
      accessor: 'id',
      render: (row) => (
        <button
          onClick={() => handleViewDetails(row.id)}
          className="p-2 text-primary-600 hover:bg-primary-50 rounded-lg transition-colors"
          title="View Details"
        >
          <Eye className="h-4 w-4" />
        </button>
      ),
    },
  ];

  return (
    <>
      <Table
        columns={columns}
        data={orders}
        loading={isLoading}
        emptyMessage="No orders found. Create your first order!"
      />

      {totalPages > 1 && (
        <div className="mt-6">
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={onPageChange}
          />
        </div>
      )}
    </>
  );
};

export default OrderList;
