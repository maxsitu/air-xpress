import NavigationActionType from '../action/NavigationActionType'

export function navigation(state = {
  lastNavPath: null
}, action) {

  switch (action.type) {
    case NavigationActionType.SET_LAST_NAV_PATH:
      return Object.assign({}, state,
        {
          lastNavPath: action.value
        });

    case NavigationActionType.UNSET_LAST_NAV_PATH:
      return Object.assign({},
        state,
        {
          lastNavPath: null
        });

    default:
      return state;
  }
};