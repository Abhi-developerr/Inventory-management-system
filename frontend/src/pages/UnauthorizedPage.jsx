import { Link } from 'react-router-dom';
import { Home, ShieldAlert } from 'lucide-react';
import Button from '../components/common/Button';

const UnauthorizedPage = () => {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="text-center">
        <div className="flex justify-center mb-6">
          <ShieldAlert className="h-24 w-24 text-danger-500" />
        </div>
        <h1 className="text-6xl font-bold text-gray-900 mb-4">403</h1>
        <p className="text-xl text-gray-600 mb-2">
          Access Denied
        </p>
        <p className="text-gray-500 mb-8">
          You don't have permission to access this resource.
        </p>
        <Link to="/dashboard">
          <Button variant="primary" className="inline-flex items-center gap-2">
            <Home className="h-4 w-4" />
            Back to Dashboard
          </Button>
        </Link>
      </div>
    </div>
  );
};

export default UnauthorizedPage;
