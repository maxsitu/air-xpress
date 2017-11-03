import {reducer as reduxFormReducer} from 'redux-form';
import {SET_REDIRECT, NAVIGATE_TO} from '../Action';

export const form = reduxFormReducer;

export const isLoggedIn = (state = false, action) => {
  switch (action.type) {
    case 'LOGIN':
      return true;
    case 'LOGOUT':
      return false;
    default:
      return state;
  }
};

export const redirectPath = (state = null, action) => {
  switch (action.type) {
    case SET_REDIRECT:
      return action.path;
    default:
      return state;
  }
};

export const navigatePath = (state = null, action) => {
  switch (action.type) {
    case NAVIGATE_TO:
      return action.path;
    default:
      return state;
  }
};