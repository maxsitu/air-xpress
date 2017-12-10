import {http} from '../../core/constants';
import HttpBaseService from '../base/base.service';

class ApiService extends HttpBaseService {
  requestJson(url, method, body, headers) {
    headers = Object.assign({}, headers, {
      [http.headerKeys.CONTENT_TYPE]: http.contentTypes.APPLICATION_JSON
    });

    return this.request(url, method, body, headers);
  }

  getJson(url, body, headers) {
    return this.requestJson(url, 'GET', body, headers);
  }

  postJson(url, body, headers) {
    return this.requestJson(url, 'POST', body, headers);
  }

  requestByFeature(apiSpec, body = undefined, extraAttrs = {}) {
    body = body && JSON.stringify(body);
    const url = apiSpec.path,
      method = apiSpec.method;

    const attrs = Object.assign({}, extraAttrs);

    return this.request(url, method, body, attrs);
  }

  requestJsonByFeature(apiSpec, body = undefined) {
    body = body && JSON.stringify(body);
    const url = apiSpec.path,
      method = apiSpec.method;

    return this.requestJson(url, method, body).then(resp => {
      if (resp.ok) {
        return resp;
      }

      return resp.text().then(msg => {
        throw msg;
      });
    });
  }
}

export default ApiService;