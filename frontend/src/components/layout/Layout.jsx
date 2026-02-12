import Sidebar from './Sidebar';
import Navbar from './Navbar';
import { useInventoryUpdates } from '../../hooks/useInventoryUpdates';

const Layout = ({ children }) => {
  useInventoryUpdates(true);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Sidebar */}
      <Sidebar />

      {/* Main content */}
      <div className="lg:ml-64">
        {/* Navbar */}
        <Navbar />

        {/* Page content */}
        <main className="p-4 md:p-6 lg:p-8 pt-20 lg:pt-8">
          {children}
        </main>
      </div>
    </div>
  );
};

export default Layout;
