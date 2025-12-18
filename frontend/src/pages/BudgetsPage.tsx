import React, { useState } from 'react';
import { Plus, CreditCard as Edit, Trash2, Target } from 'lucide-react';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { Modal } from '../components/ui/Modal';
import { LoadingSpinner } from '../components/ui/LoadingSpinner';
import { BudgetForm } from '../components/forms/BudgetForm';
import { formatCurrency, formatPercentage } from '../utils/formatters';
import { Budget } from '../types';
import { useBudgets, useDeleteBudget } from '../hooks/useBudgets';

export const BudgetsPage: React.FC = () => {
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editingBudget, setEditingBudget] = useState<Budget | null>(null);

  const { data: budgets = [], isLoading } = useBudgets();
  const deleteMutation = useDeleteBudget();

  const getProgressColor = (spent: number, budget: number) => {
    const percentage = (spent / budget) * 100;
    if (percentage >= 100) return 'bg-red-500';
    if (percentage >= 80) return 'bg-yellow-500';
    return 'bg-green-500';
  };

  const getProgressBgColor = (spent: number, budget: number) => {
    const percentage = (spent / budget) * 100;
    if (percentage >= 100) return 'bg-red-100 dark:bg-red-900';
    if (percentage >= 80) return 'bg-yellow-100 dark:bg-yellow-900';
    return 'bg-green-100 dark:bg-green-900';
  };

  const handleBudgetSaved = () => {
    handleCloseModal();
  };

  const handleEditBudget = (budget: Budget) => {
    setEditingBudget(budget);
    setIsAddModalOpen(true);
  };

  const handleDeleteBudget = async (budgetId: number) => {
    if (!window.confirm('Are you sure you want to delete this budget?')) return;
    
    try {
      await deleteMutation.mutateAsync(budgetId);
    } catch (error) {
      console.error('Error deleting budget:', error);
    }
  };

  const handleCloseModal = () => {
    setIsAddModalOpen(false);
    setEditingBudget(null);
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
          Budgets
        </h1>
        <Button onClick={() => setIsAddModalOpen(true)} className="whitespace-nowrap">
          <Plus className="h-4 w-4 mr-2" />
          Create Budget
        </Button>
      </div>

      {/* Budget Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-blue-100 dark:bg-blue-900">
              <Target className="h-6 w-6 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600 dark:text-gray-400">
                Active Budgets
              </p>
              <p className="text-2xl font-bold text-gray-900 dark:text-white">
                {budgets.length}
              </p>
            </div>
          </div>
        </Card>
        
        <Card>
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-green-100 dark:bg-green-900">
              <Target className="h-6 w-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600 dark:text-gray-400">
                Total Budget
              </p>
              <p className="text-2xl font-bold text-gray-900 dark:text-white">
                {formatCurrency(budgets.reduce((sum, budget) => sum + budget.amount, 0))}
              </p>
            </div>
          </div>
        </Card>
        
        <Card>
          <div className="flex items-center">
            <div className="p-3 rounded-full bg-orange-100 dark:bg-orange-900">
              <Target className="h-6 w-6 text-orange-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600 dark:text-gray-400">
                Total Spent
              </p>
              <p className="text-2xl font-bold text-gray-900 dark:text-white">
                {formatCurrency(budgets.reduce((sum, budget) => sum + budget.spentAmount, 0))}
              </p>
            </div>
          </div>
        </Card>
      </div>

      {/* Budget List */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {budgets.map((budget) => {
          const percentage = (budget.spentAmount / budget.amount) * 100;
          const remaining = budget.amount - budget.spentAmount;
          
          return (
            <Card key={budget.id}>
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center">
                  <span className="text-2xl mr-3">
                    {budget.categoryIcon}
                  </span>
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white">
                      {budget.categoryName}
                    </h3>
                    <p className="text-sm text-gray-500 dark:text-gray-400 capitalize">
                      {budget.period.toLowerCase()}
                    </p>
                  </div>
                </div>
                <div className="flex space-x-1">
                  <Button 
                    variant="ghost" 
                    size="sm"
                    onClick={() => handleEditBudget(budget)}
                  >
                    <Edit className="h-4 w-4" />
                  </Button>
                  <Button 
                    variant="ghost" 
                    size="sm"
                    onClick={() => handleDeleteBudget(budget.id)}
                    disabled={deleteMutation.isPending}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              </div>
              
              <div className="space-y-3">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600 dark:text-gray-400">Spent</span>
                  <span className="font-medium text-gray-900 dark:text-white">
                    {formatCurrency(budget.spentAmount)} of {formatCurrency(budget.amount)}
                  </span>
                </div>
                
                <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                  <div
                    className={`h-2 rounded-full transition-all duration-300 ${getProgressColor(budget.spentAmount, budget.amount)}`}
                    style={{ width: `${Math.min(percentage, 100)}%` }}
                  />
                </div>
                
                <div className="flex justify-between items-center">
                  <div className={`text-xs px-2 py-1 rounded-full ${getProgressBgColor(budget.spentAmount, budget.amount)}`}>
                    {formatPercentage(budget.spentAmount, budget.amount)} used
                  </div>
                  <span className={`text-sm font-medium ${
                    remaining >= 0 
                      ? 'text-green-600 dark:text-green-400' 
                      : 'text-red-600 dark:text-red-400'
                  }`}>
                    {remaining >= 0 ? 'Remaining: ' : 'Over by: '}
                    {formatCurrency(Math.abs(remaining))}
                  </span>
                </div>
              </div>
            </Card>
          );
        })}
      </div>

      {budgets.length === 0 && (
        <Card>
          <div className="text-center py-12">
            <Target className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
              No budgets created yet
            </h3>
            <p className="text-gray-500 dark:text-gray-400 mb-4">
              Create your first budget to start tracking your spending goals.
            </p>
            <Button onClick={() => setIsAddModalOpen(true)} className="whitespace-nowrap">
              <Plus className="h-4 w-4 mr-2" />
              Create Your First Budget
            </Button>
          </div>
        </Card>
      )}

      {/* Add/Edit Budget Modal */}
      <Modal
        isOpen={isAddModalOpen}
        onClose={handleCloseModal}
        title={editingBudget ? 'Edit Budget' : 'Create New Budget'}
        size="md"
      >
        <BudgetForm
          onSuccess={handleBudgetSaved}
          onCancel={handleCloseModal}
          editingBudget={editingBudget}
        />
      </Modal>
    </div>
  );
};