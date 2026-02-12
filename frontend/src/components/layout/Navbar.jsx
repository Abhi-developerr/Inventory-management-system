import { Bell, Settings, User, LogOut, Shield, HelpCircle, X } from 'lucide-react';
import { useAuth } from '../../store/AuthContext';
import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [showNotifications, setShowNotifications] = useState(false);
  const [showSettings, setShowSettings] = useState(false);
  const notificationRef = useRef(null);
  const settingsRef = useRef(null);

  // Sample notifications
  const notifications = [
    {
      id: 1,
      title: 'Low Stock Alert',
      message: 'Product "Laptop Dell XPS" is running low on stock',
      time: '5 min ago',
      unread: true,
    },
    {
      id: 2,
      title: 'New Order',
      message: 'Order #1234 has been placed',
      time: '1 hour ago',
      unread: true,
    },
    {
      id: 3,
      title: 'Order Completed',
      message: 'Order #1230 has been delivered',
      time: '3 hours ago',
      unread: false,
    },
  ];

  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (notificationRef.current && !notificationRef.current.contains(event.target)) {
        setShowNotifications(false);
      }
      if (settingsRef.current && !settingsRef.current.contains(event.target)) {
        setShowSettings(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-8">
      {/* Page title - can be dynamic based on route */}
      <div>
        <h2 className="text-xl font-semibold text-gray-800">Welcome back, {user?.username}!</h2>
        {user?.organizationName && (
          <p className="text-sm text-gray-500">{user.organizationName}</p>
        )}
      </div>

      {/* Right section */}
      <div className="flex items-center gap-4">
        {/* Notifications */}
        <div className="relative" ref={notificationRef}>
          <button 
            onClick={() => {
              setShowNotifications(!showNotifications);
              setShowSettings(false);
            }}
            className="p-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors relative"
          >
            <Bell className="h-5 w-5" />
            {notifications.filter(n => n.unread).length > 0 && (
              <span className="absolute top-1 right-1 h-2 w-2 bg-danger-500 rounded-full"></span>
            )}
          </button>

          {/* Notifications Dropdown */}
          {showNotifications && (
            <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-2xl border border-gray-200 z-50 animate-fade-in">
              <div className="p-4 border-b border-gray-200 flex items-center justify-between">
                <h3 className="font-semibold text-gray-900">Notifications</h3>
                <button 
                  onClick={() => setShowNotifications(false)}
                  className="p-1 hover:bg-gray-100 rounded transition-colors"
                >
                  <X className="h-4 w-4 text-gray-500" />
                </button>
              </div>
              <div className="max-h-96 overflow-y-auto">
                {notifications.length > 0 ? (
                  notifications.map((notification) => (
                    <div
                      key={notification.id}
                      className={`p-4 border-b border-gray-100 hover:bg-gray-50 transition-colors cursor-pointer ${
                        notification.unread ? 'bg-primary-50/30' : ''
                      }`}
                    >
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <p className="font-medium text-sm text-gray-900">{notification.title}</p>
                          <p className="text-sm text-gray-600 mt-1">{notification.message}</p>
                          <p className="text-xs text-gray-400 mt-2">{notification.time}</p>
                        </div>
                        {notification.unread && (
                          <div className="w-2 h-2 bg-primary-600 rounded-full ml-2 mt-1"></div>
                        )}
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="p-8 text-center text-gray-500">
                    <Bell className="h-12 w-12 mx-auto mb-3 text-gray-300" />
                    <p>No notifications</p>
                  </div>
                )}
              </div>
              <div className="p-3 border-t border-gray-200">
                <button className="w-full text-center text-sm text-primary-600 hover:text-primary-700 font-medium">
                  View all notifications
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Settings */}
        <div className="relative" ref={settingsRef}>
          <button 
            onClick={() => {
              setShowSettings(!showSettings);
              setShowNotifications(false);
            }}
            className="p-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <Settings className="h-5 w-5" />
          </button>

          {/* Settings Dropdown */}
          {showSettings && (
            <div className="absolute right-0 mt-2 w-64 bg-white rounded-lg shadow-2xl border border-gray-200 z-50 animate-fade-in">
              <div className="p-4 border-b border-gray-200">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 bg-gradient-to-br from-primary-500 to-secondary-500 rounded-full flex items-center justify-center">
                    <User className="h-5 w-5 text-white" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold text-sm text-gray-900 truncate">{user?.username}</p>
                    <p className="text-xs text-gray-500 truncate">{user?.email || user?.organizationName}</p>
                  </div>
                </div>
              </div>
              
              <div className="py-2">
                <button className="w-full px-4 py-2.5 text-left hover:bg-gray-50 transition-colors flex items-center space-x-3 text-gray-700">
                  <User className="h-4 w-4" />
                  <span className="text-sm">My Profile</span>
                </button>
                <button className="w-full px-4 py-2.5 text-left hover:bg-gray-50 transition-colors flex items-center space-x-3 text-gray-700">
                  <Settings className="h-4 w-4" />
                  <span className="text-sm">Account Settings</span>
                </button>
                <button className="w-full px-4 py-2.5 text-left hover:bg-gray-50 transition-colors flex items-center space-x-3 text-gray-700">
                  <Shield className="h-4 w-4" />
                  <span className="text-sm">
                    Role: <span className="font-semibold text-primary-600">{user?.role}</span>
                  </span>
                </button>
                <button className="w-full px-4 py-2.5 text-left hover:bg-gray-50 transition-colors flex items-center space-x-3 text-gray-700">
                  <HelpCircle className="h-4 w-4" />
                  <span className="text-sm">Help & Support</span>
                </button>
              </div>
              
              <div className="border-t border-gray-200 py-2">
                <button 
                  onClick={handleLogout}
                  className="w-full px-4 py-2.5 text-left hover:bg-danger-50 transition-colors flex items-center space-x-3 text-danger-600"
                >
                  <LogOut className="h-4 w-4" />
                  <span className="text-sm font-medium">Logout</span>
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Navbar;
