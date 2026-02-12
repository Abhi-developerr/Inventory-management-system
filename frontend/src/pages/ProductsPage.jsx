import { useState, useEffect } from 'react';
import { Plus, Search } from 'lucide-react';
import Button from '../components/common/Button';
import SearchBar from '../components/common/SearchBar';
import ErrorMessage from '../components/common/ErrorMessage';
import ExportActions from '../components/common/ExportActions';
import { usePermissions } from '../hooks/usePermissions';
import { useProducts } from '../features/products/hooks/useProducts';
import { useSearchProducts } from '../features/products/hooks/useSearchProducts';
import ProductList from '../features/products/components/ProductList';
import ProductForm from '../features/products/components/ProductForm';

const ProductsPage = () => {
  const { canCreateProduct } = usePermissions();
  const [currentPage, setCurrentPage] = useState(0);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');

  // Debounce search query
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(searchQuery);
      setCurrentPage(0); // Reset to first page on search
    }, 500);

    return () => clearTimeout(timer);
  }, [searchQuery]);

  // Use search or normal fetch based on search query
  const isSearching = debouncedSearch.length > 0;
  const productsQuery = useProducts(currentPage, 20, 'name,asc');
  const searchProductsQuery = useSearchProducts(debouncedSearch, currentPage, 20, 'name,asc', isSearching);

  const activeQuery = isSearching ? searchProductsQuery : productsQuery;
  const { data, isLoading, error } = activeQuery;

  const products = data?.content || [];
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
          <h1 className="text-3xl font-bold text-gray-900">Products</h1>
          <p className="text-gray-600 mt-1">Manage your inventory</p>
        </div>
        <div className="flex items-center gap-3">
          <ExportActions type="products" />
          {canCreateProduct && (
            <Button
              variant="primary"
              onClick={handleCreateClick}
              className="inline-flex items-center gap-2"
            >
              <Plus className="h-4 w-4" />
              Create Product
            </Button>
          )}
        </div>
      </div>

      {/* Search Bar */}
      <div className="mb-6">
        <SearchBar
          value={searchQuery}
          onChange={setSearchQuery}
          placeholder="Search products by name, SKU, or description..."
          className="max-w-md"
        />
      </div>

      {/* Error State */}
      {error && (
        <ErrorMessage
          message="Failed to load products. Please try again."
          className="mb-6"
        />
      )}

      {/* Products List */}
      <ProductList
        products={products}
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={handlePageChange}
        isLoading={isLoading}
      />

      {/* Create Form Modal */}
      <ProductForm isOpen={isFormOpen} onClose={handleCloseForm} />
    </div>
  );
};

export default ProductsPage;
