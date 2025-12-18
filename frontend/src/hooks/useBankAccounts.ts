import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '../lib/apiClient';
import { BankAccount, ConnectBankRequest, JobRequest } from '../types';

export const useBankAccounts = () => {
    return useQuery({
        queryKey: ['bankAccounts'],
        queryFn: async (): Promise<BankAccount[]> => {
            const response = await apiClient.get<BankAccount[]>('/api/banks/');
            return response.data;
        },
    });
};

export const useConnectBank = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (data: ConnectBankRequest): Promise<BankAccount> => {
            const response = await apiClient.post<BankAccount>('/api/banks/', data);
            return response.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['bankAccounts'] });
        },
    });
};

// Helper to check job status
export const checkJobStatus = async (jobId: string) => {
    const response = await apiClient.get<JobRequest>(`/api/jobs/${jobId}`);
    return response.data;
};

export const useInitiateSyncBank = () => {
    return useMutation({
        mutationFn: async ({ bankId, idempotencyKey }: { bankId: number; idempotencyKey: string }): Promise<{ jobId: string }> => {
            const response = await apiClient.post<{ jobId: string }>('/api/jobs',
                { bankAccountId: bankId },
                { headers: { 'Idempotency-Key': idempotencyKey } }
            );
            return response.data;
        },
        onSuccess: () => {
            // We don't invalidate here yet because the job just started
        },
    });
};
