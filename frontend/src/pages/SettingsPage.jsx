import { useState } from 'react';
import { Settings, Shield, User, Lock } from 'lucide-react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/common/Tabs';
import TwoFactorSettings from '../features/auth/components/TwoFactorSettings';

const SettingsPage = () => {
  const [activeTab, setActiveTab] = useState('security');

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Account Settings</h1>
        <p className="text-gray-600 mt-2">Manage your account security and preferences</p>
      </div>

      <div className="bg-white rounded-lg shadow-soft">
        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsList className="border-b border-gray-200">
            <TabsTrigger value="security" icon={Lock}>
              Security
            </TabsTrigger>
            <TabsTrigger value="profile" icon={User}>
              Profile
            </TabsTrigger>
            <TabsTrigger value="notifications" icon={Settings}>
              Notifications
            </TabsTrigger>
          </TabsList>

          <div className="p-6">
            <TabsContent value="security">
              <div className="space-y-8">
                <div>
                  <h2 className="text-lg font-semibold text-gray-900 mb-4">Security Settings</h2>
                  <TwoFactorSettings />
                </div>
              </div>
            </TabsContent>

            <TabsContent value="profile">
              <div className="space-y-6">
                <h2 className="text-lg font-semibold text-gray-900">Profile Information</h2>
                <p className="text-gray-600">Profile settings coming soon...</p>
              </div>
            </TabsContent>

            <TabsContent value="notifications">
              <div className="space-y-6">
                <h2 className="text-lg font-semibold text-gray-900">Notification Preferences</h2>
                <p className="text-gray-600">Notification settings coming soon...</p>
              </div>
            </TabsContent>
          </div>
        </Tabs>
      </div>
    </div>
  );
};

export default SettingsPage;
