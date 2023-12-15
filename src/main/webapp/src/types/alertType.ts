export type Alert = {
  title?: string;
  text?: string;
  type: 'success' | 'info' | 'warning' | 'error';
  action: boolean;
};
