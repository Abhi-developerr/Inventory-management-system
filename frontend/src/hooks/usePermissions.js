import { useAuth } from '../store/AuthContext';
import { ROLES } from '../utils/constants';

/**
 * Custom hook for role-based permissions
 */
export const usePermissions = () => {
  const { user, hasRole, isAdmin, isSuperAdmin } = useAuth();

  return {
    // User info
    currentUser: user,
    currentRole: user?.role,

    // Role checks
    canAccessAdmin: isAdmin(),
    canAccessSuperAdmin: isSuperAdmin(),
    
    // Category permissions
    canCreateCategory: hasRole([ROLES.ADMIN, ROLES.SUPER_ADMIN]),
    canEditCategory: hasRole([ROLES.ADMIN, ROLES.SUPER_ADMIN]),
    canDeleteCategory: hasRole([ROLES.ADMIN, ROLES.SUPER_ADMIN]),
    canViewCategories: true, // All authenticated users
    
    // Product permissions
    canCreateProduct: hasRole([ROLES.ADMIN, ROLES.SUPER_ADMIN]),
    canEditProduct: hasRole([ROLES.ADMIN, ROLES.SUPER_ADMIN]),
    canDeleteProduct: hasRole([ROLES.ADMIN, ROLES.SUPER_ADMIN]),
    canViewProducts: true, // All authenticated users
    canUpdateStock: hasRole([ROLES.ADMIN, ROLES.SUPER_ADMIN]),
    
    // Order permissions
    canCreateOrder: true, // All authenticated users
    canViewOrders: true, // All authenticated users
    canUpdateOrderStatus: hasRole([ROLES.ADMIN, ROLES.SUPER_ADMIN]),
    canCancelOrder: hasRole([ROLES.ADMIN, ROLES.SUPER_ADMIN]),
    
    // Dashboard permissions
    canViewDashboard: true, // All authenticated users
    canViewLowStock: true, // All authenticated users
    
    // Helper function to check multiple roles
    hasAnyRole: (roles) => hasRole(roles),
  };
};
