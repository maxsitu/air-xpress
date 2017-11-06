import {reducer as reduxFormReducer} from 'redux-form';


import {userStatus} from './UserStatusReducer';
import {navigation} from './NavigationReducer';

export default {
  form: reduxFormReducer,
  userStatus,
  navigation
}