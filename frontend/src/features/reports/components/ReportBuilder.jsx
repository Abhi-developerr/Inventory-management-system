import { useState } from 'react';
import Modal from '../../../components/common/Modal';
import Button from '../../../components/common/Button';
import Input from '../../../components/common/Input';

const ReportBuilder = ({ isOpen, onClose, onGenerate, isLoading }) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    reportType: 'SALES_SUMMARY',
    startDate: '',
    endDate: '',
    fileFormat: 'PDF',
  });

  const reportTypes = [
    { value: 'SALES_SUMMARY', label: 'Sales Summary' },
    { value: 'INVENTORY_STATUS', label: 'Inventory Status' },
    { value: 'LOW_STOCK_ALERT', label: 'Low Stock Alert' },
    { value: 'ORDER_ANALYSIS', label: 'Order Analysis' },
    { value: 'SUPPLIER_PERFORMANCE', label: 'Supplier Performance' },
    { value: 'REVENUE_TREND', label: 'Revenue Trend' },
    { value: 'STOCK_MOVEMENT', label: 'Stock Movement' },
    { value: 'PRODUCT_PERFORMANCE', label: 'Product Performance' },
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onGenerate(formData);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Create New Report">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Report Name *
          </label>
          <Input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="Monthly Sales Report"
            required
            disabled={isLoading}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Description
          </label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Report details..."
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            rows="3"
            disabled={isLoading}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Report Type *
          </label>
          <select
            name="reportType"
            value={formData.reportType}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            disabled={isLoading}
          >
            {reportTypes.map(type => (
              <option key={type.value} value={type.value}>
                {type.label}
              </option>
            ))}
          </select>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Start Date
            </label>
            <Input
              type="date"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
              disabled={isLoading}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              End Date
            </label>
            <Input
              type="date"
              name="endDate"
              value={formData.endDate}
              onChange={handleChange}
              disabled={isLoading}
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Export Format
          </label>
          <select
            name="fileFormat"
            value={formData.fileFormat}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
            disabled={isLoading}
          >
            <option value="PDF">PDF</option>
            <option value="EXCEL">Excel</option>
            <option value="CSV">CSV</option>
          </select>
        </div>

        <div className="flex gap-3 pt-4 border-t border-gray-200">
          <Button
            type="button"
            onClick={onClose}
            variant="secondary"
            className="flex-1"
            disabled={isLoading}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            className="flex-1"
            disabled={isLoading || !formData.name}
            loading={isLoading}
          >
            Generate Report
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default ReportBuilder;
