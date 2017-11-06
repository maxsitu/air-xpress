import React from 'react';
import {connect} from 'react-redux';
import {Redirect} from 'react-router-dom';
import PropTypes from 'prop-types';

import {NavigationAction} from '../../state/action';

class UserStatusContainer extends React.Component {
  static GUARANTEE_LOGGED_IN = 'logged_in';
  static GUARANTEE_NOT_LOGGED_IN = 'not_logged_in';

  static propTypes = {
    lastNavPath: PropTypes.string,
    currentPath: PropTypes.string.isRequired,
    guarantee: PropTypes.oneOf(
      [
        UserStatusContainer.GUARANTEE_LOGGED_IN,
        UserStatusContainer.GUARANTEE_NOT_LOGGED_IN]).isRequired,
    redirectPath: PropTypes.string,
    isLoggedIn: PropTypes.bool.isRequired,
    updateLastPath: PropTypes.func.isRequired
  };

  componentWillMount() {
    let {guarantee, updateLastPath, lastNavPath, isLoggedIn, currentPath} = this.props;
    let guaranteeLoggedIn = guarantee === UserStatusContainer.GUARANTEE_LOGGED_IN;

    // If current user is not logged in while component requires to be logged in, update
    // last navigate path with the current path.
    guaranteeLoggedIn &&
    !isLoggedIn &&
    lastNavPath !== currentPath &&
    updateLastPath(currentPath);
  }

  render() {
    const {isLoggedIn, guarantee} = this.props;
    let guaranteeLoggedIn = guarantee === UserStatusContainer.GUARANTEE_LOGGED_IN;
    let guaranteeNotLoggedIn = guarantee === UserStatusContainer.GUARANTEE_NOT_LOGGED_IN;

    if (
      (guaranteeLoggedIn && isLoggedIn) ||
      (guaranteeNotLoggedIn && !isLoggedIn)
    ) {
      return this.props.children;
    }

    return <Redirect to={this.props.redirectPath}/>;
  }
}

function mapStateToProps(state, ownProps) {
  return {
    isLoggedIn: state.userStatus.isLoggedIn,
    lastNavPath: state.navigation.lastNavPath
  }
}

function mapDispatchToProps(dispatch) {
  return {
    updateLastPath: (path) => dispatch(NavigationAction.setNavigationLastNavPath(path))
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(UserStatusContainer);
