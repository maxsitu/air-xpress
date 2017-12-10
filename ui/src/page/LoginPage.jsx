import React from 'react';
import PropTypes from 'prop-types';

import UserStatusContainer from '../component/container/UserStatusContainer';
import LoginValidationForm from '../form/login/LoginValidationForm';
import Navbar from '../component/Navbar';

class LoginPage extends React.Component{

  static propTypes = {
    currentPath: PropTypes.string.isRequired
  };

  render() {
    return (
      <div>
        <Navbar/>
        <UserStatusContainer guarantee={UserStatusContainer.GUARANTEE_NOT_LOGGED_IN}
                             redirectPath="/"
                             currentPath={this.props.currentPath}>
          <LoginValidationForm/>
        </UserStatusContainer>
      </div>

    )
  }
}

export default LoginPage;