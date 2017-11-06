import UserStatusActionType from './UserStatusActionType';

function setUserStatusIsLoggedIn(isLoggedIn) {
  return {
    type: UserStatusActionType.SET_USER_LOGIN,
    value: isLoggedIn
  }
}

export default {
  setUserStatusIsLoggedIn
};