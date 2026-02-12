import { Download, Eye, Trash2, Clock } from 'lucide-react';
import { formatDateTime, formatCurrency } from '../../../utils/formatters';
import Table from '../../../components/common/Table';
import Button from '../../../components/common/Button';
import { useDownloadReport, useDeleteReport } from '../hooks/useReportOperations';

const ReportList = ({ reports, isLoading, onView, onSchedule }) => {
  const downloadMutation = useDownloadReport();
  const deleteMutation = useDeleteReport();

  const columns = [
    {
      header: 'Name',
      accessor: 'name',
      render: (row) => (
        <div>
          <p className="font-medium text-gray-900">{row.name}</p>
          <p className="text-xs text-gray-500 mt-1">{row.reportType}</p>
        </div>
      ),
    },
    {
      header: 'Description',
      accessor: 'description',
      render: (row) => (
        <p className="text-sm text-gray-600 max-w-xs truncate">{row.description}</p>
      ),
    },
    {
      header: 'Generated',
      accessor: 'generatedAt',
      render: (row) => (
        <div>
          <p className="text-sm font-medium text-gray-900">
            {row.generatedAt ? formatDateTime(row.generatedAt) : 'Not generated'}
          </p>
          <p className="text-xs text-gray-500 mt-1">by {row.generatedBy || 'N/A'}</p>
        </div>
      ),
    },
    {
      header: 'Format',
      accessor: 'fileFormat',
      render: (row) => (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
          {row.fileFormat}
        </span>
      ),
    },
    {
      header: 'Size',
      accessor: 'fileSize',
      render: (row) => (
        <span className="text-sm text-gray-600">
          {row.fileSize ? (row.fileSize / 1024).toFixed(2) + ' KB' : 'N/A'}
        </span>
      ),
    },
    {
      header: 'Actions',
      accessor: 'id',
      render: (row) => (
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => onView(row)}
            icon={Eye}
            title="View"
          />
          <Button
            variant="ghost"
            size="sm"
            onClick={() => downloadMutation.mutate(row.id)}
            icon={Download}
            title="Download"
            disabled={!row.generatedAt}
          />
          <Button
            variant="ghost"
            size="sm"
            onClick={() => onSchedule(row)}
            icon={Clock}
            title="Schedule"
          />
          <Button
            variant="ghost"
            size="sm"
            onClick={() => {
              if (window.confirm('Delete this report?')) {
                deleteMutation.mutate(row.id);
              }
            }}
            icon={Trash2}
            className="text-danger-600"
            title="Delete"
            disabled={deleteMutation.isPending}
          />
        </div>
      ),
    },
  ];

  return (
    <Table
      columns={columns}
      data={reports || []}
      loading={isLoading}
      emptyMessage="No reports found. Create one to get started."
    />
  );
};

export default ReportList;
