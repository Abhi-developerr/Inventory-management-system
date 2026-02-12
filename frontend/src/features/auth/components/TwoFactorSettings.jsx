import { useState } from 'react';
import { Shield, RotateCcw, Trash2 } from 'lucide-react';
import Button from '../../../components/common/Button';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import {
  useTwoFactorStatus,
  useInitiateTwoFactor,
  useVerifyAndEnable2FA,
  useDisableTwoFactor,
  useRegenerateRecoveryCodes,
} from '../hooks/useTwoFactorAuth';
import TwoFactorSetupModal from './TwoFactorSetupModal';
import TwoFactorVerifyModal from './TwoFactorVerifyModal';

const TwoFactorSettings = () => {
  const [setupModalOpen, setSetupModalOpen] = useState(false);
  const [setupData, setSetupData] = useState(null);
  const [verifyModalOpen, setVerifyModalOpen] = useState(false);

  const { data: status, isLoading: statusLoading } = useTwoFactorStatus();
  const initiateMutation = useInitiateTwoFactor();
  const enableMutation = useVerifyAndEnable2FA();
  const disableMutation = useDisableTwoFactor();
  const regenerateMutation = useRegenerateRecoveryCodes();

  const handleInitiate = async () => {
    const data = await initiateMutation.mutateAsync();
    setSetupData(data);
    setSetupModalOpen(true);
  };

  const handleVerifyAndEnable = async (totpCode, backupCodes) => {
    await enableMutation.mutateAsync({
      totpCode,
      recoveryCodes: backupCodes,
    });
    setSetupModalOpen(false);
    setSetupData(null);
  };

  const handleDisable = () => {
    if (window.confirm('Disable 2FA? You will need to set it up again to re-enable it.')) {
      disableMutation.mutate();
    }
  };

  const handleRegenerate = () => {
    if (window.confirm('Generate new recovery codes? Old codes will no longer work.')) {
      regenerateMutation.mutate();
    }
  };

  if (statusLoading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="space-y-6">
      <div className="bg-white p-6 rounded-lg shadow-soft">
        <div className="flex items-start justify-between">
          <div className="flex items-start gap-4">
            <div className="p-3 bg-primary-100 rounded-lg">
              <Shield className="h-6 w-6 text-primary-600" />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-900">Two-Factor Authentication</h3>
              <p className="text-sm text-gray-600 mt-1">
                Strengthen your account security with time-based authentication codes
              </p>
              <div className="mt-4 flex items-center gap-3">
                {status?.enabled ? (
                  <div className="flex items-center gap-2">
                    <div className="h-3 w-3 rounded-full bg-success-600" />
                    <span className="text-sm font-medium text-success-600">Enabled</span>
                  </div>
                ) : (
                  <div className="flex items-center gap-2">
                    <div className="h-3 w-3 rounded-full bg-gray-400" />
                    <span className="text-sm font-medium text-gray-600">Not enabled</span>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="flex flex-col gap-2">
            {!status?.enabled ? (
              <Button
                onClick={handleInitiate}
                loading={initiateMutation.isPending}
              >
                Enable 2FA
              </Button>
            ) : (
              <>
                <Button
                  variant="secondary"
                  onClick={handleRegenerate}
                  icon={RotateCcw}
                  loading={regenerateMutation.isPending}
                  className="gap-2"
                >
                  New Recovery Codes
                </Button>
                <Button
                  variant="danger"
                  onClick={handleDisable}
                  icon={Trash2}
                  loading={disableMutation.isPending}
                  className="gap-2"
                >
                  Disable 2FA
                </Button>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Info Box */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <h4 className="font-semibold text-blue-900 text-sm">How it works</h4>
        <ul className="text-sm text-blue-800 mt-2 space-y-1">
          <li>• Download an authenticator app (Google Authenticator, Authy, or Microsoft Authenticator)</li>
          <li>• Scan the QR code or enter the secret key manually</li>
          <li>• When logging in, you'll be asked for a 6-digit code from your app</li>
          <li>• Save your recovery codes in case you lose access to your authenticator app</li>
        </ul>
      </div>

      {/* Modals */}
      {setupData && (
        <TwoFactorSetupModal
          isOpen={setupModalOpen}
          onClose={() => {
            setSetupModalOpen(false);
            setSetupData(null);
          }}
          secret={setupData.secret}
          qrCode={setupData.qrCode}
          backupCodes={setupData.backupCodes}
          onVerify={handleVerifyAndEnable}
          isLoading={enableMutation.isPending}
        />
      )}
    </div>
  );
};

export default TwoFactorSettings;
