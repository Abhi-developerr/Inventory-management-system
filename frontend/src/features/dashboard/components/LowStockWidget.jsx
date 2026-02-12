import { AlertTriangle, Package } from 'lucide-react';
import { Link } from 'react-router-dom';
import { formatCurrency } from '../../../utils/formatters';
import Button from '../../../components/common/Button';

const LowStockWidget = ({ products, isLoading }) => {
  if (isLoading) {
    return (
      <div className="bg-white rounded-lg shadow-soft p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Low Stock Alerts</h3>
        <div className="flex items-center justify-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
        </div>
      </div>
    );
  }

  if (!products || products.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-soft p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Low Stock Alerts</h3>
        <div className="text-center py-8">
          <Package className="h-12 w-12 text-success-500 mx-auto mb-3" />
          <p className="text-gray-600">All products are well stocked!</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-soft p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900">Low Stock Alerts</h3>
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-warning-100 text-warning-800">
          {products.length} items
        </span>
      </div>

      <div className="space-y-3 max-h-96 overflow-y-auto scrollbar-thin">
        {products.map((product) => (
          <div
            key={product.id}
            className="flex items-center justify-between p-3 bg-warning-50 border border-warning-200 rounded-lg"
          >
            <div className="flex items-center gap-3 flex-1">
              <AlertTriangle className="h-5 w-5 text-warning-600 flex-shrink-0" />
              <div className="flex-1 min-w-0">
                <p className="font-medium text-gray-900 truncate">{product.name}</p>
                <p className="text-sm text-gray-600">
                  SKU: {product.sku} • {formatCurrency(product.price)}
                </p>
              </div>
            </div>
            <div className="text-right ml-3">
              <p className="text-sm font-semibold text-danger-600">
                {product.stockQuantity} left
              </p>
              <p className="text-xs text-gray-500">
                Threshold: {product.lowStockThreshold}
              </p>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-4 pt-4 border-t">
        <Link to="/products">
          <Button variant="outline" size="sm" className="w-full">
            View All Products
          </Button>
        </Link>
      </div>
    </div>
  );
};

export default LowStockWidget;
