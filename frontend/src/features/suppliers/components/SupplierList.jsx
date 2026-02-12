import { useState } from 'react';
import { Edit, Trash2, Link as LinkIcon, Unlink } from 'lucide-react';
import Table from '../../../components/common/Table';
import Pagination from '../../../components/common/Pagination';
import ConfirmDialog from '../../../components/common/ConfirmDialog';
import { usePermissions } from '../../../hooks/usePermissions';
import { useDeleteSupplier } from '../hooks/useSupplierOperations';
import { formatCurrency, formatDateTime } from '../../../utils/formatters';
import SupplierForm from './SupplierForm';

const SupplierList = ({ suppliers, currentPage, totalPages, onPageChange, isLoading }) => {
  const { canEditProduct, canDeleteProduct } = usePermissions();
  const deleteMutation = useDeleteSupplier();

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [selectedSupplier, setSelectedSupplier] = useState(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [supplierToDelete, setSupplierToDelete] = useState(null);

  const handleEdit = (supplier) => {
    setSelectedSupplier(supplier);
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setSelectedSupplier(null);
  };

  const handleDeleteClick = (supplier) => {
    setSupplierToDelete(supplier);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (supplierToDelete) {
      await deleteMutation.mutateAsync(supplierToDelete.id);
      setDeleteDialogOpen(false);
      setSupplierToDelete(null);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setSupplierToDelete(null);
  };

  const columns = [
    {
      header: 'Supplier',
      accessor: 'name',
      render: (row) => (
        <div>
          <p className="font-medium text-gray-900">{row.name}</p>
          <p className="text-xs text-gray-500">Code: {row.code}</p>
        </div>
      ),
    },
    {
      header: 'Contact',
      accessor: 'contactPerson',
      render: (row) => (
        <div>
          <p className="text-sm text-gray-900">{row.contactPerson || 'N/A'}</p>
          <p className="text-xs text-gray-500">{row.contactEmail || ''}</p>
        </div>
      ),
    },
    {
      header: 'Lead Time',
      accessor: 'leadTimeDays',
      render: (row) => <span className="text-sm text-gray-600">{row.leadTimeDays} days</span>,
    },
    {
      header: 'MOQ',
      accessor: 'minimumOrderQuantity',
      render: (row) => <span className="text-sm font-medium text-gray-900">{row.minimumOrderQuantity}</span>,
    },
    {
      header: 'Rating',
      accessor: 'rating',
      render: (row) => (
        <div>
          {row.rating ? (
            <div className="flex items-center gap-1">
              <span className="text-sm font-medium text-gray-900">{row.rating.toFixed(1)}</span>
              <span className="text-yellow-400">★</span>
            </div>
          ) : (
            <span className="text-sm text-gray-500">No rating</span>
          )}
        </div>
      ),
    },
    {
      header: 'Products',
      accessor: 'products',
      render: (row) => <span className="text-sm text-gray-600">{row.products?.length || 0} products</span>,
    },
    {
      header: 'Actions',
      accessor: 'id',
      render: (row) => (
        <div className="flex items-center gap-2">
          {canEditProduct && (
            <button
              onClick={() => handleEdit(row)}
              className="p-2 text-primary-600 hover:bg-primary-50 rounded-lg transition-colors"
              title="Edit"
            >
              <Edit className="h-4 w-4" />
            </button>
          )}
          {canDeleteProduct && (
            <button
              onClick={() => handleDeleteClick(row)}
              className="p-2 text-danger-600 hover:bg-danger-50 rounded-lg transition-colors"
              title="Delete"
            >
              <Trash2 className="h-4 w-4" />
            </button>
          )}
        </div>
      ),
    },
  ];

  return (
    <>
      <Table
        columns={columns}
        data={suppliers}
        loading={isLoading}
        emptyMessage="No suppliers found. Create your first supplier!"
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

      <SupplierForm
        isOpen={isFormOpen}
        onClose={handleCloseForm}
        supplier={selectedSupplier}
      />

      <ConfirmDialog
        isOpen={deleteDialogOpen}
        onClose={handleDeleteCancel}
        onConfirm={handleDeleteConfirm}
        title="Delete Supplier"
        message={`Are you sure you want to delete "${supplierToDelete?.name}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        variant="danger"
        loading={deleteMutation.isPending}
      />
    </>
  );
};

export default SupplierList;
