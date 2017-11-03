import * as fetch from 'isomorphic-fetch';

class HttpBaseService {
  /**
   * Send GET http request
   * @param url     {string} url of destination endpoint
   * @param method  {string} request method, e.g. GET, POST, PUT, DELETE, PATCH
   * @param body    {object} request body object
   * @param headers {object} request header object
   * @returns {Promise} response sent back from remote
   */
  httpSend(url, method, body, headers) {
    const options = {
      method,
      body,
      headers,
      credentials: 'same-origin'
    };

    return fetch(url, options);
  }


}

export default HttpBaseService;