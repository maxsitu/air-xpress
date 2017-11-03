import {http} from '../../core/constants';
import HttpBaseService from '../base/base.service';

class ApiService extends HttpBaseService {
  httpJson(url, method, body, headers) {
    headers = Object.assign({}, headers, {
      [http.headerKeys.CONTENT_TYPE]: http.contentTypes.APPLICATION_JSON
    });

    return this.httpSend(url, method, body, headers);
  }

  httpGetJson(url, body, headers) {
    return this.httpJson(url, 'GET', body, headers);
  }

  httpPostJson(url, body, headers) {
    return this.httpJson(url, 'POST', body, headers);
  }

  httpSendBySpec(apiSpec, body = undefined) {
    body = body && JSON.stringify(body);
    const url = apiSpec.path,
      method = apiSpec.method;

    return this.httpSend(url, method, body, {});
  }

  httpSendJsonBySpec(apiSpec, body = undefined) {
    body = body && JSON.stringify(body);
    const url = apiSpec.path,
      method = apiSpec.method;

    return this.httpJson(url, method, body).then(resp => {
      if (resp.ok) {
        return resp;
      }
      return resp.json().then(msg => {
        throw msg;
      });
    });
  }
}

export default ApiService;