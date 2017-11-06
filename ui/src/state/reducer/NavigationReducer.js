import NavigationActionType from '../action/NavigationActionType'

export function navigation(state = {
    lastNavPath: null
  }, action) {

    switch (action.type) {
      case NavigationActionType.SET_LAST_NAV_PATH:
        return Object.assign({}, {
          lastNavPath: action.value
        }, state);

      case NavigationActionType.UNSET_LAST_NAV_PATH:
        return Object.assign({}, {
          lastNavPath: null
        }, state);

      default:
        return state;
    }
};