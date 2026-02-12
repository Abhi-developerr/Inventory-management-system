import { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import Modal from '../../../components/common/Modal';
import Input from '../../../components/common/Input';
import Button from '../../../components/common/Button';
import { toast } from 'react-toastify';

const TwoFactorVerifyModal = ({ isOpen, onClose, onVerify, isLoading, allowRecovery = true }) => {
  const [code, setCode] = useState('');
  const [useRecovery, setUseRecovery] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!code || code.length < (useRecovery ? 8 : 6)) {
      toast.error(`Please enter a valid ${useRecovery ? '8-character' : '6-digit'} code`);
      return;
    }
    onVerify(code, useRecovery);
    setCode('');
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Two-Factor Authentication">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-900 mb-2">
            {useRecovery ? 'Recovery Code' : 'Authenticator Code'}
          </label>
          <p className="text-sm text-gray-600 mb-3">
            {useRecovery
              ? 'Enter one of your 8-character recovery codes'
              : 'Enter the 6-digit code from your authenticator app'}
          </p>
          <Input
            type="text"
            inputMode="numeric"
            placeholder={useRecovery ? '12345678' : '000000'}
            maxLength={useRecovery ? '8' : '6'}
            value={code}
            onChange={(e) => setCode(e.target.value.toUpperCase().replace(/[^0-9A-Z]/g, ''))}
            className="font-mono text-center text-2xl tracking-widest"
            disabled={isLoading}
            autoFocus
          />
        </div>

        {allowRecovery && (
          <button
            type="button"
            onClick={() => {
              setUseRecovery(!useRecovery);
              setCode('');
            }}
            className="text-sm text-primary-600 hover:text-primary-700"
          >
            {useRecovery ? 'Use authenticator code instead' : 'Lost your phone? Use recovery code'}
          </button>
        )}

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
            disabled={isLoading || code.length < (useRecovery ? 8 : 6)}
            loading={isLoading}
          >
            Verify
          </Button>
        </div>
      </form>
    </Modal>
  );
};

export default TwoFactorVerifyModal;
