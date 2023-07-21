import FetchWrapper from "./FetchWrapper";
import { storeToRefs } from "pinia";

class PrincipalService {
  constructor(store) {
    this.store = store;
    this.fw = new FetchWrapper(store);
    this.refs = storeToRefs(store);
  }

  identify(force) {
    return new Promise((resolve, reject) => {
      let identity2 = this.refs.identity.value;
      if (force === true) {
        console.log("force");
        this.refs.identity.value = undefined;
        identity2 = undefined;
      }

      // check and see if we have retrieved the identity data from the server.
      // if we have, reuse it by immediately resolving
      if (identity2 !== undefined && identity2 !== null) {
        resolve(identity2);
      } else {
        // retrieve the identity data from the server, update the identity object, and then resolve.
        this.fw
          .getJson("api/account")
          .then((response) => {
            console.log(
              "setIdentity:",
              response.data,
              "setAuthenticated: true"
            );
            this.refs.identity.value = response.data;
            this.refs.authenticated.value = true;
            resolve(identity2);
          })
          .catch(() => {
            console.log("setIdentity:", null, "setAuthenticated: true");
            this.refs.identity.value = null;
            this.refs.authenticated.value = false;
            reject(identity2);
          });
      }
    });
  }

  authenticate(identity2) {
    console.log("authenticate", identity2);
    this.refs.identity.value = identity2;
    this.refs.authenticated.value =
      identity2 !== null && identity2 !== undefined;
  }

  isInAnyRole(roles) {
    const identity2 = this.refs.identity.value;
    const authenticated2 = this.refs.authenticated.value;
    if (
      !authenticated2 ||
      identity2 === undefined ||
      identity2 === null ||
      !identity2.roles
    ) {
      return false;
    }

    return roles.some((role) => this.isInRole(role));
  }

  isInRole(role) {
    const identity2 = this.refs.identity.value;
    const authenticated2 = this.refs.authenticated.value;
    if (
      !authenticated2 ||
      identity2 === undefined ||
      identity2 === null ||
      !identity2.roles
    ) {
      return false;
    }

    return this.refs.identity.roles.indexOf(role) !== -1;
  }

  isAuthenticated() {
    return this.refs.authenticated.value;
  }

  isIdentityResolved() {
    return typeof this.refs.identity.value !== "undefined";
  }
}

export default PrincipalService;
