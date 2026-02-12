import { Package, ShoppingCart, AlertTriangle, Clock } from 'lucide-react';
import StatsCard from '../features/dashboard/components/StatsCard';
import LowStockWidget from '../features/dashboard/components/LowStockWidget';
import { useProducts } from '../features/products/hooks/useProducts';
import { useOrders } from '../features/orders/hooks/useOrders';
import { useLowStockProducts } from '../features/products/hooks/useLowStockProducts';
import { ORDER_STATUS } from '../utils/constants';

const DashboardPage = () => {
  // Fetch data for dashboard stats
  const { data: productsData } = useProducts(0, 1, 'name,asc'); // Just need totalElements
  const { data: ordersData } = useOrders(0, 100, 'orderDate,desc'); // Fetch more for filtering
  const { data: lowStockData, isLoading: lowStockLoading } = useLowStockProducts();

  const totalProducts = productsData?.totalElements || 0;
  const totalOrders = ordersData?.totalElements || 0;
  const lowStockCount = lowStockData?.length || 0;
  
  // Calculate pending orders (CREATED + CONFIRMED)
  const allOrders = ordersData?.content || [];
  const pendingOrders = allOrders.filter(
    order => order.status === ORDER_STATUS.CREATED || order.status === ORDER_STATUS.CONFIRMED
  ).length;

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600 mt-1">Overview of your inventory and orders</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <StatsCard
          title="Total Products"
          value={totalProducts}
          icon={Package}
          colorClass="text-primary-600 bg-primary-600"
        />
        <StatsCard
          title="Total Orders"
          value={totalOrders}
          icon={ShoppingCart}
          colorClass="text-success-600 bg-success-600"
        />
        <StatsCard
          title="Low Stock Items"
          value={lowStockCount}
          icon={AlertTriangle}
          colorClass="text-warning-600 bg-warning-600"
        />
        <StatsCard
          title="Pending Orders"
          value={pendingOrders}
          icon={Clock}
          colorClass="text-danger-600 bg-danger-600"
        />
      </div>

      {/* Low Stock Widget */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          {/* Placeholder for charts or recent orders */}
          <div className="bg-white rounded-lg shadow-soft p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h3>
            <div className="space-y-3">
              {allOrders.slice(0, 5).map((order) => (
                <div key={order.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">{order.orderNumber}</p>
                    <p className="text-sm text-gray-600">{order.customerName}</p>
                  </div>
                  <div className="text-right">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      order.status === 'DELIVERED' 
                        ? 'bg-success-100 text-success-800'
                        : order.status === 'CANCELLED'
                        ? 'bg-danger-100 text-danger-800'
                        : 'bg-blue-100 text-blue-800'
                    }`}>
                      {order.status}
                    </span>
                  </div>
                </div>
              ))}
              {allOrders.length === 0 && (
                <p className="text-gray-500 text-center py-8">No recent orders</p>
              )}
            </div>
          </div>
        </div>
        
        <div>
          <LowStockWidget products={lowStockData || []} isLoading={lowStockLoading} />
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
