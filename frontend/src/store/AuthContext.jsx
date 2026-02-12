import { createContext, useContext, useState, useEffect } from 'react';
import { getAuthToken, setAuthToken, getUserData, setUserData, clearAuthData } from '../utils/storage';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Initialize auth state from localStorage
  useEffect(() => {
    const storedToken = getAuthToken();
    const storedUser = getUserData();

    if (storedToken && storedUser) {
      setToken(storedToken);
      setUser(storedUser);
    }
    
    setLoading(false);
  }, []);

  /**
   * Login user
   * @param {Object} authData - { accessToken, userId, username, email, role, organizationId, organizationName }
   */
  const login = (authData) => {
    const { accessToken, ...userData } = authData;
    
    // Store token and user data
    setAuthToken(accessToken);
    setUserData(userData);
    
    // Update state
    setToken(accessToken);
    setUser(userData);
  };

  /**
   * Logout user
   */
  const logout = () => {
    clearAuthData();
    setToken(null);
    setUser(null);
  };

  /**
   * Check if user is authenticated
   */
  const isAuthenticated = () => {
    return !!token && !!user;
  };

  /**
   * Check if user has specific role
   * @param {string|string[]} roles - Role or array of roles to check
   */
  const hasRole = (roles) => {
    if (!user) return false;
    
    if (Array.isArray(roles)) {
      return roles.includes(user.role);
    }
    
    return user.role === roles;
  };

  /**
   * Check if user is admin or super admin
   */
  const isAdmin = () => {
    return hasRole(['ADMIN', 'SUPER_ADMIN']);
  };

  /**
   * Check if user is super admin
   */
  const isSuperAdmin = () => {
    return hasRole('SUPER_ADMIN');
  };

  const value = {
    user,
    token,
    loading,
    login,
    logout,
    isAuthenticated,
    hasRole,
    isAdmin,
    isSuperAdmin,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
