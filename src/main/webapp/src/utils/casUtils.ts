import { useConfigurationStore } from '@/stores/configurationStore';
import { storeToRefs } from 'pinia';

const { VITE_API_URL } = import.meta.env;

const jsonp = (uri: string, callbackName: string, timeout: number): Promise<object> => {
  let url = VITE_API_URL.endsWith('/') ? VITE_API_URL.slice(0, -1) : VITE_API_URL;
  url += uri;

  return new Promise((resolve, reject) => {
    callbackName =
      typeof callbackName === 'string'
        ? callbackName
        : 'jsonp_' + (Math.floor(Math.random() * 100000) * Date.now()).toString(16);
    timeout = typeof timeout === 'number' ? timeout : 5000;

    let timeoutTimer: NodeJS.Timeout | null = null;
    if (timeout > -1) {
      timeoutTimer = setTimeout(() => {
        // eslint-disable-next-line
        console.warn('Timeout JSONP');
        removeErrorListener();
        removeScript();
        reject(
          // @ts-ignore
          new Error({
            statusText: 'Request Timeout',
            status: 408,
          }),
        );
      }, timeout);
    }

    // @ts-ignore
    const onError = (err) => {
      // eslint-disable-next-line
      console.error('Error JSONP', err);
      if (timeoutTimer) {
        clearTimeout(timeoutTimer);
      }
      removeErrorListener();
      removeScript();
      reject(
        // @ts-ignore
        new Error({
          status: 400,
          statusText: 'Bad Request',
        }),
      );
    };

    // @ts-ignore
    window[callbackName] = (json) => {
      if (timeoutTimer) {
        clearTimeout(timeoutTimer);
      }
      removeErrorListener();
      removeScript();
      resolve(json);
    };

    const script = document.createElement('script');
    script.type = 'text/javascript';

    const removeErrorListener = () => {
      script.removeEventListener('error', onError);
    };
    const removeScript = () => {
      document.body.removeChild(script);
      // @ts-ignore
      delete window[callbackName];
    };

    // Add error listener.
    script.addEventListener('error', onError);

    // Append to head element.
    script.src = url + (/\?/.test(url) ? '&' : '?') + 'callback=' + callbackName;
    script.async = true;
    document.body.appendChild(script);
  });
};

// Objet en charge de la redirection vers le serveur CAS
// eslint-disable-next-line
let relogState = {};

// Méthode en charge du processus de connexion
// Une fois connecté, l'utilisateur est redirigé
const login = async () => {
  const configurationStore = useConfigurationStore();
  const { identity } = storeToRefs(configurationStore);

  try {
    const response = await jsonp('/app/login', 'JSON_CALLBACK', 1000);
    // @ts-ignore
    identity.value = response;
  } catch (e) {
    identity.value = undefined;
    relog();
  }
};

// Méthode effectuant une redirection sur le serveur CAS,
// un listener est mis en place afin de détecter la réponse
// du serveur CAS
const relog = () => {
  windowOpenCleanup(relogState);
  // @ts-ignore
  relogState.listener = onmessage;
  window.addEventListener('message', onmessage);

  // @ts-ignore
  relogState.window = window.open(`${VITE_API_URL}/app/login?postMessage`);
};

// Méthode de nettoyage de la page de login
// @ts-ignore
const windowOpenCleanup = (state) => {
  try {
    if (state.listener) window.removeEventListener('message', state.listener);
    if (state.window) state.window.close();
  } catch (e) {
    // eslint-disable-next-line
    console.error(e);
  }
};

// Méthode utilisé lors de la réception de la réponse
// du serveur CAS puis redirige l'utilisateur
// @ts-ignore
const onmessage = (e) => {
  if (typeof e.data !== 'string') return;

  const m = e.data.match(/^loggedUser=(.*)$/);
  if (!m) return;

  windowOpenCleanup(relogState);
  login();
};

export { login };
