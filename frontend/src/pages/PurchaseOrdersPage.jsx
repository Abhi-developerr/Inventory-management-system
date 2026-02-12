import { useState } from 'react';
import { Plus } from 'lucide-react';
import Button from '../components/common/Button';
import { usePurchaseOrders } from '../features/purchaseOrders/hooks/usePurchaseOrderOperations';
import PurchaseOrderList from '../features/purchaseOrders/components/PurchaseOrderList';

const PurchaseOrdersPage = () => {
  const [currentPage, setCurrentPage] = useState(0);
  const [isFormOpen, setIsFormOpen] = useState(false);

  const { data: posData, isLoading } = usePurchaseOrders(currentPage, 20);

  const purchaseOrders = posData?.content || [];
  const totalPages = posData?.totalPages || 0;

  const handlePageChange = (page) => {
    setCurrentPage(page - 1);
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Purchase Orders</h1>
          <p className="text-gray-600 mt-1">Manage restock orders from suppliers</p>
        </div>
        <Button
          onClick={() => setIsFormOpen(true)}
          icon={Plus}
          className="gap-2"
        >
          New PO
        </Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="bg-white p-4 rounded-lg shadow-soft">
          <p className="text-sm text-gray-600">Total POs</p>
          <p className="text-2xl font-bold text-gray-900">{posData?.totalElements || 0}</p>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-soft">
          <p className="text-sm text-gray-600">Pending Approval</p>
          <p className="text-2xl font-bold text-blue-600">0</p>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-soft">
          <p className="text-sm text-gray-600">Approved</p>
          <p className="text-2xl font-bold text-green-600">0</p>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-soft">
          <p className="text-sm text-gray-600">Received</p>
          <p className="text-2xl font-bold text-success-600">0</p>
        </div>
      </div>

      <PurchaseOrderList
        purchaseOrders={purchaseOrders}
        currentPage={currentPage + 1}
        totalPages={totalPages}
        onPageChange={handlePageChange}
        isLoading={isLoading}
      />
    </div>
  );
};

export default PurchaseOrdersPage;
