import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { Budget, BudgetPeriod } from '../../types';
import { useCategories } from '../../hooks/useCategories';
import { useBudgets, useCreateBudget, useUpdateBudget } from '../../hooks/useBudgets';

const budgetSchema = yup.object({
  categoryId: yup
    .number()
    .required('Category is required'),
  amount: yup
    .number()
    .positive('Amount must be positive')
    .required('Amount is required'),
  period: yup
    .string()
    .oneOf(['MONTHLY', 'YEARLY'], 'Period must be either monthly or yearly')
    .required('Period is required')
});

type BudgetFormData = yup.InferType<typeof budgetSchema>;

interface BudgetFormProps {
  onSuccess: () => void;
  onCancel: () => void;
  editingBudget?: Budget | null;
}

export const BudgetForm: React.FC<BudgetFormProps> = ({
  onSuccess,
  onCancel,
  editingBudget
}) => {
  const { data: categories = [], isLoading: loadingCategories } = useCategories();
  const { data: existingBudgets = [] } = useBudgets();
  const createMutation = useCreateBudget();
  const updateMutation = useUpdateBudget();

  // Find category ID from name if editing (backend returns categoryName, not ID)
  const getInitialCategoryId = () => {
    if (!editingBudget) return undefined;
    const category = categories.find(c => c.name === editingBudget.categoryName);
    return category?.id;
  };

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError
  } = useForm<BudgetFormData>({
    resolver: yupResolver(budgetSchema),
    defaultValues: {
      categoryId: getInitialCategoryId(),
      amount: editingBudget?.amount || undefined,
      period: editingBudget?.period || 'MONTHLY'
    }
  });

  // Filter out income categories for budgets (budgets are typically for expenses)
  const expenseCategories = categories.filter(category => 
    !['Salary', 'Investment', 'Other Income'].includes(category.name)
  );

  const onSubmit = async (data: BudgetFormData) => {
    // Check for duplicate budget (same category and period)
    const duplicateBudget = existingBudgets.find(budget => {
      const category = categories.find(c => c.id === data.categoryId);
      return category && 
        budget.categoryName === category.name && 
        budget.period === data.period &&
        budget.id !== editingBudget?.id;
    });
    
    if (duplicateBudget) {
      setError('categoryId', {
        type: 'manual',
        message: `A ${data.period.toLowerCase()} budget already exists for this category`
      });
      return;
    }

    try {
      const requestData = {
        category: { id: data.categoryId },
        amount: data.amount,
        period: data.period as BudgetPeriod
      };

      if (editingBudget) {
        await updateMutation.mutateAsync({
          id: editingBudget.id,
          data: requestData
        });
      } else {
        await createMutation.mutateAsync(requestData);
      }
      onSuccess();
    } catch (error) {
      console.error('Error saving budget:', error);
    }
  };

  if (loadingCategories) {
    return (
      <div className="flex items-center justify-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-2 border-gray-300 border-t-blue-600" />
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <div>
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
          Category
        </label>
        <select
          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:placeholder-gray-400 dark:focus:ring-blue-400 dark:focus:border-blue-400"
          {...register('categoryId', { valueAsNumber: true })}
        >
          <option value="">Select a category</option>
          {expenseCategories.map((category) => (
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

      <Input
        label="Budget Amount"
        type="number"
        step="0.01"
        min="0"
        placeholder="0.00"
        error={errors.amount?.message}
        helper="Set your spending limit for this category"
        {...register('amount', { valueAsNumber: true })}
      />

      <div>
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
          Budget Period
        </label>
        <select
          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:placeholder-gray-400 dark:focus:ring-blue-400 dark:focus:border-blue-400"
          {...register('period')}
        >
          <option value="MONTHLY">Monthly</option>
          <option value="YEARLY">Yearly</option>
        </select>
        {errors.period && (
          <p className="mt-1 text-sm text-red-600 dark:text-red-400">
            {errors.period.message}
          </p>
        )}
        <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
          Choose how often this budget resets
        </p>
      </div>

      <div className="flex justify-end space-x-3 pt-4">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>
        <Button 
          type="submit" 
          loading={createMutation.isPending || updateMutation.isPending}
        >
          {editingBudget ? 'Update Budget' : 'Create Budget'}
        </Button>
      </div>
    </form>
  );
};