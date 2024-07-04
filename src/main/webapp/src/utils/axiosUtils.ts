import i18n from '@/plugins/i18n.ts';
import { login } from '@/utils/casUtils.ts';
import axios from 'axios';
import { differenceInMilliseconds } from 'date-fns';
import { type ToastContainerOptions, toast } from 'vue3-toastify';

const { t } = i18n.global;

const { VITE_API_URI, VITE_AXIOS_TIMEOUT, VITE_REFRESH_IDENTITY_MILLISECONDS } = import.meta.env;

const instance = axios.create({
  baseURL: VITE_API_URI,
  timeout: VITE_AXIOS_TIMEOUT,
  withCredentials: true,
  xsrfCookieName: 'CSRF-TOKEN',
  xsrfHeaderName: 'X-CSRF-TOKEN',
});

const intercept = () => {
  let lastUpdated = new Date();

  instance.interceptors.request.use(async (config) => {
    if (differenceInMilliseconds(new Date(), lastUpdated) > VITE_REFRESH_IDENTITY_MILLISECONDS) {
      await login();
      lastUpdated = new Date();
    }

    return config;
  });
};

const errorHandler = (e: any, toastOrI18n?: boolean | string): void => {
  let showToast: boolean = typeof toastOrI18n == 'boolean' && toastOrI18n;
  const i18nHandled: Array<number> = [401, 404, 500];
  let message: string, error: any;

  if (axios.isAxiosError(e)) {
    if (typeof toastOrI18n == 'string' && toastOrI18n.trim().length > 0) {
      message = `toast.${toastOrI18n}`;
      showToast = true;
    } else {
      message = i18nHandled.includes(e.response?.status ?? -1)
        ? `toast.error.${e.response!.status}`
        : 'toast.error.unmanaged';
    }
    error = e.message;
  } else if (e instanceof Error) {
    message = 'toast.error.stock';
    error = e.message;
  } else {
    message = 'toast.error.unknown';
    error = e;
  }

  if (showToast) toast.error(t(message), { clearOnUrlChange: false } as ToastContainerOptions);
  console.error(error);
};

export { instance, errorHandler, intercept };
