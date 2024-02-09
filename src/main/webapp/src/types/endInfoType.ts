import type { enumValues } from '@/types/enumValuesType.ts';

export type endInfo = {
  date?: string;
  months?: number;
  isPast: boolean;
} & enumValues;
