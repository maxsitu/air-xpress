export const SET_REDIRECT = 'SET_REDIRECT';
export const NAVIGATE_TO = 'NAVIGATE_TO';

export const setRedirectPath = (path) => {
  return {
    type: SET_REDIRECT,
    path
  }
};

export const navigateTo = (path) => {
  return {
    type: NAVIGATE_TO,
    path
  };
};

export const logIn = () => ({type: 'LOGIN'});
export const logOut = () => ({type: 'LOGOUT'});