import {
  ASYNC_START,
  PLANE_EDITOR_PAGE_LOADED,
  PLANE_EDITOR_PAGE_UNLOADED,
  PLANE_SUBMITTED, UPDATE_FIELD_PLANE_EDITOR
} from "../constants/actionTypes";

export default (state = {}, action) => {
  switch (action.type) {
    case PLANE_EDITOR_PAGE_LOADED:
      return {
        ...state,
        nNo: action.nNo || '',
        manufacturerName: action.manufacturerName || '',
        serialNo: action.serialNo || '',
        model: action.model || '',
        pilotSeats: action.pilotSeats || 0,
        minPilot: action.minPilot || 0,
        customerSeats: action.customerSeats || 0
      };
    case PLANE_EDITOR_PAGE_UNLOADED:
      return {};
    case UPDATE_FIELD_PLANE_EDITOR:
      return { ...state, [action.key]: action.value };
    case PLANE_SUBMITTED:
      return { ...state, inProgress: false};
    case ASYNC_START:
      if (action.subtype === PLANE_SUBMITTED) {
        return { ...state, inProgress: true};
      }
      break;
    default:
      return state;
  }

  return state;
}