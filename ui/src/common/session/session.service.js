import UserService from '../user/user.service';
import sessionSettings from './sessionSettings';
import Cookies from 'universal-cookie';


class SessionService extends UserService {
  static _instance = undefined;

  static getInstance() {
    SessionService._instance = SessionService._instance || new SessionService();
    return SessionService._instance;
  }

  constructor() {
    super();
    this._user = undefined;
    this._cookies = new Cookies();
  }

  _hasSessionData() {
    const data_cookie = sessionSettings.SESSION_DATA_COOKIE;
    return !!this._cookies.get(data_cookie);
  }

  _hasRefreshToken() {
    return !!this._cookies.get(sessionSettings.REFRESH_TOKEN_COOKIE);
  }

  _removeSessionData() {
    return this._cookies.remove(sessionSettings.SESSION_DATA_COOKIE);
  }

  _removeRefreshToken() {
    return this._cookies.remove(sessionSettings.REFRESH_TOKEN_COOKIE);
  }

  _removeLocalSessionData() {
    this._removeSessionData();
    this._removeRefreshToken();
  }

  get user() {
    return this._user;
  }

  set user(user) {
    this._user = user;
  }

  isLoggedIn() {
    return this._hasSessionData() && !!this.user;
  }

  isSessionRefreshable() {
    return this._hasRefreshToken();
  }

  refreshSession() {
    if (!this._hasSessionData()) {
      return Promise.resolve({});
    }

    return super.get().then((user) => {
      this.user = user;
    }).catch(e => console.log(e));
  }

  logout() {
    this._removeLocalSessionData();
    this._removeRefreshToken();
    this.user = null;
    return super.logout();
  }
}

export default SessionService;