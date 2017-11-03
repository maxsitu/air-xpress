/**
 * Created by walle on 6/11/17.
 */
export const user = {
  login: {
    path: '/api/users',
    method: 'POST'
  },
  info: {
    path: '/api/users',
    method: 'GET'
  },
  register: {
    path: '/api/users/register',
    method: 'POST'
  },
  change_password: {
    path: '/api/users/changepassword',
    method: 'POST'
  },
  logout: {
    path: '/api/users/logout',
    method: 'GET'
  }
};