import {
  LOCATION_LIST_PAGE_LOADED,
  LOCATION_LIST_PAGE_UNLOADED
} from '../constants/actionTypes';

export default (state = {}, action) => {
  switch (action.type) {
    case LOCATION_LIST_PAGE_LOADED:
      return {
        ...state,
        locations: action.payload || []
      };
    case LOCATION_LIST_PAGE_UNLOADED:
      return {};
    default:
      return {
        ...state
      };
  }

  return state;
};