import { AlertCircle } from 'lucide-react';

const ErrorMessage = ({ message, className = '' }) => {
  if (!message) return null;

  return (
    <div className={`flex items-center gap-2 p-4 bg-danger-50 border border-danger-200 rounded-lg ${className}`}>
      <AlertCircle className="h-5 w-5 text-danger-600 flex-shrink-0" />
      <p className="text-sm text-danger-800">{message}</p>
    </div>
  );
};

export default ErrorMessage;
