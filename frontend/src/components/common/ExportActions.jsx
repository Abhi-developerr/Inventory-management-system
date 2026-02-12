import { useState } from 'react';
import { exportAPI } from '../../api/advanced.api';
import Button from './Button';

function ExportActions({ type = 'products' }) {
  const [exporting, setExporting] = useState(false);

  const handleExport = async (format) => {
    try {
      setExporting(true);
      let response;
      
      if (type === 'products') {
        response = format === 'excel' 
          ? await exportAPI.exportProductsToExcel()
          : await exportAPI.exportProductsToCSV();
      } else if (type === 'orders') {
        response = await exportAPI.exportOrdersToExcel();
      }

      // Create download link
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      const extension = format === 'excel' ? 'xlsx' : 'csv';
      link.setAttribute('download', `${type}_${timestamp}.${extension}`);
      
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Export failed:', error);
      alert('Export failed. Please try again.');
    } finally {
      setExporting(false);
    }
  };

  return (
    <div className="flex gap-2">
      {type === 'products' && (
        <Button
          variant="secondary"
          onClick={() => handleExport('csv')}
          disabled={exporting}
        >
          {exporting ? 'Exporting...' : '📄 Export CSV'}
        </Button>
      )}
      <Button
        variant="secondary"
        onClick={() => handleExport('excel')}
        disabled={exporting}
      >
        {exporting ? 'Exporting...' : '📊 Export Excel'}
      </Button>
    </div>
  );
}

export default ExportActions;
