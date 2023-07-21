import CookieUtils from "./CookieUtils";
import { storeToRefs } from "pinia";
import { ref } from "vue";

const { VITE_API_URL } = import.meta.env;

class FetchWrapper {
  constructor(store) {
    this.refs = storeToRefs(store);
  }

  countPendingRequests() {
    return countPendingRequests.value;
  }

  getJson(url) {
    return new Promise((resolve, reject) => {
      fetchWithRetry(resolve, reject, VITE_API_URL + url, {}, this.refs);
    });
  }

  getJsonP(url) {
    return jsonp(VITE_API_URL + url, "JSON_CALLBACK", 1000);
  }

  postJson(url, data) {
    return new Promise((resolve, reject) => {
      fetchWithRetry(
        resolve,
        reject,
        VITE_API_URL + url,
        {
          method: "POST",
          body: JSON.stringify(data),
          headers: getHeader(),
        },
        this.refs
      );
    });
  }

  putJson(url, data) {
    return new Promise((resolve, reject) => {
      fetchWithRetry(
        resolve,
        reject,
        VITE_API_URL + url,
        {
          method: "PUT",
          body: JSON.stringify(data),
          headers: getHeader(),
        },
        this.refs
      );
    });
  }

  deleteJson(url) {
    return new Promise((resolve, reject) => {
      fetchWithRetry(
        resolve,
        reject,
        VITE_API_URL + url,
        {
          method: "DELETE",
          headers: getHeader(),
        },
        this.refs
      );
    });
  }
}

// Compteur des requêtes en cours
const countPendingRequests = ref(0);

// Fonction permettant de faire une requête JSON et de la réessayer après login si erreur 401
function fetchWithRetry(resolve, reject, url, params, refs) {
  countPendingRequests.value++;
  fetch(url, params)
    .then((response) => {
      if (!response.ok) {
        if (
          response.status !== 401 ||
          url === VITE_API_URL + "api/account" ||
          refs.loginModalOpened.value
        ) {
          reject(response);
        } else {
          // Si erreur 401, redirection vers la page de login
          console.error("401");
          // if (route.name !== "Login") {
          //   refs.loginModalOpened.value = true;
          //   refs.returnRoute.value = {
          //     name: route.name,
          //     params: route.params,
          //     meta: route.meta,
          //   };
          //   console.log("GO TO LOGIN");
          // }
        }
      } else {
        response
          .text()
          .then((text) => {
            resolve({
              data: text ? JSON.parse(text) : null,
              headers: response.headers,
            });
          })
          .catch((error) => {
            reject(error);
          });
      }
    })
    .catch((error) => {
      reject(error);
    })
    .finally(() => {
      countPendingRequests.value--;
    });
}

// Fonction retournant le header des requêtes
function getHeader() {
  return new Headers({
    "Content-Type": "application/json;charset=UTF-8",
    "X-CSRF-TOKEN": CookieUtils.getCookie("CSRF-TOKEN"),
  });
}

// Fonction permettant d'exécutant une requête de type JSONP
function jsonp(url, callbackName, timeout) {
  return new Promise((resolve, reject) => {
    callbackName =
      typeof callbackName === "string"
        ? callbackName
        : "jsonp_" +
          (Math.floor(Math.random() * 100000) * Date.now()).toString(16);
    timeout = typeof timeout === "number" ? timeout : 5000;

    let timeoutTimer = null;
    if (timeout > -1) {
      timeoutTimer = setTimeout(() => {
        // eslint-disable-next-line
        console.warn("Timeout JSONP");
        removeErrorListener();
        removeScript();
        reject(
          new Error({
            statusText: "Request Timeout",
            status: 408,
          })
        );
      }, timeout);
    }

    const onError = (err) => {
      // eslint-disable-next-line
      console.error("Error JSONP", err);
      if (timeoutTimer) {
        clearTimeout(timeoutTimer);
      }
      removeErrorListener();
      reject(
        new Error({
          status: 400,
          statusText: "Bad Request",
        })
      );
    };

    window[callbackName] = (json) => {
      if (timeoutTimer) {
        clearTimeout(timeoutTimer);
      }
      removeErrorListener();
      removeScript();
      resolve(json);
    };

    const script = document.createElement("script");
    script.type = "text/javascript";

    const removeErrorListener = () => {
      script.removeEventListener("error", onError);
    };
    const removeScript = () => {
      document.body.removeChild(script);
      delete window[callbackName];
    };

    // Add error listener.
    script.addEventListener("error", onError);

    // Append to head element.
    script.src =
      url + (/\?/.test(url) ? "&" : "?") + "callback=" + callbackName;
    script.async = true;
    document.body.appendChild(script);
  });
}

export default FetchWrapper;
