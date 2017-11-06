import NavigationActionType from './NavigationActionType';

function setNavigationLastNavPath(path) {
  return {
    type: NavigationActionType.SET_LAST_NAV_PATH,
    value: path
  }
}

function unsetNavigationLastNavPath() {
  return {
    type: NavigationActionType.UNSET_LAST_NAV_PATH
  }
}

export default {
  setNavigationLastNavPath,
  unsetNavigationLastNavPath
};