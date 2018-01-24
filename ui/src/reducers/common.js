import {
  APP_LOAD,
  REDIRECT,
  LOGOUT,
  // ARTICLE_SUBMITTED,
  SETTINGS_SAVED,
  LOGIN,
  REGISTER,
  // DELETE_ARTICLE,
  // ARTICLE_PAGE_UNLOADED,
  // EDITOR_PAGE_UNLOADED,
  HOME_PAGE_UNLOADED,
  PROFILE_PAGE_UNLOADED,
  // PROFILE_FAVORITES_PAGE_UNLOADED,
  // SETTINGS_PAGE_UNLOADED,
  LOGIN_PAGE_UNLOADED,
  REGISTER_PAGE_UNLOADED,
  LOCATION_EDITOR_PAGE_LOADED, LOCATION_SUBMITTED, LOCATION_PREVIEW_CLICKED
} from '../constants/actionTypes';
import Cookies from 'universal-cookie';

const defaultState = {
  appName: 'Air-Xpress',
  token: null,
  viewChangeCounter: 0
};

const _cookies = new Cookies();

export default (state = defaultState, action) => {
  switch (action.type) {
    case APP_LOAD:
      return {
        ...state,
        token: action.token || null,
        appLoaded: true,
        currentUser: action.payload ? action.payload.login : null
      };
    case REDIRECT:
      return { ...state, redirectTo: null };
    case LOGOUT:
      return { ...state, redirectTo: '/', token: null, currentUser: null };
    // case ARTICLE_SUBMITTED:
    //   const redirectUrl = `/article/${action.payload.article.slug}`;
    //   return { ...state, redirectTo: redirectUrl };
    case LOCATION_SUBMITTED:
      return { ...state, redirectTo: `/`};
    case LOCATION_PREVIEW_CLICKED:
      return { ...state, redirectTo: `/locationEditor/${action.payload}`};
    case SETTINGS_SAVED:
      return {
        ...state,
        redirectTo: action.error ? null : '/',
        currentUser: action.error ? null : action.payload.login
      };
    case LOGIN:
    case REGISTER:
      return {
        ...state,
        redirectTo: action.error ? null : '/',
        token: action.error ? null : _cookies.get('_jt_ui_sessiondata'),
        currentUser: action.error ? null : action.payload.login
      };
    // case DELETE_ARTICLE:
    //   return { ...state, redirectTo: '/' };
    // case ARTICLE_PAGE_UNLOADED:
    // case EDITOR_PAGE_UNLOADED:
    case LOCATION_EDITOR_PAGE_LOADED:
    case PROFILE_PAGE_UNLOADED:
    // case PROFILE_FAVORITES_PAGE_UNLOADED:
    // case SETTINGS_PAGE_UNLOADED:
    case LOGIN_PAGE_UNLOADED:
    case REGISTER_PAGE_UNLOADED:
      return { ...state, viewChangeCounter: state.viewChangeCounter + 1 };
    default:
      return state;
  }
};
