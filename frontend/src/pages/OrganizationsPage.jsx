import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Edit, Trash2, Building2, Power, Search, Users, Package } from 'lucide-react';
import api from '../api/organization.api';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import Input from '../components/common/Input';
import { toast } from 'react-toastify';
import { useAuth } from '../store/AuthContext';

const OrganizationsPage = () => {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [showModal, setShowModal] = useState(false);
  const [editingOrg, setEditingOrg] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  
  const [formData, setFormData] = useState({
    name: '',
    companyCode: '',
    description: '',
    planType: 'BASIC',
    maxUsers: 10,
    maxProducts: 100,
  });

  // Check if user is SUPER_ADMIN
  const isSuperAdmin = user?.role === 'SUPER_ADMIN';

  // Fetch organizations
  const { data: organizationsData, isLoading, error } = useQuery({
    queryKey: ['organizations', currentPage],
    queryFn: async () => {
      try {
        const response = await api.get('/organizations', {
          params: { page: currentPage, size: 10 }
        });
        console.log('Organizations API response:', response);
        // axiosInstance returns ApiResponse, so response.data contains the actual data
        return response;
      } catch (err) {
        console.error('Error fetching organizations:', err);
        throw err;
      }
    },
    enabled: isSuperAdmin,
  });

  // Create/Update mutation
  const saveMutation = useMutation({
    mutationFn: async (data) => {
      if (editingOrg) {
        return api.put(`/organizations/${editingOrg.id}`, data);
      }
      return api.post('/organizations', data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['organizations']);
      toast.success(editingOrg ? 'Organization updated successfully' : 'Organization created successfully');
      handleCloseModal();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to save organization');
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: (id) => api.delete(`/organizations/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries(['organizations']);
      toast.success('Organization deleted successfully');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to delete organization');
    },
  });

  // Toggle status mutation
  const toggleStatusMutation = useMutation({
    mutationFn: (id) => api.patch(`/organizations/${id}/toggle-status`),
    onSuccess: () => {
      queryClient.invalidateQueries(['organizations']);
      toast.success('Organization status updated');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Failed to update status');
    },
  });

  const handleOpenModal = (org = null) => {
    if (org) {
      setEditingOrg(org);
      setFormData({
        name: org.name,
        companyCode: org.companyCode,
        description: org.description || '',
        planType: org.planType,
        maxUsers: org.maxUsers,
        maxProducts: org.maxProducts,
      });
    } else {
      setEditingOrg(null);
      setFormData({
        name: '',
        companyCode: '',
        description: '',
        planType: 'BASIC',
        maxUsers: 10,
        maxProducts: 100,
      });
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingOrg(null);
    setFormData({
      name: '',
      companyCode: '',
      description: '',
      planType: 'BASIC',
      maxUsers: 10,
      maxProducts: 100,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    saveMutation.mutate(formData);
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this organization? This will delete all associated data.')) {
      deleteMutation.mutate(id);
    }
  };

  const handleToggleStatus = (id) => {
    toggleStatusMutation.mutate(id);
  };

  if (!isSuperAdmin) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <Building2 className="h-16 w-16 text-gray-400 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-gray-900 mb-2">Access Denied</h2>
          <p className="text-gray-600">Only SUPER_ADMIN can manage organizations</p>
        </div>
      </div>
    );
  }

  // Show error state
  if (error) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <Building2 className="h-16 w-16 text-red-400 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-gray-900 mb-2">Error Loading Organizations</h2>
          <p className="text-gray-600">{error?.message || 'Failed to load organizations'}</p>
          <Button 
            onClick={() => window.location.reload()} 
            variant="primary" 
            className="mt-4"
          >
            Retry
          </Button>
        </div>
      </div>
    );
  }

  const organizations = organizationsData?.data?.data?.content || [];
  const totalPages = organizationsData?.data?.data?.totalPages || 0;

  const filteredOrganizations = organizations.filter(org =>
    org.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    org.companyCode.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getPlanBadgeColor = (plan) => {
    const colors = {
      BASIC: 'bg-gray-100 text-gray-700',
      PREMIUM: 'bg-primary-100 text-primary-700',
      ENTERPRISE: 'bg-purple-100 text-purple-700',
    };
    return colors[plan] || 'bg-gray-100 text-gray-700';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Organizations</h1>
          <p className="text-gray-600 mt-1">Manage all organizations in the system</p>
        </div>
        <Button onClick={() => handleOpenModal()} variant="primary" className="shadow-lg">
          <Plus className="h-5 w-5 mr-2" />
          Add Organization
        </Button>
      </div>

      {/* Search */}
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
        <Input
          type="text"
          placeholder="Search organizations..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10"
        />
      </div>

      {/* Organizations Grid */}
      {isLoading ? (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
          <p className="text-gray-600 mt-4">Loading organizations...</p>
        </div>
      ) : filteredOrganizations.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg border border-gray-200">
          <Building2 className="h-16 w-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-gray-900 mb-2">No organizations found</h3>
          <p className="text-gray-600 mb-6">Get started by creating a new organization</p>
          <Button onClick={() => handleOpenModal()} variant="primary">
            <Plus className="h-5 w-5 mr-2" />
            Add First Organization
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredOrganizations.map((org) => (
            <div
              key={org.id}
              className="bg-white rounded-xl shadow-md hover:shadow-xl transition-shadow border border-gray-200 overflow-hidden"
            >
              {/* Card Header */}
              <div className="bg-gradient-to-r from-primary-500 to-secondary-500 p-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
                      <Building2 className="h-6 w-6 text-white" />
                    </div>
                    <div>
                      <h3 className="text-lg font-bold text-white truncate">{org.name}</h3>
                      <p className="text-sm text-white/80">{org.companyCode}</p>
                    </div>
                  </div>
                  <button
                    onClick={() => handleToggleStatus(org.id)}
                    className={`p-2 rounded-lg transition-colors ${
                      org.isActive 
                        ? 'bg-green-500/20 text-green-100 hover:bg-green-500/30' 
                        : 'bg-red-500/20 text-red-100 hover:bg-red-500/30'
                    }`}
                    title={org.isActive ? 'Active - Click to deactivate' : 'Inactive - Click to activate'}
                  >
                    <Power className="h-5 w-5" />
                  </button>
                </div>
              </div>

              {/* Card Body */}
              <div className="p-4 space-y-4">
                <p className="text-sm text-gray-600 line-clamp-2 min-h-[40px]">
                  {org.description || 'No description provided'}
                </p>

                <div className="flex items-center justify-between">
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getPlanBadgeColor(org.planType)}`}>
                    {org.planType}
                  </span>
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                    org.isActive 
                      ? 'bg-green-100 text-green-700' 
                      : 'bg-red-100 text-red-700'
                  }`}>
                    {org.isActive ? 'Active' : 'Inactive'}
                  </span>
                </div>

                <div className="grid grid-cols-2 gap-4 pt-4 border-t border-gray-200">
                  <div className="flex items-center space-x-2">
                    <Users className="h-4 w-4 text-gray-400" />
                    <div>
                      <p className="text-xs text-gray-500">Max Users</p>
                      <p className="text-sm font-semibold text-gray-900">{org.maxUsers || 'Unlimited'}</p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Package className="h-4 w-4 text-gray-400" />
                    <div>
                      <p className="text-xs text-gray-500">Max Products</p>
                      <p className="text-sm font-semibold text-gray-900">{org.maxProducts || 'Unlimited'}</p>
                    </div>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex gap-2 pt-4 border-t border-gray-200">
                  <Button
                    onClick={() => handleOpenModal(org)}
                    variant="outline"
                    className="flex-1"
                  >
                    <Edit className="h-4 w-4 mr-2" />
                    Edit
                  </Button>
                  <Button
                    onClick={() => handleDelete(org.id)}
                    variant="danger"
                    className="flex-1"
                  >
                    <Trash2 className="h-4 w-4 mr-2" />
                    Delete
                  </Button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-8">
          <Button
            onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
            disabled={currentPage === 0}
            variant="outline"
          >
            Previous
          </Button>
          <span className="px-4 py-2 text-sm text-gray-700">
            Page {currentPage + 1} of {totalPages}
          </span>
          <Button
            onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
            disabled={currentPage === totalPages - 1}
            variant="outline"
          >
            Next
          </Button>
        </div>
      )}

      {/* Create/Edit Modal */}
      <Modal
        isOpen={showModal}
        onClose={handleCloseModal}
        title={editingOrg ? 'Edit Organization' : 'Create Organization'}
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Organization Name *
            </label>
            <Input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="e.g., TechStore Inc."
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Company Code *
            </label>
            <Input
              type="text"
              value={formData.companyCode}
              onChange={(e) => setFormData({ ...formData, companyCode: e.target.value.toUpperCase() })}
              placeholder="e.g., TECH001"
              required
              disabled={editingOrg}
            />
            {editingOrg && (
              <p className="text-xs text-gray-500 mt-1">Company code cannot be changed</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Description
            </label>
            <textarea
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="Brief description of the organization"
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Plan Type *
            </label>
            <select
              value={formData.planType}
              onChange={(e) => setFormData({ ...formData, planType: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              required
            >
              <option value="BASIC">Basic</option>
              <option value="PREMIUM">Premium</option>
              <option value="ENTERPRISE">Enterprise</option>
            </select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Max Users
              </label>
              <Input
                type="number"
                value={formData.maxUsers}
                onChange={(e) => setFormData({ ...formData, maxUsers: parseInt(e.target.value) || 0 })}
                min="1"
                placeholder="10"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Max Products
              </label>
              <Input
                type="number"
                value={formData.maxProducts}
                onChange={(e) => setFormData({ ...formData, maxProducts: parseInt(e.target.value) || 0 })}
                min="1"
                placeholder="100"
              />
            </div>
          </div>

          <div className="flex gap-3 pt-4">
            <Button
              type="button"
              onClick={handleCloseModal}
              variant="outline"
              className="flex-1"
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              className="flex-1"
              disabled={saveMutation.isPending}
            >
              {saveMutation.isPending ? 'Saving...' : editingOrg ? 'Update' : 'Create'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default OrganizationsPage;
