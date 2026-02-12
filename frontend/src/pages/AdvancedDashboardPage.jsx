import { useState, useEffect } from 'react';
import { analyticsAPI } from '../api/advanced.api';
import { Line, Bar, Doughnut } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import LoadingSpinner from '../components/common/LoadingSpinner';
import { ChevronDown, RotateCcw, TrendingUp, Package, ShoppingCart, DollarSign } from 'lucide-react';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

function AdvancedDashboard() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [salesTrend, setSalesTrend] = useState([]);
  const [topProducts, setTopProducts] = useState([]);
  const [orderBreakdown, setOrderBreakdown] = useState({});
  const [expandedSections, setExpandedSections] = useState({
    salesTrend: true,
    orderBreakdown: true,
    topProducts: true,
  });

  useEffect(() => {
    fetchAllData();
  }, []);

  const fetchAllData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const [analyticsRes, trendRes, topProductsRes, breakdownRes] = await Promise.all([
        analyticsAPI.getDashboardAnalytics().catch(e => ({ data: { data: {} } })),
        analyticsAPI.getSalesTrend(30).catch(e => ({ data: { data: [] } })),
        analyticsAPI.getTopProducts(5).catch(e => ({ data: { data: [] } })),
        analyticsAPI.getOrderStatusBreakdown().catch(e => ({ data: { data: {} } })),
      ]);

      const analyticsData = analyticsRes?.data?.data || {};
      const trendData = Array.isArray(trendRes?.data?.data) ? trendRes.data.data : [];
      const productsData = Array.isArray(topProductsRes?.data?.data) ? topProductsRes.data.data : [];
      const breakdownData = breakdownRes?.data?.data || {};

      setAnalytics(analyticsData);
      setSalesTrend(trendData);
      setTopProducts(productsData);
      setOrderBreakdown(breakdownData);
    } catch (err) {
      console.error('Failed to fetch analytics:', err);
      setError('Failed to load analytics data');
      setAnalytics({});
      setSalesTrend([]);
      setTopProducts([]);
      setOrderBreakdown({});
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  const toggleSection = (section) => {
    setExpandedSections(prev => ({
      ...prev,
      [section]: !prev[section]
    }));
  };

  // Sales Trend Chart Data
  const salesTrendData = {
    labels: salesTrend.map((d) => d.date || ''),
    datasets: [
      {
        label: 'Daily Sales ($)',
        data: salesTrend.map((d) => d.sales || 0),
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        tension: 0.4,
      },
    ],
  };

  // Top Products Chart Data
  const topProductsData = {
    labels: topProducts.map((p) => p.productName || 'Unknown'),
    datasets: [
      {
        label: 'Units Sold',
        data: topProducts.map((p) => p.totalSold || 0),
        backgroundColor: [
          'rgba(59, 130, 246, 0.8)',
          'rgba(16, 185, 129, 0.8)',
          'rgba(245, 158, 11, 0.8)',
          'rgba(239, 68, 68, 0.8)',
          'rgba(139, 92, 246, 0.8)',
        ],
      },
    ],
  };

  // Order Status Breakdown
  const orderBreakdownData = {
    labels: Object.keys(orderBreakdown),
    datasets: [
      {
        data: Object.values(orderBreakdown),
        backgroundColor: [
          '#3B82F6',
          '#10B981',
          '#F59E0B',
          '#EF4444',
          '#8B5CF6',
        ],
      },
    ],
  };

  return (
    <div className="space-y-6">
      {/* Header with Title and Refresh Button */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Advanced Analytics</h1>
          <p className="text-gray-600 mt-1">Real-time business insights and KPIs</p>
        </div>
        <button
          onClick={fetchAllData}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <RotateCcw className="h-4 w-4" />
          Refresh
        </button>
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700 flex items-center gap-2">
          <span className="text-lg">⚠️</span>
          {error}
        </div>
      )}

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Revenue"
          value={`$${(analytics?.totalRevenue || 0).toFixed(2)}`}
          subtitle={`Monthly: $${(analytics?.monthlyRevenue || 0).toFixed(2)}`}
          icon={DollarSign}
          color="blue"
        />
        <StatCard
          title="Total Products"
          value={analytics?.totalProducts || 0}
          subtitle={`${analytics?.lowStockProducts || 0} Low Stock`}
          icon={Package}
          color="green"
        />
        <StatCard
          title="Total Orders"
          value={analytics?.totalOrders || 0}
          subtitle="All Time"
          icon={ShoppingCart}
          color="purple"
        />
        <StatCard
          title="Inventory Value"
          value={`$${(analytics?.inventoryValue || 0).toFixed(2)}`}
          subtitle="Current Stock"
          icon={TrendingUp}
          color="yellow"
        />
      </div>

      {/* Charts Row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Sales Trend */}
        <CollapsibleChartCard
          title="Sales Trend"
          subtitle="Last 30 days"
          isExpanded={expandedSections.salesTrend}
          onToggle={() => toggleSection('salesTrend')}
          icon={<TrendingUp className="h-5 w-5" />}
        >
          {salesTrend.length > 0 ? (
            <div className="relative h-64">
              <Line data={salesTrendData} options={{ responsive: true, maintainAspectRatio: false }} />
            </div>
          ) : (
            <div className="h-64 flex items-center justify-center text-gray-500">No sales data available</div>
          )}
        </CollapsibleChartCard>

        {/* Order Status Breakdown */}
        <CollapsibleChartCard
          title="Order Status"
          subtitle="Distribution"
          isExpanded={expandedSections.orderBreakdown}
          onToggle={() => toggleSection('orderBreakdown')}
          icon={<ShoppingCart className="h-5 w-5" />}
        >
          {Object.keys(orderBreakdown).length > 0 ? (
            <div className="relative h-64">
              <Doughnut data={orderBreakdownData} options={{ responsive: true, maintainAspectRatio: false }} />
            </div>
          ) : (
            <div className="h-64 flex items-center justify-center text-gray-500">No order data available</div>
          )}
        </CollapsibleChartCard>
      </div>

      {/* Charts Row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Top Products */}
        <CollapsibleChartCard
          title="Top 5 Products"
          subtitle="By sales volume"
          isExpanded={expandedSections.topProducts}
          onToggle={() => toggleSection('topProducts')}
          icon={<Package className="h-5 w-5" />}
        >
          {topProducts.length > 0 ? (
            <div className="relative h-64">
              <Bar 
                data={topProductsData} 
                options={{ 
                  responsive: true, 
                  maintainAspectRatio: false,
                  indexAxis: 'y',
                }} 
              />
            </div>
          ) : (
            <div className="h-64 flex items-center justify-center text-gray-500">No product data available</div>
          )}
        </CollapsibleChartCard>

        {/* Top Products Table */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 overflow-hidden hover:shadow-md transition-shadow">
          <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
            <Package className="h-5 w-5 text-blue-600" />
            Top Products Detail
          </h3>
          {topProducts.length > 0 ? (
            <div className="overflow-x-auto max-h-96 overflow-y-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50 sticky top-0">
                  <tr>
                    <th className="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Product</th>
                    <th className="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Sold</th>
                    <th className="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Revenue</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {topProducts.map((product, index) => (
                    <tr key={product.productId || index} className="hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-3 text-sm font-medium text-gray-900">{product.productName || 'N/A'}</td>
                      <td className="px-4 py-3 text-sm text-gray-600">{product.totalSold || 0}</td>
                      <td className="px-4 py-3 text-sm font-semibold text-green-600">${(product.revenue || 0).toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="h-64 flex items-center justify-center text-gray-500">No product data available</div>
          )}
        </div>
      </div>
    </div>
  );
}

function CollapsibleChartCard({ title, subtitle, children, isExpanded, onToggle, icon }) {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition-shadow">
      <button
        onClick={onToggle}
        className="w-full px-6 py-4 flex items-center justify-between bg-gradient-to-r from-gray-50 to-gray-100 hover:from-blue-50 hover:to-blue-100 transition-colors"
      >
        <div className="flex items-center gap-3">
          <div className="text-blue-600">{icon}</div>
          <div className="text-left">
            <h3 className="font-semibold text-gray-900">{title}</h3>
            <p className="text-xs text-gray-600">{subtitle}</p>
          </div>
        </div>
        <ChevronDown 
          className={`h-5 w-5 text-gray-600 transition-transform ${isExpanded ? 'rotate-180' : ''}`}
        />
      </button>
      {isExpanded && (
        <div className="p-6 border-t border-gray-100">
          {children}
        </div>
      )}
    </div>
  );
}

