import React, { useState, useEffect, useRef } from 'react';
import { Plus, RefreshCw, Building, CheckCircle, AlertCircle } from 'lucide-react';
import { toast } from 'react-toastify';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Modal } from '../components/ui/Modal';
import { LoadingSpinner } from '../components/ui/LoadingSpinner';
import { formatCurrency, formatDateTime } from '../utils/formatters';
import { BankAccount, ConnectBankRequest, AccountType } from '../types';
import { useQueryClient } from '@tanstack/react-query';
import { useBankAccounts, useConnectBank, useInitiateSyncBank, checkJobStatus } from '../hooks/useBankAccounts';

const accountTypes: { value: AccountType; label: string }[] = [
  { value: 'CHECKING', label: 'Checking Account' },
  { value: 'SAVINGS', label: 'Savings Account' }
];

export const BankConnectionsPage: React.FC = () => {
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const pendingSyncKeys = useRef<Record<number, string>>({});
  
  // Load active jobs from localStorage
  const [activeJobs, setActiveJobs] = useState<Record<number, string>>(() => {
    try {
      const saved = localStorage.getItem('active_sync_jobs');
      return saved ? JSON.parse(saved) : {};
    } catch {
      return {};
    }
  });

  // Persist active jobs to localStorage
  useEffect(() => {
    localStorage.setItem('active_sync_jobs', JSON.stringify(activeJobs));
  }, [activeJobs]);

  const [formData, setFormData] = useState<ConnectBankRequest & { consent: boolean }>({
    bankName: '',
    accountType: 'CHECKING',
    nickName: '',
    accountNum: '',
    consent: false
  });
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  const { data: accounts = [], isLoading } = useBankAccounts();
  const connectMutation = useConnectBank();
  const initiateSyncMutation = useInitiateSyncBank();
  const queryClient = useQueryClient();

  // Poll for active jobs
  useEffect(() => {
    const bankIds = Object.keys(activeJobs).map(Number);
    if (bankIds.length === 0) return;

    const intervalId = setInterval(async () => {
      for (const bankId of bankIds) {
        const jobId = activeJobs[bankId];
        try {
          const job = await checkJobStatus(jobId);
          
          if (job.status === 'COMPLETED') {
            toast.success(job.result || 'Sync completed successfully');
            
            // Remove from active jobs
            setActiveJobs(prev => {
              const next = { ...prev };
              delete next[bankId];
              return next;
            });
            
            // Refresh data
            queryClient.invalidateQueries({ queryKey: ['bankAccounts'] });
            queryClient.invalidateQueries({ queryKey: ['transactions'] });
            queryClient.invalidateQueries({ queryKey: ['dashboard'] });
          } else if (job.status === 'FAILED') {
            toast.error(job.result || 'Sync failed');
            
            // Remove from active jobs
            setActiveJobs(prev => {
              const next = { ...prev };
              delete next[bankId];
              return next;
            });
          }
        } catch (error) {
          console.error('Error polling job status:', error);
        }
      }
    }, 2000);

    return () => clearInterval(intervalId);
  }, [activeJobs, queryClient]);

  const validateForm = () => {
    const errors: Record<string, string> = {};
    
    if (!formData.bankName.trim()) {
      errors.bankName = 'Bank name is required';
    } else if (formData.bankName.length < 3 || formData.bankName.length > 15) {
      errors.bankName = 'Bank name must be between 3 and 15 characters';
    }
    
    if (!formData.nickName.trim()) {
      errors.nickName = 'Account nickname is required';
    } else if (formData.nickName.length < 4 || formData.nickName.length > 20) {
      errors.nickName = 'Nickname must be between 4 and 20 characters';
    }
    
    if (!formData.accountNum.trim()) {
      errors.accountNum = 'Account number is required';
    } else if (!/^\d{8,12}$/.test(formData.accountNum)) {
      errors.accountNum = 'Account number must be 8-12 digits';
    }
    
    if (!formData.consent) {
      errors.consent = 'You must agree to the terms and conditions';
    }
    
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleConnectBank = async () => {
    if (!validateForm()) return;
    
    try {
      const { consent, ...requestData } = formData;
      await connectMutation.mutateAsync(requestData);
      handleCloseModal();
    } catch (error) {
      console.error('Error connecting bank:', error);
    }
  };

  const handleCloseModal = () => {
    setIsAddModalOpen(false);
    setFormData({
      bankName: '',
      accountType: 'CHECKING',
      nickName: '',
      accountNum: '',
      consent: false
    });
    setFormErrors({});
  };

  const handleSync = async (accountId: number) => {
    try {
      // Get existing key or generate new one
      let key = pendingSyncKeys.current[accountId];
      if (!key) {
        key = crypto.randomUUID();
        pendingSyncKeys.current[accountId] = key;
      }

      toast.info('Initiating sync...');
      const { jobId } = await initiateSyncMutation.mutateAsync({ 
        bankId: accountId, 
        idempotencyKey: key 
      });

      // Clear key on success
      delete pendingSyncKeys.current[accountId];
      
      setActiveJobs(prev => ({ ...prev, [accountId]: jobId }));
    } catch (error) {
      console.error('Error initiating sync:', error);
      // Clear key on error to allow retry
      delete pendingSyncKeys.current[accountId];
      toast.error('Failed to start sync');
    }
  };

  const getConnectionStatus = (account: BankAccount) => {
    const lastSyncDate = new Date(account.lastSync);
    const hoursSinceSync = (Date.now() - lastSyncDate.getTime()) / (1000 * 60 * 60);
    
    if (hoursSinceSync > 24) {
      return {
        icon: AlertCircle,
        text: 'Sync Required',
        color: 'text-yellow-600 dark:text-yellow-400',
        bgColor: 'bg-yellow-100 dark:bg-yellow-900'
      };
    }
    
    return {
      icon: CheckCircle,
      text: 'Connected',
      color: 'text-green-600 dark:text-green-400',
      bgColor: 'bg-green-100 dark:bg-green-900'
    };
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
          Bank Connections
        </h1>
        <Button onClick={() => setIsAddModalOpen(true)} className="whitespace-nowrap">
          <Plus className="h-4 w-4 mr-2" />
          Connect Bank
        </Button>
      </div>

      {/* Connection Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-blue-100 dark:bg-blue-900">
              <Building className="h-6 w-6 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600 dark:text-gray-400">
                Connected Banks
              </p>
              <p className="text-2xl font-bold text-gray-900 dark:text-white">
                {accounts.length}
              </p>
            </div>
          </div>
        </Card>
        
        <Card>
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-green-100 dark:bg-green-900">
              <CheckCircle className="h-6 w-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600 dark:text-gray-400">
                Total Balance
              </p>
              <p className="text-2xl font-bold text-gray-900 dark:text-white">
                {formatCurrency(accounts.reduce((sum, acc) => sum + acc.balance, 0))}
              </p>
            </div>
          </div>
        </Card>
        
        <Card>
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-yellow-100 dark:bg-yellow-900">
              <AlertCircle className="h-6 w-6 text-yellow-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600 dark:text-gray-400">
                Need Sync
              </p>
              <p className="text-2xl font-bold text-gray-900 dark:text-white">
                {accounts.filter(acc => {
                  const hoursSinceSync = (Date.now() - new Date(acc.lastSync).getTime()) / (1000 * 60 * 60);
                  return hoursSinceSync > 24;
                }).length}
              </p>
            </div>
          </div>
        </Card>
      </div>

      {/* Bank Accounts List */}
      <div className="space-y-4">
        {accounts.map((account) => {
          const status = getConnectionStatus(account);
          const StatusIcon = status.icon;
          const isSyncing = !!activeJobs[account.id];
          
          return (
            <Card key={account.id}>
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-4">
                  <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900 rounded-full flex items-center justify-center">
                    <Building className="h-6 w-6 text-blue-600" />
                  </div>
                  
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
                      {account.nickName}
                    </h3>
                    <p className="text-sm text-gray-500 dark:text-gray-400">
                      {account.bankName} • {account.maskedAccountNum}
                    </p>
                    <p className="text-xs text-gray-400 dark:text-gray-500">
                      Last sync: {formatDateTime(new Date(account.lastSync))}
                    </p>
                  </div>
                </div>
                
                <div className="flex items-center space-x-4">
                  <div className="text-right">
                    <p className="text-xl font-bold text-gray-900 dark:text-white">
                      {formatCurrency(account.balance)}
                    </p>
                    <div className={`inline-flex items-center px-2 py-1 rounded-full text-xs ${status.bgColor} ${status.color}`}>
                      <StatusIcon className="h-3 w-3 mr-1" />
                      {status.text}
                    </div>
                  </div>
                  
                  <div className="flex space-x-2">
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleSync(account.id)}
                      disabled={isSyncing}
                    >
                      <RefreshCw className={`h-4 w-4 ${isSyncing ? 'animate-spin' : ''}`} />
                    </Button>
                  </div>
                </div>
              </div>
            </Card>
          );
        })}
      </div>

      {accounts.length === 0 && (
        <Card>
          <div className="text-center py-12">
            <Building className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
              No bank accounts connected
            </h3>
            <p className="text-gray-500 dark:text-gray-400 mb-4">
              Connect your first bank account to start tracking your finances automatically.
            </p>
            <Button onClick={() => setIsAddModalOpen(true)} className="whitespace-nowrap">
              <Plus className="h-4 w-4 mr-2" />
              Connect Your First Bank
            </Button>
          </div>
        </Card>
      )}

      {/* Add Bank Connection Modal */}
      <Modal
        isOpen={isAddModalOpen}
        onClose={handleCloseModal}
        title="Connect Bank Account"
        size="lg"
      >
        <form onSubmit={(e) => { e.preventDefault(); handleConnectBank(); }} className="space-y-6">
          <div className="bg-blue-50 dark:bg-blue-900 p-4 rounded-lg">
            <p className="text-sm text-blue-800 dark:text-blue-200">
              <strong>Demo Mode:</strong> This is a demonstration. No real banking credentials are required or stored.
            </p>
          </div>

          <Input
            label="Bank Name"
            value={formData.bankName}
            onChange={(e) => setFormData({ ...formData, bankName: e.target.value })}
            error={formErrors.bankName}
            placeholder="Enter your bank name (e.g., Chase Bank)"
          />

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Account Type
            </label>
            <select
              value={formData.accountType}
              onChange={(e) => setFormData({ ...formData, accountType: e.target.value as AccountType })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:placeholder-gray-400 dark:focus:ring-blue-400 dark:focus:border-blue-400"
            >
              {accountTypes.map((type) => (
                <option key={type.value} value={type.value}>
                  {type.label}
                </option>
              ))}
            </select>
          </div>

          <Input
            label="Account Nickname"
            value={formData.nickName}
            onChange={(e) => setFormData({ ...formData, nickName: e.target.value })}
            error={formErrors.nickName}
            placeholder="Enter a nickname for this account (e.g., Main Checking)"
            helper="This helps you identify the account in your dashboard"
          />

          <Input
            label="Account Number"
            value={formData.accountNum}
            onChange={(e) => setFormData({ ...formData, accountNum: e.target.value })}
            error={formErrors.accountNum}
            placeholder="Enter your account number"
            helper="Only the last 4 digits will be displayed for security"
          />

          <div className="space-y-2">
            <div className="flex items-start">
              <input
                type="checkbox"
                id="consent"
                checked={formData.consent}
                onChange={(e) => setFormData({ ...formData, consent: e.target.checked })}
                className="mt-1 h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label htmlFor="consent" className="ml-2 text-sm text-gray-700 dark:text-gray-300">
                I agree to connect this bank account and allow FinanceTracker to access my account information for transaction tracking purposes. I understand this is a demo application and no real banking data will be accessed.
              </label>
            </div>
            {formErrors.consent && (
              <p className="text-sm text-red-600 dark:text-red-400">{formErrors.consent}</p>
            )}
          </div>
          
          <div className="flex justify-end space-x-3">
            <Button 
              type="button" 
              variant="secondary" 
              onClick={handleCloseModal}
            >
              Cancel
            </Button>
            <Button 
              type="submit"
              disabled={connectMutation.isPending}
              loading={connectMutation.isPending}
            >
              {connectMutation.isPending ? 'Connecting...' : 'Connect Bank'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};