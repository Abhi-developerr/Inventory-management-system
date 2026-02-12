import { ORDER_STATUS_LABELS, ORDER_STATUS_COLORS } from '../../../utils/constants';

const OrderStatusBadge = ({ status }) => {
  const label = ORDER_STATUS_LABELS[status] || status;
  const colorClass = ORDER_STATUS_COLORS[status] || 'bg-gray-100 text-gray-800';

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${colorClass}`}>
      {label}
    </span>
  );
};

export default OrderStatusBadge;
