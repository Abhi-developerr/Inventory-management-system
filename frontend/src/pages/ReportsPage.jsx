import { useState } from 'react';
import { Plus } from 'lucide-react';
import Button from '../components/common/Button';
import Pagination from '../components/common/Pagination';
import { useReports, useGenerateReport } from '../features/reports/hooks/useReportOperations';
import ReportList from '../features/reports/components/ReportList';
import ReportBuilder from '../features/reports/components/ReportBuilder';

const ReportsPage = () => {
  const [currentPage, setCurrentPage] = useState(0);
  const [builderOpen, setBuilderOpen] = useState(false);
  const [selectedReport, setSelectedReport] = useState(null);

  const { data: reportsData, isLoading } = useReports(currentPage, 20);
  const generateMutation = useGenerateReport();

  const reports = reportsData?.content || [];
  const totalPages = reportsData?.totalPages || 0;

  const handlePageChange = (page) => {
    setCurrentPage(page - 1);
  };

  const handleViewReport = (report) => {
    setSelectedReport(report);
    // Could show a preview modal here
  };

  const handleScheduleReport = (report) => {
    // TODO: Show schedule configuration modal
  };

  const handleCreateReport = (reportData) => {
    generateMutation.mutate(reportData, {
      onSuccess: () => {
        setBuilderOpen(false);
      },
    });
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Reports</h1>
          <p className="text-gray-600 mt-1">Generate, manage, and schedule business reports</p>
        </div>
        <Button
          onClick={() => setBuilderOpen(true)}
          icon={Plus}
          className="gap-2"
        >
          New Report
        </Button>
      </div>

      {/* Report Type Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <ReportTypeCard
          title="Sales Summary"
          description="Revenue, orders, top products"
          type="SALES_SUMMARY"
          onClick={() => {
            setBuilderOpen(true);
            setSelectedReport({ reportType: 'SALES_SUMMARY' });
          }}
        />
        <ReportTypeCard
          title="Inventory Status"
          description="Stock levels, low stock items"
          type="INVENTORY_STATUS"
          onClick={() => {
            setBuilderOpen(true);
            setSelectedReport({ reportType: 'INVENTORY_STATUS' });
          }}
        />
        <ReportTypeCard
          title="Low Stock Alert"
          description="Products below threshold"
          type="LOW_STOCK_ALERT"
          onClick={() => {
            setBuilderOpen(true);
            setSelectedReport({ reportType: 'LOW_STOCK_ALERT' });
          }}
        />
      </div>

      {/* Reports List */}
      <div className="bg-white rounded-lg shadow-soft p-6">
        <ReportList
          reports={reports}
          isLoading={isLoading}
          onView={handleViewReport}
          onSchedule={handleScheduleReport}
        />

        {totalPages > 1 && (
          <div className="mt-6">
            <Pagination
              currentPage={currentPage + 1}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          </div>
        )}
      </div>

      {/* Report Builder Modal */}
      {builderOpen && (
        <ReportBuilder
          isOpen={builderOpen}
          onClose={() => {
            setBuilderOpen(false);
            setSelectedReport(null);
          }}
          onGenerate={handleCreateReport}
          isLoading={generateMutation.isPending}
        />
      )}
    </div>
  );
};

const ReportTypeCard = ({ title, description, type, onClick }) => {
  return (
    <div
      onClick={onClick}
      className="bg-white p-4 rounded-lg border border-gray-200 hover:border-primary-500 hover:shadow-md cursor-pointer transition-all"
    >
      <h3 className="font-semibold text-gray-900">{title}</h3>
      <p className="text-sm text-gray-600 mt-1">{description}</p>
    </div>
  );
};

export default ReportsPage;
