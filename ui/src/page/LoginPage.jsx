import React from 'react';
import PropTypes from 'prop-types';

import UserStatusContainer from '../component/container/UserStatusContainer';
import LoginValidationForm from '../form/login/LoginValidationForm';

class LoginPage extends React.Component{

  static propTypes = {
    currentPath: PropTypes.string.isRequired
  };

  render() {
    return (
      <UserStatusContainer guarantee={UserStatusContainer.GUARANTEE_NOT_LOGGED_IN}
                           redirectPath="/"
                           currentPath={this.props.currentPath}
      >
        <LoginValidationForm/>
      </UserStatusContainer>
    )
  }
}

export default LoginPage;