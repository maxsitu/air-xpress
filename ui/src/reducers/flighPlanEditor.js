import {
  UPDATE_FIELD_FLIGHT_PLAN,
  FLIGHT_PLAN_PAGE_LOADED,
  FLIGHT_PLAN_PAGE_UNLOADED,
  FLIGHT_PLAN_PAGE_ADD_FLIGHT_STEP,
  FLIGHT_PLAN_PAGE_REMOVE_FLIGHT_STEP,
  FLIGHT_PLAN_PAGE_VALIDATE_ERROR
} from "../constants/actionTypes";

export default (state = {}, action) => {
  switch (action.type) {
    case FLIGHT_PLAN_PAGE_LOADED:
      return {
        ...state,
        flightSteps: [...Array(2)].map(a => ({}))
      };
    case FLIGHT_PLAN_PAGE_UNLOADED:
      return {};
    case FLIGHT_PLAN_PAGE_ADD_FLIGHT_STEP: {
      const flightSteps = [ ...state.flightSteps ];
      flightSteps.push({});
      return {
        ...state,
        flightSteps
      };
    }
    case FLIGHT_PLAN_PAGE_REMOVE_FLIGHT_STEP: {
      const flightSteps = [ ...state.flightSteps ];
      flightSteps.pop();
      return {
        ...state,
        flightSteps
      };
    }
    case UPDATE_FIELD_FLIGHT_PLAN: {
      const flightSteps = [ ...state.flightSteps ];
      flightSteps[action.idx] = flightSteps[action.idx] || {};
      flightSteps[action.idx][action.key] = action.value;
      return { ...state, flightSteps, lastUpdatedIndex: action.idx };
    }
    case FLIGHT_PLAN_PAGE_VALIDATE_ERROR: {
      return {
        ...state,
        errors: action.errors
      }
    }
    default:
      return state;
  }
}