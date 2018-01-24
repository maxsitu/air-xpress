import fetch from 'isomorphic-fetch';

const responseText = res => res.ok ? res.text() : Promise.reject(res.text());
const responseJson = res => res.ok ? res.json() : Promise.reject(res.text());
const addJsonHeader = options => Object.assign({}, options, {
  headers: {
  'Content-Type': 'application/json; charset=utf-8'
  }
});
const API_ROOT = '';

let token = null;

class SameOriginFetch {
  constructor() {
    const doFetch = async function(url, options) {
      return await fetch(url, {
        credentials: 'same-origin',
        ...options
      });
    };

    const doFetchWithMethod = async function (method, url, options, body) {
      return await doFetch(url, Object.assign({},
        options,
        {
          method,
          body
        }));
    };

    this.doFetch = doFetch;
    this.doFetchWithMethod = doFetchWithMethod;
  }

  async del(url, options) {
    return await this.doFetchWithMethod('DELETE', `${API_ROOT}${url}`, options);
  }

  async get(url, options) {
    return await this.doFetchWithMethod('GET', `${API_ROOT}${url}`, options);
  }

  async put(url, options, body) {
    body = body && JSON.stringify(body);
    return await this.doFetchWithMethod('PUT', `${API_ROOT}${url}`, addJsonHeader(options), body);
  }

  async post(url, options, body) {
    body = body && JSON.stringify(body);
    return await this.doFetchWithMethod('POST', `${API_ROOT}${url}`, addJsonHeader(options), body);
  }
}

class AuthFetch extends SameOriginFetch {
  async current() {
    return await this.get('/users').then(responseJson);
  }

  async login(login, password, rememberMe) {
    rememberMe = !!rememberMe;
    return await this.post('/users', null, {
      login,
      password,
      rememberMe
    }).then(responseJson);
  }

  async register(login, email, password) {
    return await this.post('/users/register', null, {
      login,
      email,
      password
    }).then(responseJson);
  }

  async changePassword() {
    return await this.post('/users/changepassword');
  }

  async logout() {
    return await this.get('/users/logout').then(responseText);
  }
}

class LocationFetch extends SameOriginFetch {
  async all() {
    return await this.get('/location').then(responseJson);
  }

  async findById(id) {
    return await this.get(`/location/id/${id}`).then(responseJson);
  }

  async findByCode(code) {
    return await this.get(`/location/code/${code}`).then(responseJson);
  }

  async create(code, name, geoLat, geoLon) {
    return await this.post(`/location`, null, {
      code,
      name,
      geoLat,
      geoLon
    }).then(responseText);
  }

  async update(id, code, name, geoLat, geoLon) {
    return await this.put(`/location/id/${id}`, null, {
      code,
      name,
      geoLat,
      geoLon
    }).then(responseText);
  }
}

const Auth = new AuthFetch();
const Location = new LocationFetch();

export default {
  Auth,
  Location,
  setToken: _token => { token = _token; }
};