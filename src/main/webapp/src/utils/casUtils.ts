const { VITE_API_URL } = import.meta.env;

const jsonp = (
  uri: string,
  callbackName: string,
  timeout: number
): Promise<object> => {
  let url = VITE_API_URL.endsWith("/")
    ? VITE_API_URL.slice(0, -1)
    : VITE_API_URL;
  url += uri;

  return new Promise((resolve, reject) => {
    callbackName =
      typeof callbackName === "string"
        ? callbackName
        : "jsonp_" +
          (Math.floor(Math.random() * 100000) * Date.now()).toString(16);
    timeout = typeof timeout === "number" ? timeout : 5000;

    let timeoutTimer: NodeJS.Timeout | null = null;
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
};

const getCookie = (name: string) => {
  if (!document.cookie) {
    return null;
  }

  const cookie = document.cookie
    .split(";")
    .map((c) => c.trim())
    .find((c) => c.startsWith(name));

  if (cookie === null || cookie === undefined) {
    return null;
  }

  return decodeURIComponent(cookie.split("=")[1]);
};

const setCookie = (name: string, value: string) => {
  const path = ";path=" + VITE_API_URL;
  if (!document.cookie) {
    document.cookie = name + "=" + encodeURIComponent(value) + path;
  } else {
    const cookie = document.cookie
      .split(";")
      .find((c) => c.trim().startsWith(name + "="));
    if (!cookie) {
      document.cookie = name + "=" + encodeURIComponent(value) + path;
    } else {
      const key = cookie.split("=")[0];
      document.cookie = key + "=" + encodeURIComponent(value) + path;
    }
  }
};

const getToken = () => getCookie("CSRF-TOKEN");

export { jsonp, getToken };
