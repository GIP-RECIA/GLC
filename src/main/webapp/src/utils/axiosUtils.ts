import i18n from "@/plugins/i18n";
import axios from "axios";
import { useToast } from "vue-toastification";

const { t } = i18n.global;
const toast = useToast();

const { VITE_API_URL } = import.meta.env;

const instance = axios.create({
  baseURL: VITE_API_URL,
  withCredentials: true,
  xsrfCookieName: "CSRF-TOKEN",
  xsrfHeaderName: "X-CSRF-TOKEN",
});

const errorHandler = (e: any, toastOrI18n?: boolean | string): void => {
  const showToast: boolean = typeof toastOrI18n == "boolean" && toastOrI18n;

  if (axios.isAxiosError(e)) {
    if (typeof toastOrI18n == "string") toast.error(t(`toast.${toastOrI18n}`));
    else if (showToast) {
      if ([401, 404, 500].includes(e.response ? e.response.status : 0))
        toast.error(t(`toast.error.${e.response!.status}`));
      else toast.error(t("toast.error.unmanaged"));
    }
    console.error(e.message);
  } else if (e instanceof Error) {
    if (showToast) toast.error(t("toast.error.stock") + e.message);
    console.error(e.message);
  } else {
    if (showToast) toast.error(t("toast.error.unknown"));
    console.error(e);
  }
};

export { instance, errorHandler };
