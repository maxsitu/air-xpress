import UserStatusActionType from '../action/UserStatusActionType'

export function userStatus(state = {
  isLoggedIn: false
}, action) {

  switch (action.type) {
    case UserStatusActionType.SET_USER_LOGIN:
      return Object.assign({}, {
        isLoggedIn: action.value
      }, state);

    default:
      return state;
  }
}