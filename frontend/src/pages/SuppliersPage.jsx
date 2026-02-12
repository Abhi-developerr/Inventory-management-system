import { useState } from 'react';
import { Plus } from 'lucide-react';
import Button from '../components/common/Button';
import { useSuppliers } from '../features/suppliers/hooks/useSuppliers';
import SupplierList from '../features/suppliers/components/SupplierList';
import SupplierForm from '../features/suppliers/components/SupplierForm';

const SuppliersPage = () => {
  const [currentPage, setCurrentPage] = useState(0);
  const [isFormOpen, setIsFormOpen] = useState(false);

  const { data: suppliersData, isLoading } = useSuppliers(currentPage, 20);

  const suppliers = suppliersData?.content || [];
  const totalPages = suppliersData?.totalPages || 0;

  const handlePageChange = (page) => {
    setCurrentPage(page - 1);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Suppliers</h1>
          <p className="text-gray-600 mt-1">Manage vendor relationships and track supplier performance</p>
        </div>
        <Button
          onClick={() => setIsFormOpen(true)}
          icon={Plus}
          className="gap-2"
        >
          New Supplier
        </Button>
      </div>

      <SupplierList
        suppliers={suppliers}
        currentPage={currentPage + 1}
        totalPages={totalPages}
        onPageChange={handlePageChange}
        isLoading={isLoading}
      />

      <SupplierForm
        isOpen={isFormOpen}
        onClose={handleCloseForm}
        supplier={null}
      />
    </div>
  );
};

export default SuppliersPage;
