import {user} from '../../core/constants/api';
import ApiService from '../api/api.service';

function onSuccess(resp) {
  if (resp.ok) {
    return resp.json();
  } else {
    return Promise.reject(resp.text());
  }
}

function onError(error) {
  console.error(error);
  return Promise.reject(error);
}

class UserService extends ApiService {
  register(regInfo) {
    const info = {
      login: regInfo.login,
      email: regInfo.email,
      password: regInfo.password
    };

    return this.requestJsonByFeature(user.register, info)
      .then(resp => {
        if (resp.ok) {
          return resp;
        }
        throw resp.json();
      })
  }

  login(login) {
    return this.requestJsonByFeature(user.login, login)
      .then(onSuccess)
      .catch(onError);
  }

  get() {
    return this.requestJsonByFeature(user.info)
      .then(onSuccess)
      .catch(onError);
  }

  logout() {
    return this.requestByFeature(user.logout)
      .then(resp => {
        if (!resp.ok) {
          return Promise.reject(resp);
        }
      })
      .catch(onError);
  }
}

export default UserService;