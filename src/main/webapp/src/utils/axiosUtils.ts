import i18n from '@/plugins/i18n.ts';
import { login } from '@/utils/casUtils.ts';
import axios from 'axios';
import { differenceInMilliseconds } from 'date-fns';
import { type ToastContainerOptions, toast } from 'vue3-toastify';

const { t } = i18n.global;

const { VITE_API_URI, VITE_REFRESH_IDENTITY_MILLISECONDS } = import.meta.env;

const instance = axios.create({
  baseURL: VITE_API_URI,
  timeout: 10000,
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
  const showToast: boolean = typeof toastOrI18n == 'boolean' && toastOrI18n;
  const toastOptions: ToastContainerOptions = { clearOnUrlChange: false };

  if (axios.isAxiosError(e)) {
    if (typeof toastOrI18n == 'string') toast.error(t(`toast.${toastOrI18n}`), toastOptions);
    else if (showToast) {
      if ([401, 404, 500].includes(e.response ? e.response.status : 0))
        toast.error(t(`toast.error.${e.response!.status}`), toastOptions);
      else toast.error(t('toast.error.unmanaged'), toastOptions);
    }
    console.error(e.message);
  } else if (e instanceof Error) {
    if (showToast) toast.error(t('toast.error.stock') + e.message, toastOptions);
    console.error(e.message);
  } else {
    if (showToast) toast.error(t('toast.error.unknown'), toastOptions);
    console.error(e);
  }
};

export { instance, errorHandler, intercept };
