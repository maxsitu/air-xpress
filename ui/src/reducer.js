import { combineReducers } from 'redux';
import { routerReducer } from 'react-router-redux';

// import article from './reducers/article';
// import articleList from './reducers/articleList';
import auth from './reducers/auth';
import common from './reducers/common';
// import editor from './reducers/editor';
import home from './reducers/home';
// import profile from './reducers/profile';
import settings from './reducers/settings';
import locationList from './reducers/locationList';
import locationEditor from './reducers/locationEditor';
import planeEditor from './reducers/planeEditor';
import flightPlanEditor from './reducers/flighPlanEditor';

export default combineReducers({
  // article,
  // articleList,
  auth,
  common,
  // editor,
  home,
  // profile,
  settings,
  locationList,
  locationEditor,
  planeEditor,
  flightPlanEditor,
  router: routerReducer
});
