import { useState, useEffect, useRef } from 'react';
import { Html5Qrcode } from 'html5-qrcode';
import { X, Camera, AlertCircle } from 'lucide-react';
import Modal from '../../../components/common/Modal';
import Button from '../../../components/common/Button';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import { useProductByBarcode } from '../hooks/useBarcodeScanner';
import { toast } from 'react-toastify';

const BarcodeScannerModal = ({ isOpen, onClose, onProductFound }) => {
  const [scannedCode, setScannedCode] = useState(null);
  const [scanning, setScanning] = useState(false);
  const [error, setError] = useState(null);
  const scannerRef = useRef(null);
  const html5QrCodeRef = useRef(null);

  // Query product when barcode is scanned
  const { data: product, isLoading, error: fetchError } = useProductByBarcode(scannedCode, {
    enabled: !!scannedCode,
  });

  useEffect(() => {
    if (product && scannedCode) {
      toast.success(`Product found: ${product.name}`);
      onProductFound(product);
      handleStopScanning();
      onClose();
    }
  }, [product, scannedCode]);

  useEffect(() => {
    if (fetchError) {
      toast.error('Product not found with this barcode');
      setScannedCode(null);
    }
  }, [fetchError]);

  useEffect(() => {
    if (isOpen) {
      handleStartScanning();
    } else {
      handleStopScanning();
    }

    return () => {
      handleStopScanning();
    };
  }, [isOpen]);

  const handleStartScanning = async () => {
    try {
      setError(null);
      setScanning(true);

      html5QrCodeRef.current = new Html5Qrcode('barcode-reader');

      await html5QrCodeRef.current.start(
        { facingMode: 'environment' },
        {
          fps: 10,
          qrbox: { width: 250, height: 250 },
        },
        onScanSuccess,
        onScanError
      );
    } catch (err) {
      console.error('Camera error:', err);
      setError('Failed to access camera. Please check permissions.');
      setScanning(false);
    }
  };

  const handleStopScanning = async () => {
    if (html5QrCodeRef.current && html5QrCodeRef.current.isScanning) {
      try {
        await html5QrCodeRef.current.stop();
        html5QrCodeRef.current.clear();
      } catch (err) {
        console.error('Error stopping scanner:', err);
      }
    }
    setScanning(false);
  };

  const onScanSuccess = (decodedText, decodedResult) => {
    console.log('Barcode scanned:', decodedText);
    setScannedCode(decodedText);
  };

  const onScanError = (errorMessage) => {
    // Ignore scan errors (they happen continuously when no code is detected)
  };

  const handleClose = () => {
    handleStopScanning();
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Scan Barcode">
      <div className="space-y-4">
        {/* Scanner viewport */}
        <div className="relative bg-black rounded-lg overflow-hidden" style={{ minHeight: '400px' }}>
          <div id="barcode-reader" ref={scannerRef} className="w-full"></div>

          {/* Loading overlay */}
          {isLoading && (
            <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
              <div className="bg-white rounded-lg p-6">
                <LoadingSpinner />
                <p className="text-sm text-gray-600 mt-2">Looking up product...</p>
              </div>
            </div>
          )}

          {/* Error overlay */}
          {error && (
            <div className="absolute inset-0 bg-black bg-opacity-75 flex items-center justify-center">
              <div className="bg-white rounded-lg p-6 max-w-sm">
                <AlertCircle className="h-12 w-12 text-danger-600 mx-auto mb-3" />
                <p className="text-sm text-gray-900 text-center">{error}</p>
                <Button onClick={handleStartScanning} className="mt-4 w-full">
                  Try Again
                </Button>
              </div>
            </div>
          )}
        </div>

        {/* Instructions */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <div className="flex items-start gap-3">
            <Camera className="h-5 w-5 text-blue-600 flex-shrink-0 mt-0.5" />
            <div>
              <h4 className="font-semibold text-blue-900 text-sm">Scanning Instructions</h4>
              <ul className="text-sm text-blue-800 mt-2 space-y-1">
                <li>• Position the barcode within the square</li>
                <li>• Ensure good lighting for best results</li>
                <li>• Hold steady until the code is detected</li>
                <li>• Works with QR codes, EAN-13, UPC, and more</li>
              </ul>
            </div>
          </div>
        </div>

        {/* Manual entry option */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Or enter barcode manually:
          </label>
          <div className="flex gap-2">
            <input
              type="text"
              placeholder="Enter barcode number"
              className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              onKeyPress={(e) => {
                if (e.key === 'Enter') {
                  setScannedCode(e.target.value);
                }
              }}
            />
            <Button
              onClick={(e) => {
                const input = e.target.previousSibling;
                if (input.value) {
                  setScannedCode(input.value);
                }
              }}
            >
              Lookup
            </Button>
          </div>
        </div>

        {/* Close button */}
        <div className="flex justify-end pt-4 border-t border-gray-200">
          <Button variant="secondary" onClick={handleClose}>
            Cancel
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default BarcodeScannerModal;
