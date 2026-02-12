import { useState } from 'react';
import { Copy, Download, Eye, EyeOff } from 'lucide-react';
import Button from '../../../components/common/Button';
import Modal from '../../../components/common/Modal';
import Input from '../../../components/common/Input';
import { toast } from 'react-toastify';

const TwoFactorSetupModal = ({ isOpen, onClose, secret, qrCode, backupCodes, onVerify, isLoading }) => {
  const [totpCode, setTotpCode] = useState('');
  const [showCodes, setShowCodes] = useState(false);
  const [copiedIndex, setCopiedIndex] = useState(null);

  const handleVerify = () => {
    if (!totpCode || totpCode.length !== 6) {
      toast.error('Please enter a valid 6-digit code');
      return;
    }
    onVerify(totpCode, backupCodes);
  };

  const copyCode = (code, index) => {
    navigator.clipboard.writeText(code);
    setCopiedIndex(index);
    setTimeout(() => setCopiedIndex(null), 2000);
  };

  const downloadCodes = () => {
    const text = backupCodes.join('\n');
    const element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', 'recovery-codes.txt');
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Setup Two-Factor Authentication">
      <div className="space-y-6">
        {/* Step 1: QR Code */}
        <div>
          <h3 className="text-sm font-semibold text-gray-900 mb-3">Step 1: Scan QR Code</h3>
          <p className="text-sm text-gray-600 mb-4">
            Scan this QR code with your authenticator app (Google Authenticator, Authy, Microsoft Authenticator)
          </p>
          {qrCode && (
            <div className="flex justify-center p-4 bg-gray-50 rounded-lg">
              <img src={`data:image/png;base64,${qrCode}`} alt="2FA QR Code" className="w-48 h-48" />
            </div>
          )}
          <details className="mt-3 p-3 bg-blue-50 rounded-lg">
            <summary className="text-sm font-medium text-blue-900 cursor-pointer">Can't scan? Enter manually</summary>
            <p className="text-sm text-blue-800 mt-2 break-all font-mono">{secret}</p>
          </details>
        </div>

        {/* Step 2: Verify Code */}
        <div>
          <h3 className="text-sm font-semibold text-gray-900 mb-3">Step 2: Verify Code</h3>
          <p className="text-sm text-gray-600 mb-4">Enter the 6-digit code from your authenticator app</p>
          <Input
            type="text"
            inputMode="numeric"
            placeholder="000000"
            maxLength="6"
            value={totpCode}
            onChange={(e) => setTotpCode(e.target.value.replace(/\D/g, ''))}
            className="font-mono text-center text-2xl tracking-widest"
          />
        </div>

        {/* Step 3: Recovery Codes */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <h3 className="text-sm font-semibold text-gray-900">Step 3: Save Recovery Codes</h3>
            <button
              onClick={() => setShowCodes(!showCodes)}
              className="p-1 text-gray-600 hover:text-gray-900"
            >
              {showCodes ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
            </button>
          </div>
          <p className="text-sm text-gray-600 mb-4">
            Save these codes in a safe place. Use them to regain access if you lose your authenticator app.
          </p>
          <div className="space-y-2 max-h-40 overflow-y-auto p-3 bg-gray-50 rounded-lg border border-gray-200">
            {backupCodes?.map((code, idx) => (
              <div
                key={idx}
                className="flex items-center justify-between p-2 bg-white rounded border border-gray-200"
              >
                <span className={`text-sm font-mono ${showCodes ? 'text-gray-900' : 'text-gray-400'}`}>
                  {showCodes ? code : '••••••••'}
                </span>
                <button
                  onClick={() => copyCode(code, idx)}
                  className="p-1 text-gray-600 hover:text-gray-900"
                  title="Copy"
                >
                  {copiedIndex === idx ? (
                    <span className="text-xs text-green-600">Copied</span>
                  ) : (
                    <Copy className="h-4 w-4" />
                  )}
                </button>
              </div>
            ))}
          </div>
          <Button
            onClick={downloadCodes}
            variant="secondary"
            className="mt-3 w-full gap-2"
            icon={Download}
          >
            Download Recovery Codes
          </Button>
        </div>

        {/* Actions */}
        <div className="flex gap-3 pt-4 border-t border-gray-200">
          <Button
            onClick={onClose}
            variant="secondary"
            className="flex-1"
            disabled={isLoading}
          >
            Cancel
          </Button>
          <Button
            onClick={handleVerify}
            className="flex-1"
            disabled={isLoading || totpCode.length !== 6}
            loading={isLoading}
          >
            Enable 2FA
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default TwoFactorSetupModal;