function StatCard({ title, value, subtitle, icon: Icon, color }) {
  const colorClasses = {
    blue: 'bg-gradient-to-br from-blue-50 to-blue-100 text-blue-600 border-blue-200',
    green: 'bg-gradient-to-br from-green-50 to-green-100 text-green-600 border-green-200',
    purple: 'bg-gradient-to-br from-purple-50 to-purple-100 text-purple-600 border-purple-200',
    yellow: 'bg-gradient-to-br from-amber-50 to-amber-100 text-amber-600 border-amber-200',
  };

  const iconBgClasses = {
    blue: 'bg-blue-200',
    green: 'bg-green-200',
    purple: 'bg-purple-200',
    yellow: 'bg-amber-200',
  };

  return (
    <div className={`rounded-xl border shadow-sm p-6 transition-transform hover:shadow-md hover:scale-105 ${colorClasses[color]}`}>
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium opacity-75">{title}</p>
          <p className="text-3xl font-bold mt-2">{value}</p>
          <p className="text-xs opacity-60 mt-2">{subtitle}</p>
        </div>
        <div className={`${iconBgClasses[color]} rounded-lg p-3`}>
          <Icon className="h-8 w-8" />
        </div>
      </div>
    </div>
  );
}

export default AdvancedDashboard;
