import type { CATEGORIES } from './contract.js';
export type Category = (typeof CATEGORIES)[number];
export type Compat = 'full' | 'partial' | 'none';
