import FetchWrapper from "./FetchWrapper";
import PrincipalService from "./PrincipalService";
import { storeToRefs } from "pinia";

class AuthenticationService {
  constructor(store) {
    this.refs = storeToRefs(store);
    this.fw = new FetchWrapper(store);
    this.ps = new PrincipalService(store);
  }

  login() {
    return new Promise((resolve, reject) => {
      this.fw
        .getJsonP("app/login")
        .then(() => {
          this.ps
            .identify(true)
            .then((account) => {
              if (
                account !== undefined &&
                account.user.langKey !== undefined &&
                account.user.langKey !== null
              ) {
                console.log("setLang");
                this.refs.lang.value = account.user.langKey;
              }
              resolve(account);
            })
            .catch((err) => {
              reject(err);
            });
        })
        .catch((err) => {
          this.logout();
          this.ps.authenticate(null);
          reject(err);
        });
    });
  }

  logout() {
    return new Promise((resolve, reject) => {
      this.fw
        .postJson("api/logout")
        .then((response) => {
          console.log("clearAll");
          this.store.clearAll();
          resolve(response);
        })
        .catch((err) => {
          reject(err);
        });
    });
  }

  authorize() {
    return this.ps.identify().then(() => {
      const isAuthenticated = new PrincipalService(
        this.store
      ).isAuthenticated();
      if (
        this.refs.nextRoute.value.meta.roles &&
        this.refs.nextRoute.value.meta.roles.length > 0 &&
        !this.ps.isInAnyRole(this.refs.nextRoute.value.meta.roles)
      ) {
        if (isAuthenticated) {
          // user is signed in but not authorized for desired state
          console.log("AccessDenied");
        } else {
          // user is not authenticated. stow the state they wanted before you
          // send them to the signin state, so you can return them when you're done
          console.log("setReturnRoute", this.refs.nextRoute.value);

          // now, send them to the signin state so they can log in
          console.log("Login");
        }
      }
    });
  }
}

export default AuthenticationService;
