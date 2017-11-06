import React from 'react';
import {navigateTo} from '../../state/action';
import {connect} from 'react-redux';
import {Route} from 'react-router-dom'
import EnsureLoggedInContainer from './UserStatusContainer';
import LoginValidationForm from '../../form/login/LoginValidationForm';
import SignUpForm from '../../form/signup/SignUpForm';


import {setRedirectPath} from '../../state/action';

const loginForm = ({match, location, history}) => {
  return <LoginValidationForm location={location}/>;
};

const loggedInContainer = ({location, history}) => {
  return <EnsureLoggedInContainer loginPath="/login" location={location}/>;
};

const signUpForm = ({location}) => {
  return <SignUpForm location={location}/>
};

class AppContainer extends React.Component {
  static DEFAULT_REDIRECT_PATH = '/order';
  static DEFAULT_LOGIN_PATH = '/login';

  componentDidUpdate(prevProps) {
    const { dispatch, redirectPath, history} = this.props;
    const isLoggingOut = prevProps.isLoggedIn && !this.props.isLoggedIn;
    const isLoggingIn = !prevProps.isLoggedIn && this.props.isLoggedIn;

    if (isLoggingIn) {
      dispatch(setRedirectPath(null));
      dispatch(navigateTo(redirectPath || AppContainer.DEFAULT_REDIRECT_PATH))
    } else if (isLoggingOut) {
      // do any kind of cleanup or post-logout redirection here
      dispatch(navigateTo(AppContainer.DEFAULT_LOGIN_PATH));
    } else if (this.props.navigatePath) {
      dispatch(navigateTo(null));
      history.replace(this.props.navigatePath);
    }
  }

  render() {
    return (
      <div>
        <Route path="/login" component={loginForm}/>
        <Route path="/signup" component={signUpForm}/>
        <Route component={loggedInContainer}/>
      </div>);
  }
}

function mapStateToProps(state) {
  return {
    isLoggedIn: state.isLoggedIn,
    redirectPath: state.redirectPath,
    navigatePath: state.navigatePath
  }
}

export default connect(mapStateToProps)(AppContainer)