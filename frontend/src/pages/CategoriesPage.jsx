import { useState } from 'react';
import { Plus } from 'lucide-react';
import Button from '../components/common/Button';
import ErrorMessage from '../components/common/ErrorMessage';
import { usePermissions } from '../hooks/usePermissions';
import { useCategories } from '../features/categories/hooks/useCategories';
import CategoryList from '../features/categories/components/CategoryList';
import CategoryForm from '../features/categories/components/CategoryForm';

const CategoriesPage = () => {
  const { canCreateCategory } = usePermissions();
  const [currentPage, setCurrentPage] = useState(0);
  const [isFormOpen, setIsFormOpen] = useState(false);

  const { data, isLoading, error } = useCategories(currentPage, 20, 'name,asc');

  const categories = data?.content || [];
  const totalPages = data?.totalPages || 0;

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  const handleCreateClick = () => {
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
  };

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Categories</h1>
          <p className="text-gray-600 mt-1">Manage product categories</p>
        </div>
        {canCreateCategory && (
          <Button
            variant="primary"
            onClick={handleCreateClick}
            className="inline-flex items-center gap-2"
          >
            <Plus className="h-4 w-4" />
            Create Category
          </Button>
        )}
      </div>

      {/* Error State */}
      {error && (
        <ErrorMessage
          message="Failed to load categories. Please try again."
          className="mb-6"
        />
      )}

      {/* Categories List */}
      <CategoryList
        categories={categories}
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={handlePageChange}
        isLoading={isLoading}
      />

      {/* Create Form Modal */}
      <CategoryForm isOpen={isFormOpen} onClose={handleCloseForm} />
    </div>
  );
};

export default CategoriesPage;
