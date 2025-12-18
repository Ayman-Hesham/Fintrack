import { CategoryColor } from '../types';

// Maps backend CategoryColor enum to hex codes
export const categoryColorMap: Record<CategoryColor, string> = {
    RED: '#FF6B6B',
    TEAL: '#4ECDC4',
    BLUE: '#45B7D1',
    GREEN: '#96CEB4',
    YELLOW: '#FECA57',
    PINK: '#FF9FF3',
    ROYAL_BLUE: '#54A0FF',
    PURPLE: '#5F27CD',
    CYAN: '#00D2D3',
    ORANGE: '#FF9F43',
    VIOLET: '#A55EEA',
    EMERALD: '#26DE81',
    ROSE: '#FD79A8',
    AMBER: '#FDCB6E',
    INDIGO: '#6C5CE7'
};

// Maps hex codes back to CategoryColor enum (for forms)
export const hexToColorEnum: Record<string, CategoryColor> = {
    '#FF6B6B': 'RED',
    '#4ECDC4': 'TEAL',
    '#45B7D1': 'BLUE',
    '#96CEB4': 'GREEN',
    '#FECA57': 'YELLOW',
    '#FF9FF3': 'PINK',
    '#54A0FF': 'ROYAL_BLUE',
    '#5F27CD': 'PURPLE',
    '#00D2D3': 'CYAN',
    '#FF9F43': 'ORANGE',
    '#A55EEA': 'VIOLET',
    '#26DE81': 'EMERALD',
    '#FD79A8': 'ROSE',
    '#FDCB6E': 'AMBER',
    '#6C5CE7': 'INDIGO'
};

export const getHexColor = (color: CategoryColor): string => {
    return categoryColorMap[color] || '#45B7D1';
};

export const getColorEnum = (hexCode: string): CategoryColor => {
    return hexToColorEnum[hexCode.toUpperCase()] || 'BLUE';
};

// Get all available color options for forms
export const colorOptions: { value: CategoryColor; hex: string }[] = Object.entries(categoryColorMap).map(
    ([value, hex]) => ({ value: value as CategoryColor, hex })
);
