import { TrendingUp, TrendingDown } from 'lucide-react';

const StatsCard = ({ title, value, icon: Icon, trend, trendValue, colorClass = 'text-primary-600' }) => {
  return (
    <div className="bg-white rounded-lg shadow-soft p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-3xl font-bold text-gray-900 mt-2">{value}</p>
          {trend && (
            <div className="flex items-center gap-1 mt-2">
              {trend === 'up' ? (
                <TrendingUp className="h-4 w-4 text-success-600" />
              ) : (
                <TrendingDown className="h-4 w-4 text-danger-600" />
              )}
              <span className={`text-sm font-medium ${trend === 'up' ? 'text-success-600' : 'text-danger-600'}`}>
                {trendValue}
              </span>
              <span className="text-sm text-gray-500">vs last month</span>
            </div>
          )}
        </div>
        <div className={`p-3 rounded-full bg-opacity-10 ${colorClass}`}>
          <Icon className={`h-8 w-8 ${colorClass}`} />
        </div>
      </div>
    </div>
  );
};

export default StatsCard;
