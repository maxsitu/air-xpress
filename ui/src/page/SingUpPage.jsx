import React from 'react';
import PropTypes from 'prop-types';

import UserStatusContainer from '../component/container/UserStatusContainer';
import Navbar from '../component/Navbar';
import SignUpForm from '../form/signup/SignUpForm';

class SignUpPage extends React.Component{
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
          <SignUpForm/>
        </UserStatusContainer>
      </div>

    )
  }
}

export default SignUpPage;