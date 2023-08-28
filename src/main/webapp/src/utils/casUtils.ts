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
          // @ts-ignore
          new Error({
            statusText: "Request Timeout",
            status: 408,
          })
        );
      }, timeout);
    }

    // @ts-ignore
    const onError = (err) => {
      // eslint-disable-next-line
      console.error("Error JSONP", err);
      if (timeoutTimer) {
        clearTimeout(timeoutTimer);
      }
      removeErrorListener();
      reject(
        // @ts-ignore
        new Error({
          status: 400,
          statusText: "Bad Request",
        })
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

    const script = document.createElement("script");
    script.type = "text/javascript";

    const removeErrorListener = () => {
      script.removeEventListener("error", onError);
    };
    const removeScript = () => {
      document.body.removeChild(script);
      // @ts-ignore
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

export { jsonp };
