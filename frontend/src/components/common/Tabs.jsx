import React from 'react';

export const Tabs = ({ value, onValueChange, children, className = '' }) => {
  return <div className={className}>{children}</div>;
};

export const TabsList = ({ children, className = '' }) => {
  return (
    <div className={`flex space-x-1 ${className}`}>
      {children}
    </div>
  );
};

export const TabsTrigger = ({ value, children, icon: Icon, onClick, ...props }) => {
  const isActive = props['data-active'];
  return (
    <button
      className={`flex items-center gap-2 px-4 py-3 font-medium text-sm transition-colors ${
        isActive
          ? 'text-primary-600 border-b-2 border-primary-600'
          : 'text-gray-600 hover:text-gray-900'
      }`}
      {...props}
    >
      {Icon && <Icon className="h-4 w-4" />}
      {children}
    </button>
  );
};

export const TabsContent = ({ value, children, className = '' }) => {
  return <div className={className}>{children}</div>;
};
