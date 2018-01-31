import {
  ASYNC_START,
  LOCATION_EDITOR_PAGE_LOADED,
  LOCATION_EDITOR_PAGE_UNLOADED,
  LOCATION_SUBMITTED,
  UPDATE_FIELD_LOCATION_EDITOR
} from '../constants/actionTypes';

export default (state = {}, action) => {
  switch (action.type) {
    case LOCATION_EDITOR_PAGE_LOADED:
      return {
        ...state,
        locationId: action.payload ? action.payload.id : '',
        code: action.payload ? action.payload.code : '',
        name: action.payload ? action.payload.name : '',
        geoLat: action.payload ? action.payload.geoLat : 0,
        geoLon: action.payload ? action.payload.geoLon : 0
      };
    case LOCATION_SUBMITTED:
      return {
        ...state,
        inProgress: false
      };
    case LOCATION_EDITOR_PAGE_UNLOADED:
      return {};
    case UPDATE_FIELD_LOCATION_EDITOR:
      return { ...state, [action.key]: action.value };
    case ASYNC_START:
      if (action.subtype === LOCATION_SUBMITTED) {
        return { ...state, inProgress: true };
      }
      break;
    default:
      return state;
  }

  return state;
};