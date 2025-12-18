import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { TransactionType } from '../../types';
import { useCategories } from '../../hooks/useCategories';
import { useBankAccounts } from '../../hooks/useBankAccounts';
import { useCreateTransaction } from '../../hooks/useTransactions';

interface TransactionFormData {
  date: string;
  transactionType: 'INCOME' | 'EXPENSE';
  amount: number;
  categoryId: number;
  bankAccountId: number;
  description?: string;
}

const transactionSchema = yup.object<TransactionFormData>({
  date: yup.string().required('Date is required'),
  transactionType: yup.string<'INCOME' | 'EXPENSE'>().oneOf(['INCOME', 'EXPENSE']).required('Type is required'),
  amount: yup.number().positive('Amount must be positive').required('Amount is required'),
  categoryId: yup.number().required('Category is required'),
  bankAccountId: yup.number().required('Account is required'),
  description: yup.string().optional()
});

interface TransactionFormProps {
  onSuccess: () => void;
  onCancel: () => void;
}

export const TransactionForm: React.FC<TransactionFormProps> = ({
  onSuccess,
  onCancel
}) => {
  const { data: categories = [], isLoading: loadingCategories } = useCategories();
  const { data: accounts = [], isLoading: loadingAccounts } = useBankAccounts();
  const createMutation = useCreateTransaction();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors }
  } = useForm<TransactionFormData>({
    resolver: yupResolver(transactionSchema) as never,
    defaultValues: {
      date: new Date().toISOString().split('T')[0],
      transactionType: 'EXPENSE',
      amount: undefined,
      categoryId: undefined,
      bankAccountId: undefined,
      description: ''
    }
  });

  const selectedType = watch('transactionType');

  const onSubmit = async (data: TransactionFormData) => {
    try {
      await createMutation.mutateAsync({
        date: data.date,
        transactionType: data.transactionType as TransactionType,
        amount: data.amount,
        categoryId: data.categoryId,
        bankAccountId: data.bankAccountId,
        description: data.description || 'Transaction'
      });
      onSuccess();
    } catch (error) {
      console.error('Error creating transaction:', error);
    }
  };

  // Filter categories based on transaction type
  const filteredCategories = categories.filter(category => {
    if (selectedType === 'INCOME') {
      return ['Salary', 'Investment', 'Other Income'].includes(category.name);
    } else {
      return !['Salary', 'Investment', 'Other Income'].includes(category.name);
    }
  });

  if (loadingCategories || loadingAccounts) {
    return (
      <div className="flex items-center justify-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-2 border-gray-300 border-t-blue-600" />
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          label="Date"
          type="date"
          error={errors.date?.message}
          {...register('date')}
        />

        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Type
          </label>
          <select
            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:placeholder-gray-400 dark:focus:ring-blue-400 dark:focus:border-blue-400"
            {...register('transactionType')}
          >
            <option value="EXPENSE">Expense</option>
            <option value="INCOME">Income</option>
          </select>
          {errors.transactionType && (
            <p className="mt-1 text-sm text-red-600 dark:text-red-400">
              {errors.transactionType.message}
            </p>
          )}
        </div>
      </div>

      <Input
        label="Amount"
        type="number"
        step="0.01"
        min="0"
        placeholder="0.00"
        error={errors.amount?.message}
        {...register('amount', { valueAsNumber: true })}
      />

      <div>
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
          Category
        </label>
        <select
          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:placeholder-gray-400 dark:focus:ring-blue-400 dark:focus:border-blue-400"
          {...register('categoryId', { valueAsNumber: true })}
        >
          <option value="">Select a category</option>
          {filteredCategories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.icon} {category.name}
            </option>
          ))}
        </select>
        {errors.categoryId && (
          <p className="mt-1 text-sm text-red-600 dark:text-red-400">
            {errors.categoryId.message}
          </p>
        )}
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
          Account
        </label>
        <select
          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:placeholder-gray-400 dark:focus:ring-blue-400 dark:focus:border-blue-400"
          {...register('bankAccountId', { valueAsNumber: true })}
        >
          <option value="">Select an account</option>
          {accounts.map((account) => (
            <option key={account.id} value={account.id}>
              {account.nickName} - {account.bankName}
            </option>
          ))}
        </select>
        {errors.bankAccountId && (
          <p className="mt-1 text-sm text-red-600 dark:text-red-400">
            {errors.bankAccountId.message}
          </p>
        )}
      </div>

      <Input
        label="Description"
        placeholder="Transaction description (optional)"
        helper="Leave empty to default to 'Transaction'"
        error={errors.description?.message}
        {...register('description')}
      />

      <div className="flex justify-end space-x-3 pt-4">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" loading={createMutation.isPending}>
          Add Transaction
        </Button>
      </div>
    </form>
  );
};